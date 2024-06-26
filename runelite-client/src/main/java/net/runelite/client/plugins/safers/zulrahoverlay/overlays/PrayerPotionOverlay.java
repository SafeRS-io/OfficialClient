package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;

public class PrayerPotionOverlay extends WidgetItemOverlay {

    @Inject
    private Client client;

    @Inject
    private ZulrahConfig config;

    public PrayerPotionOverlay() {
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        int prayerThreshold = config.prayerThreshold();
        PotionItem selectedPotion = config.highlightPotionItemId();
        int[] highlightItemIds = selectedPotion.getItemIdsPrayer();

        // Get the current prayer points of the player
        int playerPrayer = client.getBoostedSkillLevel(Skill.PRAYER);

        // Check if the current item ID matches any ID in the highlightItemIds array
        for (int highlightItemId : highlightItemIds) {
            if (playerPrayer <= prayerThreshold && itemId == highlightItemId) {
                drawHighlight(graphics, itemWidget, config.potionColor());
                break; // Break the loop if a match is found
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
