package net.runelite.client.plugins.safers.wintertodt;

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
import java.util.HashMap;
import java.util.Map;

public class WorldPointMinimapOverlay extends Overlay {
    private final Client client;
    private final SafeRSWintertodtConfig config;
    private Map<WorldPoint, Color> markedPoints = new HashMap<>();


    @Inject
    public WorldPointMinimapOverlay(Client client, SafeRSWintertodtConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        markedPoints.put(new WorldPoint(1630, 3962, 0), new Color(255,0,255));
        markedPoints.put(new WorldPoint(1640, 3944, 0), new Color(96,0,255));
        markedPoints.put(new WorldPoint(1638, 3988, 0), Color.BLUE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (Map.Entry<WorldPoint, Color> entry : markedPoints.entrySet()) {
            drawOnMinimap(graphics, entry.getKey(), entry.getValue());
        }

        return null;
    }
    private void drawOnMinimap(Graphics2D graphics, WorldPoint point, Color color) {
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

