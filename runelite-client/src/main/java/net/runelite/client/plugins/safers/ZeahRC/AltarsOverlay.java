package net.runelite.client.plugins.safers.ZeahRC;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AltarsOverlay extends Overlay {
    private final Client client;
    private final ExampleConfig config;

    private static final Map<Integer, Color> HIGHLIGHTED_OBJECT_COLORS = new HashMap<>();
    static {
        HIGHLIGHTED_OBJECT_COLORS.put(27979, new Color(96, 0, 255)); // Purple color
        HIGHLIGHTED_OBJECT_COLORS.put(27978, Color.BLUE); // Blue color
    }
    @Inject
    public AltarsOverlay(Client client, ExampleConfig config) {
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
                            if (gameObject != null && HIGHLIGHTED_OBJECT_COLORS.containsKey(gameObject.getId())) {
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
            Color objectColor = HIGHLIGHTED_OBJECT_COLORS.getOrDefault(closestObject.getId(), Color.WHITE); // Default color if ID not found
            renderRectangleAroundObject(graphics, closestObject, objectColor);
        }

        return null;
    }

    private void renderRectangleAroundObject(Graphics2D graphics, GameObject gameObject, Color color) {
        if (gameObject.getClickbox() != null) {
            Rectangle rect = gameObject.getClickbox().getBounds();
            int x = (int) rect.getCenterX() - 2; // Centering the 5x5 square
            int y = (int) rect.getCenterY() - 2; // Centering the 5x5 square
            graphics.setColor(color); // Use the specified color
            graphics.fillRect(x, y, 8, 8); // Draw a 5x5 square
        }
    }
}
