package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class housePoolOverlay extends Overlay {
    private final Client client;
    private final ZulrahConfig config;
    private static final int HIGHLIGHTED_OBJECT_ID = 29241;

    @Inject
    public housePoolOverlay(Client client, ZulrahConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
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

        return null;
    }

    private void renderRectangleAroundObject(Graphics2D graphics, GameObject gameObject) {
        if (gameObject.getClickbox() != null) {
            Rectangle rect = gameObject.getClickbox().getBounds();
            int x = (int) rect.getCenterX() - 2; // Centering the 5x5 square
            int y = (int) rect.getCenterY() - 2; // Centering the 5x5 square
            graphics.setColor(new Color(170,200,50)); // Use color from config
            graphics.fillRect(x, y, 5, 5); // Draw a 5x5 square
        }
    }
}
