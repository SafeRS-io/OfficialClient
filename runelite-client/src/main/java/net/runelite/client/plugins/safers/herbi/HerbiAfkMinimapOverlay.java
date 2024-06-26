package net.runelite.client.plugins.safers.herbi;

import net.runelite.client.plugins.safers.herbi.QuestHelperTools.DirectionArrow;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class HerbiAfkMinimapOverlay extends Overlay
{

	private final HerbiAfkPlugin plugin;
	private final HerbiAfkConfig config;

	@Inject
	public HerbiAfkMinimapOverlay(HerbiAfkPlugin plugin, HerbiAfkConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInHerbiboarArea())
		{
			return null;
		}

		if (config.showMiniMapArrow() && plugin.getEndLocation() != null)
		{
			DirectionArrow.renderMinimapArrow(graphics, plugin.getClient(), plugin.getEndLocation(), config.getArrowColor());
		}
		return null;
	}
}
