package net.runelite.client.plugins.safers.ZeahRC;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class HighlightMineableRuneStone extends Overlay {
    private final Client client;
    private final ExampleConfig config;

    private static final int[] HIGHLIGHTED_OBJECT_IDS = {8981, 10796}; // Now an array of IDs

    @Inject
    public HighlightMineableRuneStone(Client client, ExampleConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget widget = client.getWidget(229, 1);
        boolean widgetConditionMet = widget != null && "The runestone".equals(widget.getText());
        int excludeId = getExcludeId(widget); // Determine which ID to exclude based on the widget text

        Tile[][][] tiles = client.getScene().getTiles();

        for (Tile[][] plane : tiles) {
            for (Tile[] xTiles : plane) {
                for (Tile tile : xTiles) {
                    if (tile == null) continue;
                    for (GameObject gameObject : Optional.ofNullable(tile.getGameObjects()).orElse(new GameObject[0])) {
                        if (gameObject == null) continue;
                        // Highlight all relevant objects, not just the closest one
                        if (isGameObjectRelevant(gameObject.getId(), widgetConditionMet, excludeId)) {
                            renderRectangleAroundObject(graphics, gameObject);
                        }
                    }
                }
            }
        }

        return null; // We do not need to return any specific size as we're drawing directly on the graphics object
    }

    private boolean isGameObjectRelevant(int objectId, boolean widgetConditionMet, int excludeId) {
        if (!widgetConditionMet || excludeId == -1) {
            // If the widget condition is not met or there's no specific ID to exclude, consider all IDs.
            return Arrays.stream(HIGHLIGHTED_OBJECT_IDS).anyMatch(id -> id == objectId);
        } else {
            // If the widget condition is met, exclude the specific ID.
            return Arrays.stream(HIGHLIGHTED_OBJECT_IDS).anyMatch(id -> id == objectId && id != excludeId);
        }
    }

    private int getExcludeId(Widget widget) {
        // Implement logic to determine which object ID to exclude based on the widget's text.
        // For simplicity, return -1 if the widget text does not specify an object to exclude.
        // Example:
        if (widget != null) {
            if ("Text referring to 8981".equals(widget.getText())) {
                return 8981;
            } else if ("Text referring to 10796".equals(widget.getText())) {
                return 10796;
            }
        }
        return -1; // Default case if no specific ID should be excluded
    }
    private void renderRectangleAroundObject(Graphics2D graphics, GameObject gameObject) {
        if (gameObject.getClickbox() != null) {
            Rectangle rect = gameObject.getClickbox().getBounds();
            int centerX = (int) rect.getCenterX();
            int centerY = (int) rect.getCenterY();
            graphics.setColor(new Color(255, 0, 255)); // Use the designated color
            graphics.fillRect(centerX - 2, centerY - 2, 5, 5); // Draws a filled 5x5 square centered on the object
        }
    }

}
