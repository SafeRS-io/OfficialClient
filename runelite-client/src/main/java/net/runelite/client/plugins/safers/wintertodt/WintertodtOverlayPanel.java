package net.runelite.client.plugins.safers.wintertodt;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class WintertodtOverlayPanel extends OverlayPanel {

	private final Client client;
	private final SafeRSWintertodtConfig config;
	private WorldPoint lastPosition;
	private int lastAnimation = -1;
	private long lastMoveTime;

	@Inject
	public WintertodtOverlayPanel(Client client, SafeRSWintertodtConfig config) {
		super(null);
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.BOTTOM_LEFT);
		this.lastPosition = null;
		this.lastMoveTime = System.currentTimeMillis();
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		panelComponent.getChildren().clear();

		// Set a preferred width for the panel
		int preferredWidth = 140;


		panelComponent.getChildren().add(TitleComponent.builder()
				.text("SafeRS Wintertodt")
				.color(new Color(0, 128, 192))
				.build());

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return super.render(graphics);
		}

		WorldPoint currentPosition = localPlayer.getWorldLocation();
		int currentAnimation = localPlayer.getAnimation();

		if (currentAnimation != -1) {
			lastMoveTime = System.currentTimeMillis();
		} else if (lastPosition != null && !currentPosition.equals(lastPosition)) {
			lastMoveTime = System.currentTimeMillis();
		}

		lastPosition = currentPosition;
		lastAnimation = currentAnimation;

		long timeSinceLastActivity = System.currentTimeMillis() - lastMoveTime;
		boolean isMoving = timeSinceLastActivity <= 400;

		String moveStatus = calculateMoveStatus();
		Color moveStatusColor = isMoving() ? Color.GREEN : Color.RED;

		panelComponent.getChildren().add(LineComponent.builder()
				.left("Status:")
				.leftColor(Color.WHITE)
				.right(moveStatus)
				.rightColor(moveStatusColor)
				.build());

		// Calculate the food status
		String foodStatus = checkFoodInInventory() ? "Found" : "Not Found";
		Color foodStatusColor = checkFoodInInventory() ? Color.GREEN : Color.RED;

		// Add the food status line
		panelComponent.getChildren().add(LineComponent.builder()
				.left("Food:")
				.right(foodStatus)
				.rightColor(foodStatusColor)
				.build());

		// Calculate the panel height dynamically
		int panelHeight = panelComponent.getChildren().size() * 20; // Assuming each component is 20 pixels in height
		panelComponent.setPreferredSize(new Dimension(preferredWidth, panelHeight));

		return super.render(graphics);
	}

	private boolean checkFoodInInventory() {
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory == null) {
			return false;
		}

		FoodItem selectedFood = config.highlightItemId(); // Get selected food item from config
		int[] idsToCheck = selectedFood.getItemIds();

		for (Item item : inventory.getItems()) {
			for (int id : idsToCheck) {
				if (item.getId() == id) {
					return true;
				}
			}
		}

		return false;
	}
	private String calculateMoveStatus() {
		long timeSinceLastActivity = System.currentTimeMillis() - lastMoveTime;
		return timeSinceLastActivity <= 950 ? "Performing" : "Not Performing";
	}
	private boolean isMoving() {
		return calculateMoveStatus().equals("Performing");
	}
}
