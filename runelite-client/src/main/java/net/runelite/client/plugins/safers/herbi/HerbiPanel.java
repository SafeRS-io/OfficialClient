package net.runelite.client.plugins.safers.herbi;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class HerbiPanel extends OverlayPanel {

	private final Client client;
	private WorldPoint lastPosition;
	private int lastAnimation = -1;
	private long lastMoveTime;

	@Inject
	public HerbiPanel(Client client) {
		super(null);
		this.client = client;
		setPosition(OverlayPosition.TOP_LEFT);
		this.lastPosition = null;
		this.lastMoveTime = System.currentTimeMillis();
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		panelComponent.getChildren().clear();

		// Set a preferred width for the panel
		int preferredWidth = 130; // Adjust this value as needed

		panelComponent.getChildren().add(TitleComponent.builder()
				.text("SafeRS Herbiboar")
				.color(new Color(0, 128, 192)) // Color #0080C0
				.build());

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return super.render(graphics);
		}

		WorldPoint currentPosition = localPlayer.getWorldLocation();
		int currentAnimation = localPlayer.getAnimation();

		if ((lastPosition != null && !currentPosition.equals(lastPosition)) ||
				(currentAnimation != -1 && currentAnimation != 388) ||
				(currentAnimation != -1 && currentAnimation != 424)) {
			lastMoveTime = System.currentTimeMillis();
		}

		lastPosition = currentPosition;
		lastAnimation = currentAnimation;

		boolean npcInteracting = false;
		List<NPC> npcs = client.getNpcs();
		for (NPC npc : npcs) {
			if (npc.getInteracting() == localPlayer) {
				npcInteracting = true;
				break;
			}
		}

		long timeSinceLastActivity = System.currentTimeMillis() - lastMoveTime;
		boolean isMoving = timeSinceLastActivity <= 1200 && !npcInteracting;

		String status = isMoving ? "Hunting" : "Not Hunting";
		Color statusColor = isMoving ? Color.GREEN : Color.RED;

		panelComponent.getChildren().add(LineComponent.builder()
				.left("Status:")
				.leftColor(Color.WHITE)
				.right(status)
				.rightColor(statusColor)
				.build());

		panelComponent.setPreferredSize(new Dimension(preferredWidth, 0));

		return super.render(graphics);
	}
}
