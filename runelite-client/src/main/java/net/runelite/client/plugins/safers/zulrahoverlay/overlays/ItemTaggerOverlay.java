package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ItemTaggerOverlay extends WidgetItemOverlay {

    @Inject
    private Client client;

    @Inject
    private ZulrahConfig config; // Replace with your actual config class

    public ItemTaggerOverlay() {
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        // Group 1
        Set<Integer> highlightedItemsGroup1 = getItemIdsFromConfig(config.itemIDsGroup1());
        if (highlightedItemsGroup1.contains(itemId)) {
            drawHighlight(graphics, itemWidget, config.highlightColorGroup1());
        }

        // Group 2
        Set<Integer> highlightedItemsGroup2 = getItemIdsFromConfig(config.itemIDsGroup2());
        if (highlightedItemsGroup2.contains(itemId)) {
            drawHighlight(graphics, itemWidget, config.highlightColorGroup2());
        }

        // Group 3
        Set<Integer> highlightedItemsGroup3 = getItemIdsFromConfig(config.itemIDsGroup3());
        if (highlightedItemsGroup3.contains(itemId)) {
            drawHighlight(graphics, itemWidget, config.highlightColorGroup3());
        }
    }
    private void drawHighlight(Graphics2D graphics, WidgetItem itemWidget, Color color) {
        Rectangle bounds = itemWidget.getCanvasBounds();

        // Define the size of the square
        int squareSize = 5;

        // Calculate the center position
        int centerX = bounds.x + bounds.width / 2 - squareSize / 2;
        int centerY = bounds.y + bounds.height / 2 - squareSize / 2;

        // Set the color for the square
        graphics.setColor(color);

        // Draw the square at the calculated position
        graphics.fillRect(centerX, centerY, squareSize, squareSize);
    }




    private Set<Integer> getItemIdsFromConfig(String itemIdsString) {
        Set<Integer> itemIds = new HashSet<>();
        if (itemIdsString != null && !itemIdsString.trim().isEmpty()) {
            for (String id : itemIdsString.split(",")) {
                try {
                    itemIds.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException e) {
                    // Handle invalid number formats if necessary
                }
            }
        }
        return itemIds;
    }

}
