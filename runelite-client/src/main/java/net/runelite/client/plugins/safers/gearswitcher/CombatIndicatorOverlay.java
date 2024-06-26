package net.runelite.client.plugins.safers.gearswitcher;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class CombatIndicatorOverlay extends Overlay {
    private final Client client;

    @Inject
    public CombatIndicatorOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getLocalPlayer() == null) {
            return null;
        }

        final Player localPlayer = client.getLocalPlayer();
        final Player interactingPlayer = (Player) localPlayer.getInteracting();

        if (interactingPlayer != null) {
            int animationId = interactingPlayer.getAnimation();
            Color dotColor = determineColorByAnimation(animationId);

            Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
            if (inventoryWidget != null && inventoryWidget.getBounds() != null) {
                Rectangle bounds = inventoryWidget.getBounds();
                // Calculate the dot position to be over the inventory icon
                int dotX = bounds.x + bounds.width - 25; // Adjust these values as needed
                int dotY = bounds.y + 25; // Adjust these values as needed

                // Draw the dot
                graphics.setColor(dotColor);
                graphics.fillOval(dotX, dotY, 10, 10); // Adjust the size as needed
            }
        }

        return null;
    }

    private Color determineColorByAnimation(int animationId) {
        // Arrays of animation IDs for each combat style
        int[] meleeAnimationIds = {/* array of melee animation IDs */};
        int[] rangedAnimationIds = {/* array of ranged animation IDs */};
        int[] magicAnimationIds = {/* array of magic animation IDs */};

        // Check if the animation ID is in the melee animations array
        if (contains(meleeAnimationIds, animationId)) {
            return Color.RED; // Melee
        }
        // Check if the animation ID is in the ranged animations array
        else if (contains(rangedAnimationIds, animationId)) {
            return Color.GREEN; // Ranged
        }
        // Check if the animation ID is in the magic animations array
        else if (contains(magicAnimationIds, animationId)) {
            return Color.BLUE; // Magic
        }

        // Default color if no specific animation is detected
        return Color.GRAY;
    }

    // Helper method to check if an array contains a specific value
    private boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }
}
