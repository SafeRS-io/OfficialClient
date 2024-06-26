package net.runelite.client.plugins.safers.driftnet;

import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RuneEnergyOverlay extends WidgetItemOverlay {

    @Inject
    private Client client;

    @Inject
    private DriftNetConfig config;

    private final Set<Integer> highlightedItemsGroup1 = new HashSet<>(Arrays.asList(12625,12629,12631,12627));

    @Inject
    public RuneEnergyOverlay(Client client, DriftNetConfig config) {
        this.client = client;
        this.config = config;
        showOnInventory();
    }


    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        int runEnergyThreshold = config.runEnergyThreshold() * 100; // Scale the threshold to match API's scale
        int currentRunEnergy = client.getEnergy();

        if (currentRunEnergy < runEnergyThreshold) {
            if (highlightedItemsGroup1.contains(itemId)) {
                Color tagColor = new Color(0,255,0); // Retrieve color from config
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
}
