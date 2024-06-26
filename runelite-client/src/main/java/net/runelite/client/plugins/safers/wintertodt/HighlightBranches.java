package net.runelite.client.plugins.safers.wintertodt;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class HighlightBranches extends Overlay {
    private final Client client;
    private final SafeRSWintertodtConfig config;
    private static final int HIGHLIGHTED_OBJECT_ID = 29311;
    private static final int EXCLUDED_ITEM_ID_LOG = 20695; // ID of the log item
    private static final int EXCLUDED_ITEM_ID_FLETCHED_LOG = 20696; // ID of the fletched log item

    @Inject
    public HighlightBranches(Client client, SafeRSWintertodtConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!isExcludedItemInLastSlot()) {
            Tile[][][] tiles = client.getScene().getTiles();
            GameObject closestObject = null;
            int closestDistance = Integer.MAX_VALUE;

            for (Tile[][] plane : tiles) {
                for (Tile[] xTiles : plane) {
                    for (Tile tile : xTiles) {
                        if (tile == null) continue;
                        GameObject[] gameObjects = tile.getGameObjects();
                        if (gameObjects != null) {
                            for (GameObject gameObject : gameObjects) {
                                if (gameObject != null && gameObject.getId() == HIGHLIGHTED_OBJECT_ID) {
                                    int distance = gameObject.getLocalLocation().distanceTo(client.getLocalPlayer().getLocalLocation());
                                    if (distance < closestDistance) {
                                        closestDistance = distance;
                                        closestObject = gameObject;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (closestObject != null) {
                renderRectangleAroundObject(graphics, closestObject);
            }
        }

        return null;
    }

    private boolean isExcludedItemInLastSlot() {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory != null) {
            Item[] items = inventory.getItems();
            if (items.length > 0) {
                Item lastItem = items[items.length - 1];
                return lastItem.getId() == EXCLUDED_ITEM_ID_LOG || lastItem.getId() == EXCLUDED_ITEM_ID_FLETCHED_LOG;
            }
        }
        return false;
    }

    private void renderRectangleAroundObject(Graphics2D graphics, GameObject gameObject) {
        if (gameObject.getClickbox() != null) {
            Rectangle rect = gameObject.getClickbox().getBounds();
            int x = (int) rect.getCenterX() - 2;
            int y = (int) rect.getCenterY() - 2;
            graphics.setColor(config.BranchColor());
            graphics.fillRect(x, y, 5, 5);
        }
    }
}
