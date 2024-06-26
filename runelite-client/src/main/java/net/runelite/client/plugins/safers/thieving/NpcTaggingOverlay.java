package net.runelite.client.plugins.safers.thieving;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class NpcTaggingOverlay extends Overlay
{
    private final Client client;
    private final ThieveConfig config;

    @Inject
    private NpcTaggingOverlay(Client client, ThieveConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player localPlayer = client.getLocalPlayer();
        NpcType selectedType = config.selectedNpcType();
        int[] targetNpcIds = selectedType.getNpcIds();
        int maxDistance = config.npcMarkerDistance();

        for (NPC npc : client.getNpcs())
        {
            int npcId = npc.getId();

            for (int targetNpcId : targetNpcIds)
            {
                if (npcId == targetNpcId)
                {
                    int distanceToNpc = localPlayer.getWorldLocation().distanceTo(npc.getWorldLocation());
                    if (distanceToNpc <= maxDistance)
                    {
                        if (config.selectedNpcType() != NpcType.STALLS) {
                            Color npcColor = config.npcColor();
                            renderNpcOverlay(graphics, npc, npcColor);
                            break;
                        }
                        if (config.selectedNpcType() == NpcType.STALLS) {
                            Color npcColor = new Color(0,100,200);
                            renderNpcOverlay(graphics, npc, npcColor);
                            break;
                        }
                    }
                }
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

            graphics.setColor(color);
            graphics.fillRect(centerX - 1, centerY - 1, 3, 3);
        }
    }
}
