package net.runelite.client.plugins.safers.ZeahRC;


import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class WorldPointMarkerOverlay extends Overlay {
    private final Client client;
    private final ExampleConfig config;

    private final List<WorldPoint> markedPoints = List.of(
            new WorldPoint(1761,3873,0),
            new WorldPoint(1743,3854,0)

            // Add more points here
    );

    @Inject
    public WorldPointMarkerOverlay(Client client, ExampleConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getLocalPlayer() != null && client.getPlane() == 0) {

            for (WorldPoint point : markedPoints) {
                drawTile(graphics, point);
            }
        }

        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point) {
        if (client.getLocalPlayer() == null || client.getPlane() != point.getPlane()) {
            return;
        }

        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) > 32) {
            return; // the tile is too far away to render
        }

        Tile tile = client.getScene().getTiles()[point.getPlane()][point.getX() - client.getBaseX()][point.getY() - client.getBaseY()];
        if (tile == null) {
            return;
        }

        LocalPoint localPoint = LocalPoint.fromWorld(client, point);
        if (localPoint == null) {
            return;
        }

        Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
        if (canvasPoint != null) {
            int size = 5;
            int halfSize = size / 2;
            Color tileColor = new Color(205,10,25); // Use color from config
            graphics.setColor(tileColor);
            graphics.fillRect(canvasPoint.getX() - halfSize, canvasPoint.getY() - halfSize, size, size);
        }
    }
}

