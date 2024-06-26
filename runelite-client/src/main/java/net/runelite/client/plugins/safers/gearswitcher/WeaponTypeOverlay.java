package net.runelite.client.plugins.safers.gearswitcher;

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

public class WeaponTypeOverlay extends Overlay {

    private final Client client;
    private Color dotColor = new Color(0,0,0,0);
    // Default color

    @Inject
    public WeaponTypeOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);

    }

    public void setDotColor(Color color) {
        System.out.println("Dot color set to: " + color); // Debugging line
        this.dotColor = color;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget inventory = client.getWidget(WidgetInfo.INVENTORY);
        if (inventory != null) {
            int protectFromMissilesVarbit = client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES);
            int protectFromMeleeVarbit = client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE);
            int protectFromMageVarbit = client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MAGIC);

            if (dotColor.equals(Color.GREEN) && protectFromMissilesVarbit == 1) {
                dotColor = new Color(0,0,0,0);
            }
            if (dotColor.equals(Color.RED) && protectFromMeleeVarbit == 1) {
                dotColor = new Color(0,0,0,0);
            }
            if (dotColor.equals(Color.BLUE) && protectFromMageVarbit == 1) {
                dotColor = new Color(0,0,0,0);
            }
            Rectangle bounds = inventory.getBounds();
            graphics.setColor(dotColor);
            // Example: Draw at a fixed position within the inventory. Adjust as needed.
            graphics.fillOval(bounds.x + 160, bounds.y - 20, 10, 10);
        }
        return null;
    }
}
