package net.runelite.client.plugins.safers.morghttpclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

public class TileTracker {
    private static final Logger logger = LoggerFactory.getLogger(TileTracker.class);

    private final Client client;

    public TileTracker(Client client) {
        this.client = client;
    }


    public JsonArray getVisibleTiles(){
        JsonArray visibleTiles = new JsonArray();

        int viewportWidth = client.getViewportWidth();
        int viewportHeight = client.getViewportHeight();
        int xOffset = client.getViewportXOffset();
        int yOffset = client.getViewportYOffset();
        Rectangle gameView = new Rectangle(xOffset, yOffset, viewportWidth, viewportHeight);

        WorldPoint playerLocation = client.isInInstancedRegion()?
                WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()):
                WorldPoint.fromLocal(client, client.getLocalPlayer().getLocalLocation());

        int plane = playerLocation.getPlane();
        int playerX = playerLocation.getX();
        int playerY = playerLocation.getY();

        Tile[][][] tiles = client.getScene().getTiles();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[plane][x][y];
                if (tile == null) continue;

                Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());
                if (poly == null) continue;

                Rectangle bounds = poly.getBounds();
                int centerX = bounds.x + bounds.width / 2;
                int centerY = bounds.y + bounds.height / 2;

                if (gameView.contains(centerX, centerY)) {
                    WorldPoint tileLocation = client.isInInstancedRegion()?
                            WorldPoint.fromLocalInstance(client, tile.getLocalLocation()):
                            WorldPoint.fromLocal(client, tile.getLocalLocation());

                    JsonObject tileData = new JsonObject();
                    tileData.addProperty("canvasX", centerX);
                    tileData.addProperty("canvasY", centerY);
                    tileData.addProperty("worldX", tileLocation.getX());
                    tileData.addProperty("worldY", tileLocation.getY());
                    tileData.addProperty("plane", tileLocation.getPlane());

                    JsonArray groundItems = new JsonArray();

                    List<TileItem> items = tile.getGroundItems();
                    if(items != null && !items.isEmpty()) {
                        for (TileItem item : items) {
                            JsonObject groundItem = new JsonObject();
                            groundItem.addProperty("id", item.getId());
                            groundItem.addProperty("quantity", item.getQuantity());
                            groundItems.add(groundItem);
                        }
                    }
                    tileData.add("groundItems", groundItems);
                    visibleTiles.add(tileData);
                }
            }
        }

        return visibleTiles;
    }
}