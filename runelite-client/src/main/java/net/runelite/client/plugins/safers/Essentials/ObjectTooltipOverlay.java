package net.runelite.client.plugins.safers.Essentials;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class ObjectTooltipOverlay extends Overlay {

    private final Client client;

    @Inject
    public ObjectTooltipOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.client = client;
    }

    private boolean isNotWalkHereOrNPC() {
        MenuEntry[] menuEntries = client.getMenuEntries();
        if (menuEntries.length > 0) {
            MenuEntry topEntry = menuEntries[menuEntries.length - 1];
            boolean notWalkHere = !"Walk here".equals(topEntry.getOption());
            boolean notFromNPC = topEntry.getType() != MenuAction.EXAMINE_NPC; // NPC actions are typically 2000+
            return notWalkHere && notFromNPC;
        }
        return false;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getScene() == null || !isNotWalkHereOrNPC()) {
            return null; // Skip rendering if scene is null or the condition is not met.
        }

        // Render logic when hovering over an object that is not an NPC and is not "Walk here".
        renderTooltip(graphics);

        return null;
    }
    private void renderTooltip(Graphics2D graphics) {
        int x = 10;
        int y = 10;
        int squareSize = 5;

        graphics.setColor(new Color(125, 25, 15));
        graphics.fillRect(x, y, squareSize, squareSize);
    }
}
