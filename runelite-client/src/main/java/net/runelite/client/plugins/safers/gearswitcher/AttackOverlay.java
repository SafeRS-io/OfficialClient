package net.runelite.client.plugins.safers.gearswitcher;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class AttackOverlay extends Overlay {
    private final Client client;
    private Player taggedPlayer; // Field to store the tagged player
    private void checkAndClearTaggedPlayer() {
        if (taggedPlayer == null) {
            return;
        }

        // Check if the tagged player is still in the scene
        boolean playerPresent = client.getPlayers().contains(taggedPlayer);
        if (!playerPresent) {
            taggedPlayer = null; // Clear the tagged player if they're no longer present
            return;
        }
    }

    @Inject
    public AttackOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getLocalPlayer() == null) {
            return null;
        }

        final Player localPlayer = client.getLocalPlayer();
        final Actor target = localPlayer.getInteracting();

        // Check if the local player is interacting with a new player and update tagged player
        if (target instanceof Player && (taggedPlayer == null || !target.equals(taggedPlayer))) {
            taggedPlayer = (Player) target; // Update the tagged player when interacting with a new player
        }

        for (Player p : client.getPlayers()) {
            if (p != localPlayer && p.getInteracting() != null && p.getInteracting().equals(localPlayer)) {
                if (taggedPlayer == null) {
                    taggedPlayer = p; // Set the tagged player if there isn't one already.
                    break; // Since we're only setting taggedPlayer if it's null, break after setting it.
                }
            }
        }

// Assuming we have a basic distance check to approximate if a player might be considered "on screen" or within interaction range
        if (taggedPlayer != null) {
            int interactionDistanceThreshold = 15; // Adjust based on your needs
            boolean isPlayerFarAway = taggedPlayer.getWorldLocation().distanceTo(localPlayer.getWorldLocation()) > interactionDistanceThreshold;

            if (isPlayerFarAway) {
                taggedPlayer = null; // Clear the tagged player if they're too far away
            }
        }
        checkAndClearTaggedPlayer();

        // Render the overlay for the tagged player
        if (taggedPlayer != null) {
            // Attempt to find a more centered position on the player's model
            Point textLocation = taggedPlayer.getCanvasTextLocation(graphics, "", 100);

            if (textLocation != null) {
                int x = textLocation.getX() - 2; // Adjust to create a 5x5 square centered
                int y = textLocation.getY() - 2; // Adjust to create a 5x5 square centered

                graphics.setColor(new Color(255, 0, 255)); // Magenta color
                graphics.fillRect(x, y, 5, 5); // Draw the 5x5 square
            }
        }

        return null;
    }
    // Subscribe to chat messages to listen for specific game messages
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {

        String chatText = chatMessage.getMessage();
        if (chatText.contains("Someone else is already fighting " + taggedPlayer.getName())) {
            taggedPlayer = null;
        }
    }
}
