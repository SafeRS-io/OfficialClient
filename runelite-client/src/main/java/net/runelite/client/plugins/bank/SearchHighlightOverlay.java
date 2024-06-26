package net.runelite.client.plugins.bank;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class SearchHighlightOverlay extends Overlay {

    private final Client client;

    @Inject
    public SearchHighlightOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Get the bank's item container widget
        Widget itemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (itemContainer == null) {
            return null; // Bank is not open
        }

        Widget[] items = itemContainer.getDynamicChildren();
        if (items.length == 0) {
            return null; // No items in the bank
        }

        for (Widget item : items) {
            if (!item.isHidden()) {
                // Found the first viewable item, now highlight it
                graphics.setColor(Color.MAGENTA);
                final int squareSize = 5;
                final int x = item.getCanvasLocation().getX() + (item.getWidth() / 2) - (squareSize / 2);
                final int y = item.getCanvasLocation().getY() + (item.getHeight() / 2) - (squareSize / 2);

                graphics.fillRect(x, y, squareSize, squareSize);
                break; // Stop after highlighting the first visible item
            }
        }

        return null;
    }

}

