package net.runelite.client.plugins.safers.herbi;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

public class NpcTaggingOverlay extends Overlay
{
    private final Client client;
    private final HerbiAfkConfig config;

    @Inject
    private NpcTaggingOverlay(Client client, HerbiAfkConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return null;
        }

        String[] targetNpcNames = {"Herbiboar", "AnotherNpcName"};  // NPC names to highlight
        int maxDistance = 10;
        Color npcColor = new Color(96, 0, 255);  // Highlight color

        for (NPC npc : client.getNpcs())
        {
            String npcName = npc.getName();
            int distanceToNpc = localPlayer.getWorldLocation().distanceTo(npc.getWorldLocation());

            if (npcName != null && Arrays.asList(targetNpcNames).contains(npcName) && distanceToNpc <= maxDistance)
            {
                renderNpcOverlay(graphics, npc, npcColor);
            }
        }

        return null;
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC npc, Color color)
    {
        Shape clickbox = npc.getConvexHull();
        if (clickbox != null)
        {
            Rectangle bounds = clickbox.getBounds();
            int centerX = bounds.x + bounds.width / 2;
            int centerY = bounds.y + bounds.height / 2;
            int squareSize = 5;  // Size of the square
            int halfSquareSize = squareSize / 2;

            graphics.setColor(color);
            graphics.fillRect(centerX - halfSquareSize, centerY - halfSquareSize, squareSize, squareSize);
        }
    }
}

