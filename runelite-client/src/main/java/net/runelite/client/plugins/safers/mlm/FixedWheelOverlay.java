package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class FixedWheelOverlay extends Overlay {
    private final Client client;
    private final SafeRSMotherlodeConfig config;
    private static final int HIGHLIGHTED_OBJECT_ID = 26671;
    private static final int BROKEN_ID = 26670;

    @Inject
    public FixedWheelOverlay(Client client, SafeRSMotherlodeConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Tile[][][] tiles = client.getScene().getTiles();

        for (Tile[][] plane : tiles) {
            for (Tile[] xTiles : plane) {
                for (Tile tile : xTiles) {
                    if (tile == null) continue;
                    GameObject[] gameObjects = tile.getGameObjects();
                    if (gameObjects != null) {
                        for (GameObject gameObject : gameObjects) {
                            if (gameObject != null) {
                                if (gameObject.getId() == HIGHLIGHTED_OBJECT_ID) {
                                    // Use the color from config for highlighted objects
                                    renderRectangleAroundObject(graphics, gameObject, config.getOreVeinsStoppingColor());
                                } else if (gameObject.getId() == BROKEN_ID) {
                                    // Use a hardcoded color for broken objects
                                    Color hardcodedColor = new Color(105, 15, 54); // Red color for broken objects
                                    renderRectangleAroundObject(graphics, gameObject, hardcodedColor);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private void renderRectangleAroundObject(Graphics2D graphics, GameObject gameObject, Color color) {
        if (gameObject.getClickbox() != null) {
            Rectangle rect = gameObject.getClickbox().getBounds();
            int x = (int) rect.getCenterX() - 2; // Centering the 5x5 square
            int y = (int) rect.getCenterY() - 2; // Centering the 5x5 square
            graphics.setColor(color); // Use color from config
            graphics.fillRect(x, y, 5, 5); // Draw a 5x5 square
        }
    }
}
