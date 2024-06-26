package net.runelite.client.plugins.safers.rooftopagility;

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
    private final NomAgilityConfig config;
    private final List<WorldPoint> markedPoints = Arrays.asList(
            new WorldPoint(2625, 3677, 0),
            new WorldPoint(3103, 3279, 0),
            new WorldPoint(3295, 3188, 0),
            new WorldPoint(3273, 3195, 0),  // New poi// New point, replace 1234, 5678, 0 with the actual coordinates
            new WorldPoint(3210, 3407, 0),  // New poi// New point, replace 1234, 5678, 0 with the actual coordinates
            new WorldPoint(3036, 3340, 0),  // New poi// New point, replace 1234, 5678, 0 with the actual coordinates
            new WorldPoint(2729, 3488, 0),  // New poi// New point, replace 1234, 5678, 0 with the actual coordinates
            new WorldPoint(2669,3298,0),
            new WorldPoint(3251,6109,0),
            new WorldPoint(3502,3488,0),
            new WorldPoint(3356, 2965, 0)  // New poi// New point, replace 1234, 5678, 0 with the actual coordinates

            // Add more points here
    );

    @Inject
    public WorldPointMinimapOverlay(Client client, NomAgilityConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Color minimapColor = new Color(96,0,255); // Get color from config
        for (WorldPoint point : markedPoints) {
            drawOnMinimap(graphics, point, minimapColor);
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

        // Calculate the center of the tile
        Point centerPoint = Perspective.localToMinimap(client, new LocalPoint(x + Perspective.LOCAL_TILE_SIZE / 2, y + Perspective.LOCAL_TILE_SIZE / 2));

        if (centerPoint == null) {
            return;
        }

        // Calculate the top-left corner of the 5x5 square
        int squareSize = 5; // Size of the square
        Point topLeft = new Point(centerPoint.getX() - squareSize / 2, centerPoint.getY() - squareSize / 2);

        // Create a 5x5 square polygon around the center point
        Polygon square = new Polygon();
        square.addPoint(topLeft.getX(), topLeft.getY());
        square.addPoint(topLeft.getX() + squareSize, topLeft.getY());
        square.addPoint(topLeft.getX() + squareSize, topLeft.getY() + squareSize);
        square.addPoint(topLeft.getX(), topLeft.getY() + squareSize);

        Stroke stroke = new BasicStroke(1f);
        graphics.setStroke(stroke);
        graphics.setColor(color);
        graphics.fillPolygon(square); // Fill the square instead of just drawing its outline
    }

}

