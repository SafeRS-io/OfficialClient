package net.runelite.client.plugins.safers.wintertodt;

import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KnifeTagger extends WidgetItemOverlay {

    @Inject
    private Client client;

    @Inject
    private SafeRSWintertodtConfig config;

    private final Set<Integer> highlightedItemsGroup1 = new HashSet<>(Arrays.asList(946,123));

    public KnifeTagger() {
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        Color tagColor = config.KnifeColor();

        if (highlightedItemsGroup1.contains(itemId)) {
            drawHighlight(graphics, itemWidget, tagColor);
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
}
