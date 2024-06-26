package net.runelite.client.plugins.safers.thieving;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class PathfinderOverlay extends Overlay {
    private final Client client;
    private final ThieveConfig config;
    private Instant timerStart1 = null;
    private Instant timerStart2 = null;
    private List<WorldPoint> destinations;

    private int currentDestinationIndex = 0;
    private final long TIMER_DURATION = 2500; // 4 seconds in milliseconds
    private WorldPoint STOP_RENDERING_REGION = new WorldPoint(1752,3854,0);

    private final Color DESTINATION1_COLOR = new Color(255, 0, 255); // Redcolor for destination 1


    @Inject
    public PathfinderOverlay(Client client, ThieveConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.client = client;
        this.config = config;
        this.destinations = Arrays.asList(
                    //after mining first full inv
                    parseWorldPoint("2654,3286,0"), // bank
                    parseWorldPoint("2669,3310,0")); // stall
            // Add more destinations as needed


    }


    @Override
    public Dimension render(Graphics2D graphics) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        boolean hasItem7938 = Arrays.stream(Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems())
                .anyMatch(item -> item.getId() == 7938);
        // Check if the player is in the stop rendering region
        if (isInStopRenderingRegion(playerLocation)) {
            return null;
        }
        if (hasItem7938 && currentDestinationIndex == 0) {
            currentDestinationIndex = destinations.indexOf(parseWorldPoint("1761,3872,0")); // Index of 2nd shortcut
        }
        if (config.selectedNpcType() != NpcType.STALLS){
            currentDestinationIndex = 0;
            return null;
        }

        // Check if there are more destinations to visit
        if (currentDestinationIndex < destinations.size()) {
            WorldPoint currentDestination = destinations.get(currentDestinationIndex);

            // If the player has reached the current destination
            if (playerLocation.distanceTo(currentDestination) <= 10) {
                // Start or continue the timer
                if (timerStart1 == null) {
                    timerStart1 = Instant.now();
                }

                // Check if the timer has exceeded TIMER_DURATION
                if (Duration.between(timerStart1, Instant.now()).toMillis() > TIMER_DURATION) {
                    // Increment destination index and reset the timer
                    currentDestinationIndex = (currentDestinationIndex + 1) % destinations.size();
                    timerStart1 = null;
                } else {
                    // If the timer has not yet exceeded TIMER_DURATION, do not render the next point
                    return null;
                }
            } else {
                // Reset the timer if the player is not at the current destination
                timerStart1 = null;
            }

            // Render the current or next destination
            handleDestination(graphics, playerLocation, currentDestination, timerStart1, DESTINATION1_COLOR);
        } else {
            // Reset to the first destination if the end of the list is reached
            currentDestinationIndex = 0;
        }

        return null;
    }
    private boolean isInStartRenderingRegion(WorldPoint playerLocation) {
        // Implement logic to check if the player is in the defined stop rendering region
        // This can be a simple distance check or more complex depending on the region's definition
        return playerLocation.distanceTo(STOP_RENDERING_REGION) <= 7; // Define 'someThreshold' as appropriate
    }


    private boolean isInStopRenderingRegion(WorldPoint playerLocation) {
        // Implement logic to check if the player is in the defined stop rendering region
        // This can be a simple distance check or more complex depending on the region's definition
        return playerLocation.distanceTo(STOP_RENDERING_REGION) <= 7; // Define 'someThreshold' as appropriate
    }


    private void handleDestination(Graphics2D graphics, WorldPoint playerLocation, WorldPoint destination, Instant timerStart, Color color) {
        if (playerLocation.distanceTo(destination) <= 3 && timerStart != null) {
            if (Duration.between(timerStart, Instant.now()).toMillis() > TIMER_DURATION) {
                return;
            }
        }

        WorldPoint furthestPoint = getFurthestViewablePoint(playerLocation, destination);
        drawMinimapTile(graphics, furthestPoint, color);
    }

    private Instant updateTimer(WorldPoint playerLocation, WorldPoint destination, Instant timerStart) {
        if (playerLocation.distanceTo(destination) <= 5) {
            if (timerStart == null) {
                timerStart = Instant.now();
            }
        } else {
            timerStart = null;
        }
        return timerStart;
    }


    private WorldPoint parseWorldPoint(String input) {
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int plane = Integer.parseInt(parts[2]);
            return new WorldPoint(x, y, plane);
        } catch (Exception e) {
            return null; // Invalid input
        }
    }
    private WorldPoint getFurthestViewablePoint(WorldPoint start, WorldPoint end) {
        if (start.distanceTo(end) <= 25) {
            // If the destination is within 20 tiles, return the destination itself.
            return end;
        }

        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        double distance = start.distanceTo(end);
        double maxViewableDistance = 25; // Maximum viewable distance

        // Calculate the furthest viewable point within 20 tiles
        int x = start.getX() + (int) ((maxViewableDistance / distance) * dx);
        int y = start.getY() + (int) ((maxViewableDistance / distance) * dy);

        return new WorldPoint(x, y, start.getPlane());
    }
    private void drawMinimapTile(Graphics2D graphics, WorldPoint worldPoint, Color color) {
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null) {
            return;
        }

        Point minimapPoint = Perspective.localToMinimap(client, localPoint);
        if (minimapPoint != null) {
            // Set the color for the square
            graphics.setColor(color);

            // Draw a filled 5x5 square. The -2 adjustments center the square on the minimap point.
            graphics.fillRect(minimapPoint.getX() - 2, minimapPoint.getY() - 2, 5, 5);
        }
    }


}
