package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class DepositBoxOverlay extends Overlay {
    private final Client client;
    private final SafeRSMotherlodeConfig config;
    // Initialize the set with hard-coded object IDs
    private final Set<Integer> cookinglocationIDs = new HashSet<>(Set.of(25937)); // Example IDs

    @Inject
    public DepositBoxOverlay(Client client, SafeRSMotherlodeConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        GameObject closestObject = null;
        int closestDistance = Integer.MAX_VALUE;

        Tile[][][] tiles = client.getScene().getTiles();
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

        for (Tile[][] plane : tiles) {
            for (Tile[] xTiles : plane) {
                for (Tile tile : xTiles) {
                    if (tile == null) continue;
                    GameObject[] gameObjects = tile.getGameObjects();
                    if (gameObjects != null) {
                        for (GameObject gameObject : gameObjects) {
                            if (gameObject != null && cookinglocationIDs.contains(gameObject.getId())) {
                                int distance = gameObject.getLocalLocation().distanceTo(playerLocation);
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

        // Highlight the closest object
        if (closestObject != null) {
            renderSquareAtCenterOfObject(graphics, closestObject);
        }

        return null;
    }

    private void renderSquareAtCenterOfObject(Graphics2D graphics, GameObject gameObject) {
        if (gameObject.getClickbox() != null) { // Add null check here
            Rectangle rect = gameObject.getClickbox().getBounds();
            int centerX = (int) rect.getCenterX();
            int centerY = (int) rect.getCenterY();
            graphics.setColor(new Color(96,0,255)); // Use a dynamic color or config if necessary
            graphics.fillRect(centerX - 2, centerY - 2, 5, 5); // Draw a 5x5 square at the center
        }
    }
}
