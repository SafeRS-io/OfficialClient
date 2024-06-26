package net.runelite.client.plugins.safers.firemaker;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class NearestBankerOverlay extends Overlay {
    private final Client client;

    @Inject
    public NearestBankerOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<NPC> npcs = client.getNpcs();
        NPC nearestBanker = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (NPC npc : npcs) {
            NPCComposition composition = npc.getTransformedComposition();
            if (composition != null && composition.getName().contains("Banker")) {
                WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
                int distance = npc.getWorldLocation().distanceTo(playerLocation);
                if (distance < nearestDistance) {
                    nearestBanker = npc;
                    nearestDistance = distance;
                }
            }
        }

        if (nearestBanker != null) {
            drawBankerOverlay(graphics, nearestBanker);
        }

        return null;
    }

    private void drawBankerOverlay(Graphics2D graphics, NPC banker) {
        Shape clickbox = banker.getConvexHull();
        if (clickbox != null) {
            Rectangle bounds = clickbox.getBounds();
            int centerX = (int) bounds.getCenterX();
            int centerY = (int) bounds.getCenterY();

            int size = 5;
            graphics.setColor(Color.BLUE);
            graphics.fillRect(centerX - size / 2, centerY - size / 2, size, size);
        }
    }
}
