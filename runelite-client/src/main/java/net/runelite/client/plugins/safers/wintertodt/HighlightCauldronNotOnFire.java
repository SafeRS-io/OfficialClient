package net.runelite.client.plugins.safers.wintertodt;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class HighlightCauldronNotOnFire extends Overlay {
    private final Client client;
    private final SafeRSWintertodtConfig config;
    private static final int HIGHLIGHTED_OBJECT_ID = 29312;
    private static final int REQUIRED_ITEM_ID = 20696; // ID of the required item in the last inventory slot

    @Inject
    public HighlightCauldronNotOnFire(Client client, SafeRSWintertodtConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        Tile[][][] tiles = client.getScene().getTiles();
        GameObject closestObject = null;
        int closestDistance = Integer.MAX_VALUE;

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
                            if (gameObject != null && gameObject.getId() == HIGHLIGHTED_OBJECT_ID) {
                                WorldPoint objectLocation = gameObject.getWorldLocation();
                                int distance = objectLocation.distanceTo(playerLocation);
                                if (distance <= 7 && distance < closestDistance) {
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
            int x = (int) rect.getCenterX() - 2;
            int y = (int) rect.getCenterY() - 2;
            graphics.setColor(config.CauldronColor2());
            graphics.fillRect(x, y, 5, 5);
        }
    }
}
