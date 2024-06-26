package net.runelite.client.plugins.safers.Essentials;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class NpcTooltipOverlay extends Overlay {

    private final Client client;
    private boolean isHoveringNpc = false;

    @Inject
    public NpcTooltipOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.client = client;
    }

    private String getLeftClickAction() {
        MenuEntry[] menuEntries = client.getMenuEntries();
        if (menuEntries.length > 0) {
            MenuEntry leftClickEntry = menuEntries[menuEntries.length - 1];
            return leftClickEntry.getOption();
        }
        return null;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        String leftClickAction = getLeftClickAction();
        if ("Walk here".equals(leftClickAction)) {
            return null;
        }

        Point mouseCanvasPosition = client.getMouseCanvasPosition();

        isHoveringNpc = false;
        for (NPC npc : client.getNpcs()) {
            checkNpc(npc, mouseCanvasPosition);
        }

        if (isHoveringNpc) {
            renderTooltip(graphics);
        }

        return null;
    }

    private void checkNpc(NPC npc, Point mouseCanvasPosition) {
        if (npc == null) return;

        Shape npcClickbox = npc.getConvexHull();
        if (npcClickbox != null && npcClickbox.contains(mouseCanvasPosition.getX(), mouseCanvasPosition.getY())) {
            isHoveringNpc = true;
        }
    }

    private void renderTooltip(Graphics2D graphics) {
        // You can customize the rendering as needed
        int x = 10;
        int y = 10;
        int squareSize = 5;

        graphics.setColor(new Color(0,65,200)); // Changed color to differentiate from object tooltips
        graphics.fillRect(x, y, squareSize, squareSize);
    }
}
