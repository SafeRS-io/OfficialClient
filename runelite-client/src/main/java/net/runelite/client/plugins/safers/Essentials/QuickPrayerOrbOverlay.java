package net.runelite.client.plugins.safers.Essentials;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class QuickPrayerOrbOverlay extends Overlay {
    private final Client client;
    private final SafeRSConfig config;

    @Inject
    public QuickPrayerOrbOverlay(Client client, SafeRSConfig config) {
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

        // Check if quick prayer is active. The varbit ID for quick prayer status may vary.
        // As of my last update, the varbit for quick prayer status was not directly documented here,
        // but you can find or verify it using RuneLite's developer tools or checking their source/repositories.
        // Assuming we have a valid varbit ID for quick prayer status:
        boolean quickPrayerActive = client.getVarbitValue(Varbits.QUICK_PRAYER) == 1;

        if (quickPrayerActive) {
            return null; // Don't draw the overlay if any prayer is active
        }

        Widget compassWidget = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
        if (compassWidget != null) {
            int x = compassWidget.getCanvasLocation().getX() + compassWidget.getWidth() / 2; // Center of the compass
            int y = compassWidget.getCanvasLocation().getY() + compassWidget.getHeight() / 2; // Center of the compass

            graphics.setColor(new Color(0,200,130)); // Color of the dot
            graphics.fillOval(x, y, 5, 5); // Draw a 5x5 pixel dot
        }

        return null;
    }
}
