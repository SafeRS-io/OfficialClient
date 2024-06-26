package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahPlugin;
import net.runelite.client.plugins.safers.zulrahoverlay.constants.StandLocation;
import net.runelite.client.plugins.safers.zulrahoverlay.constants.ZulrahLocation;
import net.runelite.client.plugins.safers.zulrahoverlay.rotationutils.ZulrahNpc;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.awt.*;
import java.util.Iterator;
import java.util.Objects;

public class SceneOverlay extends Overlay
{
	private final Client client;
	private final ZulrahPlugin plugin;
	private final ZulrahConfig config;
	private final SkillIconManager skillIconManager;

	private long lastZulrahTileRenderTime = 0;

	@Inject
	private SceneOverlay(Client client, ZulrahPlugin plugin, ZulrahConfig config, SkillIconManager skillIconManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.skillIconManager = skillIconManager;
		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Font prevFont = graphics.getFont();
		graphics.setFont(config.fontType().getFont());
		if (plugin.getZulrahNpc() != null && !plugin.getZulrahNpc().isDead())
		{
			renderZulrahPhaseTiles(graphics);
			renderStandAndStallTiles(graphics);
			renderZulrahTile(graphics);
		}

		graphics.setFont(prevFont);
		return null;
	}

	private void renderZulrahPhaseTiles(Graphics2D graphics)
	{
		if (config.phaseDisplayType() != ZulrahConfig.DisplayType.OFF && config.phaseDisplayType() != ZulrahConfig.DisplayType.OVERLAY)
		{
			SetMultimap<ZulrahLocation, MutablePair<String, ZulrahNpc>> zulrahLocationsGrouped = LinkedHashMultimap.create();
			plugin.getZulrahData().forEach((data) -> {
				switch (config.phaseDisplayMode())
				{
					case CURRENT:
						data.getCurrentZulrahNpc().ifPresent((npc) -> {
							zulrahLocationsGrouped.put(npc.getZulrahLocation(), new MutablePair<>("Current", npc));
						});
						break;
					case NEXT:
						data.getNextZulrahNpc().ifPresent((npc) -> {
							zulrahLocationsGrouped.put(npc.getZulrahLocation(), new MutablePair<>(getZulrahNextString(), npc));
						});
						break;
					case BOTH:
						data.getCurrentZulrahNpc().ifPresent((npc) -> {
							zulrahLocationsGrouped.put(npc.getZulrahLocation(), new MutablePair<>("Current", npc));
						});
						data.getNextZulrahNpc().ifPresent((npc) -> {
							zulrahLocationsGrouped.put(npc.getZulrahLocation(), new MutablePair<>(getZulrahNextString(), npc));
						});
						break;
					default:
						throw new IllegalStateException("[SceneOverlay] Invalid 'phaseDisplayMode' config state");
				}

			});
			Iterator location = zulrahLocationsGrouped.keys().iterator();

			while (location.hasNext())
			{
				ZulrahLocation zulrahLocation = (ZulrahLocation) location.next();
				int offset = 0;

				for (Iterator groupedLocation = zulrahLocationsGrouped.get(zulrahLocation).iterator(); groupedLocation.hasNext(); offset += graphics.getFontMetrics().getHeight())
				{
					Pair pair = (Pair) groupedLocation.next();
					drawZulrahTile(graphics, (ZulrahNpc) pair.getRight(), (String) pair.getLeft(), offset);
				}
			}
		}
	}

	private String getZulrahNextString()
	{
		return plugin.getCurrentRotation() != null ? "Next" : "P. Next";
	}

	private void drawZulrahTile(Graphics2D graphics, ZulrahNpc zulrahNpc, String addonText, int offset)
	{
		if (zulrahNpc != null)
		{
			LocalPoint localPoint = zulrahNpc.getZulrahLocation().toLocalPoint();

			// Get the center of the tile
			Point centerPoint = Perspective.localToCanvas(client, localPoint, client.getPlane(), 0);

			if (centerPoint != null)
			{
				// Draw a 5x5 rectangle at the center of the tile
				int rectSize = 5;
				int halfRectSize = rectSize / 2;
				Rectangle rect = new Rectangle(centerPoint.getX() - halfRectSize, centerPoint.getY() - halfRectSize, rectSize, rectSize);
				graphics.setColor(new Color(255,0,255));
				graphics.fill(rect);

				// Draw text
				//String text = getZulrahPhaseString(zulrahNpc, addonText);
				//Rectangle2D textBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
				int bx = centerPoint.getX();
				int by = centerPoint.getY();
				//Point textLocation = new Point(bx - (int) textBounds.getWidth() / 2, by - offset);
				ZulrahConfig config = this.config;
				Objects.requireNonNull(config);
				//OverlayUtils.renderTextLocation(graphics, textLocation, text, zulrahNpc.getType().getColor(), config::textOutline);

			}
		}
	}



	private void renderStandAndStallTiles(Graphics2D graphics)
	{
		if (config.standLocations() || config.stallLocations())
		{
			SetMultimap standLocationsGrouped = HashMultimap.create();
			plugin.getZulrahData().forEach((data) -> {
				if (config.standLocations())
				{
					if (data.standLocationsMatch())
					{
						data.getCurrentDynamicStandLocation().ifPresent((loc) -> {
							standLocationsGrouped.put(loc, new MutablePair("Stand / Next", config.standAndNextTileColor()));
						});
					}
					else
					{
						data.getCurrentDynamicStandLocation().ifPresent((loc) -> {
							standLocationsGrouped.put(loc, new MutablePair("Stand", config.standTileColor()));
						});
						data.getNextStandLocation().ifPresent((loc) -> {
							standLocationsGrouped.put(loc, new MutablePair("Next", config.nextTileColor()));
						});
					}
				}

				if (config.stallLocations())
				{
					data.getCurrentStallLocation().ifPresent((loc) -> {
						standLocationsGrouped.put(loc, new MutablePair("Stall", config.stallTileColor()));
					});
					data.getNextStallLocation().ifPresent((loc) -> {
						standLocationsGrouped.put(loc, new MutablePair("Stall / next", config.nextTileColor()));
					});
				}

			});
			Iterator location = standLocationsGrouped.keys().iterator();

			while (location.hasNext())
			{
				StandLocation standLocation = (StandLocation) location.next();
				int offset = 0;

				for (Iterator locationGrouped = standLocationsGrouped.get(standLocation).iterator(); locationGrouped.hasNext(); offset += graphics.getFontMetrics().getHeight())
				{
					Pair pair = (Pair) locationGrouped.next();
					drawTile(graphics, standLocation.toLocalPoint(), (String) pair.getLeft(), (Color) pair.getRight(), offset);
				}
			}

		}
	}

	private void drawTile(Graphics2D graphics, LocalPoint localPoint, String text, Color color, int offset)
	{
		if (localPoint != null && !Strings.isNullOrEmpty(text))
		{
			// Calculate the text location
			//Point textLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, text, 0);
			//Point txtLoc = new Point(textLocation.getX(), textLocation.getY() - offset);
			//Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue());
			ZulrahConfig config = this.config;
			Objects.requireNonNull(config);
			//OverlayUtils.renderTextLocation(graphics, txtLoc, text, color2, config::textOutline);

			// Get the center of the tile
			Point centerPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());

			if (centerPoint != null)
			{
				// Adjust to create a 5x5 rectangle around the center
				int rectSize = 5;
				int halfRectSize = rectSize / 2;
				Rectangle rect = new Rectangle(centerPoint.getX() - halfRectSize, centerPoint.getY() - halfRectSize, rectSize, rectSize);

				// Draw the rectangle
				graphics.setColor(color);
				graphics.fill(rect);
			}
		}
	}




	private void renderZulrahTile(Graphics2D graphics)
	{
		// Check if Zulrah tile display is enabled in the configuration
		if (config.displayZulrahTile())
		{
			NPC zulrahNpc = plugin.getZulrahNpc();

			// Check if Zulrah NPC is not null and its animation is not -1
			if (zulrahNpc != null && (zulrahNpc.getAnimation() != 5072 && zulrahNpc.getAnimation() != 5073))
			{
				LocalPoint localPoint = zulrahNpc.getLocalLocation();

				// Get the center of the tile
				Point centerPoint = Perspective.localToCanvas(client, localPoint, client.getPlane(), 0);

				if (centerPoint != null)
				{
					// Set the size of the rectangle
					int rectSize = 5;
					int halfRectSize = rectSize / 2;

					// Create a 5x5 rectangle around the center point
					Rectangle rect = new Rectangle(centerPoint.getX() - halfRectSize, centerPoint.getY() - halfRectSize, rectSize, rectSize);

					// Set the color and fill the rectangle
					graphics.setColor(config.zulrahTileColor2());
					graphics.fill(rect);
				}
			}
		}
	}


}
