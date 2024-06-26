package net.runelite.client.plugins.safers.Essentials;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class CompassDotOverlay extends Overlay {
    private final Client client;
    private final SafeRSConfig config;
    private static final int COMPASS_WIDGET_ID = 10551327; // The specific widget ID for the compass

    @Inject
    public CompassDotOverlay(Client client, SafeRSConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.featureToggle()) {
            return null; // Don't draw anything if the feature is turned off
        }

        // Use the specific widget ID to get the compass widget
        Widget compassWidget = client.getWidget(COMPASS_WIDGET_ID);

        if (compassWidget != null) {
            // Center the dot on the compass widget
            int x = compassWidget.getCanvasLocation().getX() + compassWidget.getWidth() / 2;
            int y = compassWidget.getCanvasLocation().getY() + compassWidget.getHeight() / 2;

            graphics.setColor(new Color(105,52,90)); // Color of the dot
            graphics.fillOval(x - 2, y - 2, 4, 4); // Draw a 4x4 pixel dot, adjust as needed
        }

        return null;
    }
}
