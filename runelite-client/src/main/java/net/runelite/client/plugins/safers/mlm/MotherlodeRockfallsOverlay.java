package net.runelite.client.plugins.safers.mlm;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MotherlodeRockfallsOverlay extends Overlay {
    private final SafeRSMotherlodeConfig config;
    private final Motherlode motherlode;
    private final Client client;

    private final Set<Integer> ROCKFALLS = ImmutableSet.of(26679, 26680);
    private final Set<TileObject> rockfalls = new HashSet<>();

    public MotherlodeRockfallsOverlay(final SafeRSMotherlodeConfig config, final Motherlode motherlode, final Client client) {
        this.config = config;
        this.motherlode = motherlode;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public void onTileObjectSpawned(final TileObject object) {
        if (motherlode.inRegion() && ROCKFALLS.contains(object.getId())) {
            rockfalls.add(object);
        }
    }

    public void onTileObjectDespawned(final TileObject object) {
        if (motherlode.inRegion() && ROCKFALLS.contains(object.getId())) {
            rockfalls.remove(object);
        }
    }

    public void onGameStateChanged(final GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            clear();
        }
    }

    public void clear() {
        rockfalls.clear();
    }

    @Override
    public Dimension render(final Graphics2D graphics) {
        if (!motherlode.inRegion()) return null;

        final Player player = client.getLocalPlayer();
        if (player == null || motherlode.getSack().shouldBeEmptied()) return null;

        TileObject closestRockfall = null;
        int closestDistance = Integer.MAX_VALUE;
        int distancetocheck = 250;
        // Calculate the closest rockfall.
        for (final TileObject rockfall : rockfalls) {
            int distance = player.getLocalLocation().distanceTo(rockfall.getLocalLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestRockfall = rockfall;
            }
        }

        // Check if the closest rockfall is within 1 tile (approx. distance in game units).
        if (!config.upstairsOnly())
        {
            distancetocheck = 500;

        }
        if (closestRockfall != null && closestDistance <= distancetocheck) { // 1 tile is roughly 128 units in RuneScape
            renderTile(graphics, closestRockfall, config.getRockfallsColor());
        }

        return null;
    }


    private void renderTile(final Graphics2D graphics, final TileObject tileObject, final Color color) {
        if (color.getAlpha() == 0) return;

        Shape clickbox = tileObject.getClickbox();
        if (clickbox == null) return;

        Rectangle bounds = clickbox.getBounds();
        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;

        // Since we're drawing a 5x5 square, calculate the top-left corner.
        int squareSize = 5; // Pixel size of each side of the square.
        int halfSquareSize = squareSize / 2;
        int topLeftX = centerX - halfSquareSize;
        int topLeftY = centerY - halfSquareSize;

        try {
            // Set the color for the border of the square and increase alpha for visibility.
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(255, color.getAlpha())));
            graphics.setStroke(new BasicStroke(1));
            graphics.drawRect(topLeftX, topLeftY, squareSize, squareSize);

            // Fill the square with the original color.
            graphics.setColor(color);
            graphics.fillRect(topLeftX, topLeftY, squareSize, squareSize);
        } catch (final Exception ignored) {}
    }

}