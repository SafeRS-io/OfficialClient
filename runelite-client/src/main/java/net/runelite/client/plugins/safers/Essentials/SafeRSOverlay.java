package net.runelite.client.plugins.safers.Essentials;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class SafeRSOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final SafeRSConfig config;

    @Inject
    public SafeRSOverlay(Client client, SafeRSConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
        panelComponent.setPreferredSize(new Dimension(10, 10));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!config.featureToggle()) {
            return null; // Don't draw anything if the feature is turned off
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget != null) {
            // Replace these with the actual offsets relative to the inventory widget
            int x = inventoryWidget.getCanvasLocation().getX()+35; // Example X offset
            int y = inventoryWidget.getCanvasLocation().getY() -11; // Example Y offset

            graphics.setColor(new Color(223, 218, 136)); // Red color
            graphics.fillRect(x, y, 3, 3); // Drawing a 2x1 pixel dot
        }

        return null;
    }
}
