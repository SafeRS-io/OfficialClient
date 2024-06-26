package net.runelite.client.plugins.safers.thieving;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ObjectOverlay extends Overlay {
    private final Client client;
    private final ThieveConfig config;
    // Initialize the set with hard-coded object IDs
    private final Set<Integer> highlightedObjectIds = new HashSet<>(Set.of(11730, 28823)); // Example IDs

    @Inject
    public ObjectOverlay(Client client, ThieveConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.selectedNpcType() != NpcType.STALLS) {
            return null;
        }

        GameObject closestObject = null;
        int closestDistance = Integer.MAX_VALUE;

        Tile[][][] tiles = client.getScene().getTiles();
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return null; // Early exit if player is null
        }
        WorldPoint playerLocation = localPlayer.getWorldLocation();

        for (Tile[][] plane : tiles) {
            for (Tile[] xTiles : plane) {
                for (Tile tile : xTiles) {
                    if (tile == null) continue;
                    GameObject[] gameObjects = tile.getGameObjects();
                    if (gameObjects != null) {
                        for (GameObject gameObject : gameObjects) {
                            if (gameObject != null && highlightedObjectIds.contains(gameObject.getId())) {
                                WorldPoint objectLocation = gameObject.getWorldLocation();
                                int distance = objectLocation.distanceTo(playerLocation);
                                if (distance <= 2 && distance < closestDistance) {
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
        if (gameObject.getClickbox() != null) {
            Rectangle rect = gameObject.getClickbox().getBounds();
            int centerX = (int) rect.getCenterX();
            int centerY = (int) rect.getCenterY();
            graphics.setColor(new Color(255,0,255)); // Assuming there is a method to get the color
            graphics.fillRect(centerX - 2, centerY - 2, 5, 5); // Draw a 5x5 square at the center
        }
    }
}
