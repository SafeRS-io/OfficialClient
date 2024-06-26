package net.runelite.client.plugins.safers.thieving;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class SafeRSThievingOverlay extends OverlayPanel {

    private final Client client;
    private final ThieveConfig config;

    @Inject
    public SafeRSThievingOverlay(Client client, ThieveConfig config) {
        super();
        this.client = client;
        this.config = config;
        panelComponent.setBackgroundColor(new Color(26, 26, 26)); // Dark gray background
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("SafeRS Thieving")
                .color(new Color(0, 128, 192)) // Color #0080C0
                .build());

        // Status
        String status = checkFoodInInventory() ? "Found" : "Not Found";
        Color statusColor = checkFoodInInventory() ? Color.GREEN : Color.RED;

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Food:")
                .right(status)
                .rightColor(statusColor)
                .build());

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
}
