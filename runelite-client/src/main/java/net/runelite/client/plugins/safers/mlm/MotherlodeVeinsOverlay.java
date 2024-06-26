package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MotherlodeVeinsOverlay extends Overlay {
    private final SafeRSMotherlodeConfig config;
    private final Motherlode motherlode;
    private final MotherlodeVeins veins;
    private final MotherlodeInventory inventory;
    private final MotherlodeSack sack;
    private final Client client;

    private final Set<TileObject> ore_veins = new HashSet<>();

    public MotherlodeVeinsOverlay(final SafeRSMotherlodeConfig config, final Motherlode motherlode, final Client client) {
        this.config = config;
        this.motherlode = motherlode;
        this.inventory = motherlode.getInventory();
        this.sack = motherlode.getSack();
        this.veins = motherlode.getVeins();
        this.client = client;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public void onTileObjectSpawned(final TileObject object) {
        if (motherlode.inRegion() && (veins.isOreVein(object) || veins.isOreVeinDepleted(object))) {
            ore_veins.add(object);
        }
    }

    public void onTileObjectDespawned(final TileObject object) {
        if (motherlode.inRegion() && (veins.isOreVein(object) || veins.isOreVeinDepleted(object))) {
            ore_veins.remove(object);
        }
    }

    public void onGameStateChanged(final GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            clear();
        }
    }

    public void clear() {
        ore_veins.clear();
    }

    @Override
    public Dimension render(final Graphics2D graphics) {
        if (!motherlode.inRegion()) return null;

        final Player player = client.getLocalPlayer();
        if (player == null) return null;

        final int pay_dirt_needed = motherlode.getPayDirtNeeded();
        final int inventory_pay_dirt = inventory.countPayDirt();

        // Inventory is full of pay-dirt or sack should be emptied first.
        if ((pay_dirt_needed == 0 && inventory.countItems() == inventory.getSize()) || sack.shouldBeEmptied()) return null;

        TileObject nearestNonDepletedVein = null;
        double nearestNonDepletedDistance = Double.MAX_VALUE;

        // Find the nearest non-depleted vein.
        for (final TileObject ore_vein : ore_veins) {
            final OreVein vein = veins.getOreVein(ore_vein);
            if (vein == null || (config.upstairsOnly() && vein.sector == Sector.DOWNSTAIRS)) continue;

            double distance = player.getLocalLocation().distanceTo(ore_vein.getLocalLocation());

            // Check if the vein is non-depleted and closer than the current nearest.
            if (!vein.isDepleted() && distance < nearestNonDepletedDistance && distance <= config.getDrawDistance()) {
                nearestNonDepletedVein = ore_vein;
                nearestNonDepletedDistance = distance;
            }
        }

        // Render the nearest non-depleted vein if it exists.
        if (nearestNonDepletedVein != null) {
            renderSquare(graphics, nearestNonDepletedVein, config.getOreVeinsColor(), 1);
        }

        return null;
    }


    private void renderSquare(final Graphics2D graphics, final TileObject object, final Color color, float oreVeinProgress) {
        if (color.getAlpha() == 0) return; // Skip drawing if the color is fully transparent

        try {
            Point canvasPoint = object.getCanvasLocation();
            if (canvasPoint != null) {
                // Calculate the top-left corner of the square to center it on the object's location
                // Assuming the object's location is at the bottom center of the object
                int squareSize = 5; // Size of the square to draw
                int x = canvasPoint.getX() - squareSize / 2; // Adjust to center the square on the X-axis
                int y = canvasPoint.getY() - squareSize / 2; // Adjust to center the square on the Y-axis

                graphics.setColor(color); // Set the color for the square
                graphics.fillRect(x, y, squareSize, squareSize); // Draw the square
            }
        } catch (final Exception ignored) {
            // Exception handling can be more specific based on what you expect to catch
            // For now, any exceptions are ignored
        }
    }

}
