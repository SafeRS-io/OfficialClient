package net.runelite.client.plugins.safers.gearswitcher;

import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class SpecOrbOverlay extends Overlay {
    private final Client client;
    private final GearSwitcherConfig config;

    @Inject
    public SpecOrbOverlay(Client client, GearSwitcherConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Correctly accessing the special attack energy.
        // Note: You'll need to ensure you're using the correct VarPlayer constant for special attack energy.
        int specialAttackEnergy = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);

        Widget compassWidget = client.getWidget(WidgetInfo.MINIMAP_SPEC_ORB);
        if (compassWidget != null) {
            int x = compassWidget.getCanvasLocation().getX() + compassWidget.getWidth() / 2; // Center of the compass
            int y = compassWidget.getCanvasLocation().getY() + compassWidget.getHeight() / 2; // Center of the compass

            // Change color based on special attack energy
            if (specialAttackEnergy <= 700) {
                graphics.setColor(new Color(255, 100, 37)); // Red color for 0% special energy
            }
                else {
                graphics.setColor(new Color(100, 100, 200)); // Default color for non-zero special energy
            }

            graphics.fillOval(x, y, 5, 5); // Draw a 5x5 pixel dot
        }

        return null;
    }


}
