package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class WorldPointMinimapOverlay extends Overlay {
    private final Client client;
    private final SafeRSMotherlodeConfig config;
    private final List<WorldPoint> markedPoints = Collections.singletonList(
            new WorldPoint(3726, 5683, 0));
    private final List<WorldPoint> returnmarkedPoints1 = Collections.singletonList(
            new WorldPoint(3735, 5678, 0));
    private final List<WorldPoint> returnmarkedPoints2 = Collections.singletonList(
            new WorldPoint(3730, 5684, 0));
    private final List<WorldPoint> startupperPoint = Collections.singletonList(
            new WorldPoint(3752, 5679, 0));

    @Inject
    public WorldPointMinimapOverlay(Client client, SafeRSMotherlodeConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.upstairsOnly())
        {
            WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

            drawPoints(graphics, startupperPoint, new Color(0x6000FF), playerLocation, 3);

            return null;
        }
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        // For the first set of points
        drawPoints(graphics, markedPoints, new Color(0x6000FF), playerLocation, 1);

        // For the second set of points
        drawPoints(graphics, returnmarkedPoints1, new Color(0xFF00FF), playerLocation, 1);

        // For the third set of points
        drawPoints(graphics, returnmarkedPoints2, new Color(0xFF90FF), playerLocation, 1);

        return null;
    }

    private void drawPoints(Graphics2D graphics, List<WorldPoint> points, Color color, WorldPoint playerLocation, int distance) {
        for (WorldPoint point : points) {
            if (playerLocation.distanceTo(point) > distance) {
                drawOnMinimap(graphics, point, color);
            }
        }
    }

    private void drawOnMinimap(Graphics2D graphics, WorldPoint point, Color color) {
        if (!config.upstairsOnly())
        {
            return;
        }
        if (!point.isInScene(client)) {
            return;
        }

        int x = point.getX() - client.getBaseX();
        int y = point.getY() - client.getBaseY();

        x <<= Perspective.LOCAL_COORD_BITS;
        y <<= Perspective.LOCAL_COORD_BITS;

        Point mp1 = Perspective.localToMinimap(client, new LocalPoint(x, y));
        Point mp2 = Perspective.localToMinimap(client, new LocalPoint(x, y + Perspective.LOCAL_TILE_SIZE));
        Point mp3 = Perspective.localToMinimap(client, new LocalPoint(x + Perspective.LOCAL_TILE_SIZE, y + Perspective.LOCAL_TILE_SIZE));
        Point mp4 = Perspective.localToMinimap(client, new LocalPoint(x + Perspective.LOCAL_TILE_SIZE, y));

        if (mp1 == null || mp2 == null || mp3 == null || mp4 == null) {
            return;
        }

        Polygon poly = new Polygon();
        poly.addPoint(mp1.getX(), mp1.getY());
        poly.addPoint(mp2.getX(), mp2.getY());
        poly.addPoint(mp3.getX(), mp3.getY());
        poly.addPoint(mp4.getX(), mp4.getY());

        Stroke stroke = new BasicStroke(1f);
        graphics.setStroke(stroke);
        graphics.setColor(color);
        graphics.fillPolygon(poly);
        graphics.drawPolygon(poly);
    }
}
