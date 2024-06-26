package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ItemDespawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class LootOverlay extends Overlay {

    private final Client client;
    private final ZulrahConfig config;
    private final Map<LocalPoint, Integer> itemLocations;
    private final int size = 5; // Size of the square

    @Inject
    public LootOverlay(Client client, Map<LocalPoint, Integer> itemLocations, ZulrahConfig config) {
        this.client = client;
        this.itemLocations = itemLocations;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        // Setup a cleanup task, consider appropriate timing
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanupItems();
            }
        }, 0, 5000); // Run every 5 seconds
    }

    private void cleanupItems() {
        if (!client.isInInstancedRegion()) {
            itemLocations.clear();
        }
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned event) {
        LocalPoint location = event.getTile().getLocalLocation();
        if (itemLocations.containsKey(location)) {
            itemLocations.remove(location);
        } else {
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Iterator<Map.Entry<LocalPoint, Integer>> it = itemLocations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LocalPoint, Integer> entry = it.next();
            LocalPoint location = entry.getKey();
            Integer count = entry.getValue();

            if (count == null || count <= 0) {
                it.remove(); // Optionally clean up entries with no items
                continue;
            }

            Point canvasPoint = Perspective.localToCanvas(client, location, client.getPlane());
            if (canvasPoint != null) {
                int halfSize = size / 2;
                graphics.setColor(config.GroundItemColor());
                graphics.fillRect(canvasPoint.getX() - halfSize, canvasPoint.getY() - halfSize, size, size);
            }
        }
        return null;
    }
}
