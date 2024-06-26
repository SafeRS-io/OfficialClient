package net.runelite.client.plugins.safers.Essentials;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class AttackingOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final SafeRSConfig config;

    // Define NPC ID lists for different attack styles
    private final List<Integer> magicNpcs = List.of(1672);
    /* melee NPCs IDs here */
    private final List<Integer> meleeNpcs = List.of(1677,1674,1673,1676,1678,1679,1687);
    private final List<Integer> rangedNpcs = List.of(1675);

    @Inject
    public AttackingOverlay(Client client, SafeRSConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
        panelComponent.setPreferredSize(new Dimension(5, 5)); // Adjust size as needed
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.featureToggle()) {
            return null; // Don't draw anything if the feature is turned off
        }

        if (!config.featureToggle2())
        {
            return null;
        }

        Player player = client.getLocalPlayer();
        if (player == null) {
            return null; // No local player found
        }

        boolean isProtectFromMagicActive = client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MAGIC) == 1;
        boolean isProtectFromMissilesActive = client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 1;
        boolean isProtectFromMeleeActive = client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 1;

        NPC interactingNpc = null;

        // Loop to find if any NPC is currently attacking the player and the player is also interacting with the NPC
        for (NPC npc : client.getNpcs()) {
            if (npc.getInteracting() != null && npc.getInteracting().equals(player) &&
                    player.getInteracting() != null && player.getInteracting().equals(npc)) {
                interactingNpc = npc; // Found a mutually interacting NPC
                break;
            }
        }

        Color overlayColor = null;

        // Determine the overlay color based on NPC type and whether the corresponding protection prayer is active
        if (interactingNpc != null) {
            int npcId = interactingNpc.getId();
            if (magicNpcs.contains(npcId)) {
                overlayColor = isProtectFromMagicActive ? null : new Color(0, 43, 226); // Blue for magic
            } else if (meleeNpcs.contains(npcId)) {
                overlayColor = isProtectFromMeleeActive ? null : new Color(220, 20, 0); // Red for melee
            } else if (rangedNpcs.contains(npcId)) {
                overlayColor = isProtectFromMissilesActive ? null : new Color(0, 128, 0); // Green for ranged
            }
        } else {
            // Show reminders for active prayers even if no NPC is currently interacting
            if (isProtectFromMagicActive) {
                overlayColor = new Color(0, 43, 226); // Blue
            } else if (isProtectFromMissilesActive) {
                overlayColor = new Color(0, 128, 0); // Green
            } else if (isProtectFromMeleeActive) {
                overlayColor = new Color(220, 20, 0); // Red
            }
        }

        // Drawing the fixed position overlay near the prayer icon
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget != null) {
            Point inventoryWidgetLocation = inventoryWidget.getCanvasLocation();
            if (inventoryWidgetLocation != null) {
                int x = inventoryWidgetLocation.getX() + 158; // Adjust as necessary
                int y = inventoryWidgetLocation.getY() - 20; // Adjust as necessary
                if (overlayColor != null) {
                    graphics.setColor(overlayColor);
                    graphics.fillRect(x, y, panelComponent.getPreferredSize().width, panelComponent.getPreferredSize().height);
                }
            }
        }

        // Tagging the mutually interacting NPC with a magenta square
        if (interactingNpc != null && overlayColor != null) {
            Shape clickbox = interactingNpc.getConvexHull();
            if (clickbox != null) {
                Rectangle bounds = clickbox.getBounds();
                int centerX = bounds.x + bounds.width / 2 - 2; // Adjust for center and size of square
                int centerY = bounds.y + bounds.height / 2 - 2;
                graphics.setColor(new Color(255, 0, 255)); // Magenta for tagging
                graphics.fillRect(centerX, centerY, 5, 5); // Draw the square
            }
        }

        return null;
    }



}
