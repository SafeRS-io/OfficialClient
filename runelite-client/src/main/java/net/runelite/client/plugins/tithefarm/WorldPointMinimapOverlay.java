package net.runelite.client.plugins.tithefarm;

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
import java.util.Arrays;
import java.util.List;

public class WorldPointMinimapOverlay extends Overlay {
    private final Client client;
    private final TitheFarmPluginConfig config;
    private final List<WorldPoint> markedPoints = Arrays.asList(
            new WorldPoint(1813, 3488, 0),
            new WorldPoint(14181, 1048, 0)
    );

    @Inject
    public WorldPointMinimapOverlay(Client client, TitheFarmPluginConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }
    private void drawInstancePointTithe(Graphics2D graphics) {
        LocalPoint testPoint = new LocalPoint(7872, 5312);
        Point minimapPoint = Perspective.localToMinimap(client, testPoint);
        if (minimapPoint != null) {
            graphics.setColor(new Color(255, 0, 255));  // Magenta color
            int size = 5;  // Size of the square
            graphics.fillRect(minimapPoint.getX() - size / 2, minimapPoint.getY() - size / 2, size, size);
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        drawInstancePointTithe(graphics); // Call this method to draw the test point

        Color minimapColor = new Color(255, 0, 255); // Get color from config
        for (WorldPoint point : markedPoints) {
            drawOnMinimap(graphics, point, minimapColor);
        }

        return null;
    }

    private void drawOnMinimap(Graphics2D graphics, WorldPoint point, Color color) {
        if (client.isInInstancedRegion()) {
            // Convert the world point to a local point within the instance
            LocalPoint localPoint = LocalPoint.fromWorld(client, point.getX(), point.getY());
            if (localPoint == null) {
                return; // The world point is not within the currently loaded instance region
            }

            // Adjust local point to minimap points
            drawPolygonOnMinimap(graphics, localPoint, color);
        } else {
            // Handling for non-instance areas (working as per your current setup)
            if (!point.isInScene(client)) {
                return;
            }

            int baseX = point.getX() - client.getBaseX();
            int baseY = point.getY() - client.getBaseY();

            LocalPoint localPoint = new LocalPoint(baseX << Perspective.LOCAL_COORD_BITS, baseY << Perspective.LOCAL_COORD_BITS);
            drawPolygonOnMinimap(graphics, localPoint, color);
        }
    }

    private void drawPolygonOnMinimap(Graphics2D graphics, LocalPoint localPoint, Color color) {
        Point mp1 = Perspective.localToMinimap(client, localPoint);
        Point mp2 = Perspective.localToMinimap(client, new LocalPoint(localPoint.getX(), localPoint.getY() + Perspective.LOCAL_TILE_SIZE));
        Point mp3 = Perspective.localToMinimap(client, new LocalPoint(localPoint.getX() + Perspective.LOCAL_TILE_SIZE, localPoint.getY() + Perspective.LOCAL_TILE_SIZE));
        Point mp4 = Perspective.localToMinimap(client, new LocalPoint(localPoint.getX() + Perspective.LOCAL_TILE_SIZE, localPoint.getY()));

        if (mp1 == null || mp2 == null || mp3 == null || mp4 == null) {
            return; // The points are outside the minimap view
        }

        Polygon poly = new Polygon();
        poly.addPoint(mp1.getX(), mp1.getY());
        poly.addPoint(mp2.getX(), mp2.getY());
        poly.addPoint(mp3.getX(), mp3.getY());
        poly.addPoint(mp4.getX(), mp4.getY());

        graphics.setStroke(new BasicStroke(1f));
        graphics.setColor(color);
        graphics.fillPolygon(poly);
        graphics.drawPolygon(poly);
    }


}

