package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class recoiloverlay extends WidgetItemOverlay {

    @Inject
    private Client client;

    @Inject
    private ZulrahConfig config;

    private final Set<Integer> highlightedItemsGroup1 = new HashSet<>(Arrays.asList(2550, 2551)); // tags antidotes and venoms

    public recoiloverlay() {
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (isRingSlotEmpty()) {
            Color tagColor = config.recoilcolor()   ;

            if (highlightedItemsGroup1.contains(itemId)) {
                drawHighlight(graphics, itemWidget, tagColor);
            }
        }
    }

    private void drawHighlight(Graphics2D graphics, WidgetItem itemWidget, Color color) {
        Rectangle bounds = itemWidget.getCanvasBounds();
        int squareSize = 5;
        int centerX = bounds.x + bounds.width / 2 - squareSize / 2;
        int centerY = bounds.y + bounds.height / 2 - squareSize / 2;
        graphics.setColor(color);
        graphics.fillRect(centerX, centerY, squareSize, squareSize);
    }

    private boolean isRingSlotEmpty() {
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipment == null) {
            return true;
        }
        int ringSlotIndex = EquipmentInventorySlot.RING.getSlotIdx();
        return equipment.getItems()[ringSlotIndex].getId() == -1; // -1 indicates empty slot
    }
}
