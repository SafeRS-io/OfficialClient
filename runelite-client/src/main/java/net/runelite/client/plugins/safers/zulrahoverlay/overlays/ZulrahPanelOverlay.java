package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahConfig;
import net.runelite.client.plugins.safers.zulrahoverlay.ZulrahPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class ZulrahPanelOverlay extends OverlayPanel {

    private final Client client;
    private boolean isBossSpawned = false;
    private final ZulrahConfig config;
    private WorldPoint lastPosition;
    private int lastAnimation = -1;
    long lastAttackTime = 0;
    long lastMoveTime = 0;


    final long MOVEMENT_DELAY = 1500;  // 1500 milliseconds for movement
    final long ATTACK_DELAY = 2200;    // 3500 milliseconds for attacking
    long lastActionTime = 0;

    String lastAction = "None"; // Possible values: "None", "Moving", "Attacking"


    public void setBossSpawned(boolean spawned) {
        this.isBossSpawned = spawned;
    }

    // Example attacking animation IDs
    private static final int[] ATTACKING_ANIMATIONS = {
            7045, 1658, // Melee attacks
            426, 427,
            5061,// Ranged attacks
            1167, 812, // Magic attacks
            // Add or remove animations as needed
    };
    private String getJadStatus() {
        if (zulrahPlugin == null) {
            return "Unknown"; // Default case or when zulrahPlugin is not available
        }

        boolean isJad = ZulrahPlugin.isFlipPhasePrayer();
        return isJad ? "Yes" : "No";
    }
    private String getHealthStatus() {
        int playerHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int lowHpThreshold = config.lowHpThreshold();

        return playerHp > lowHpThreshold ? "Healthy" : "Not Healthy";
    }

    @Inject
    public ZulrahPanelOverlay(Client client, ZulrahConfig config) { // Inject ZulrahPlugin
        super(null);
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.lastPosition = null;
        this.lastMoveTime = System.currentTimeMillis();


        panelComponent.setPreferredSize(new Dimension(150, 0)); // Width set to 150, height will adjust automatically

    }

    @Inject
    private ZulrahPlugin zulrahPlugin;

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("SafeRS Zulrah")
                .color(new Color(0, 128, 192)) // Color #0080C0
                .build());

        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return super.render(graphics);
        }

        WorldPoint wp = localPlayer.getWorldLocation();
        int tileX = wp.getX();
        int tileY = wp.getY();
        int z = client.getPlane();

        // Handle instanced areas
        if (client.isInInstancedRegion()) {
            int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
            LocalPoint localPoint = localPlayer.getLocalLocation();
            int chunkData = instanceTemplateChunks[z][localPoint.getSceneX() / 8][localPoint.getSceneY() / 8];

            // Recalculate tileX and tileY based on instance chunk data
            tileX = (chunkData >> 14 & 0x3FF) * 8 + (tileX % 8);
            tileY = (chunkData >> 3 & 0x7FF) * 8 + (tileY % 8);
        }

        int regionX = tileX / 64;
        int regionY = tileY / 64;
        int regionID = (regionX << 8) | regionY;

        // Display the region ID
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Region ID:")
                .leftColor(Color.WHITE)
                .right(Integer.toString(regionID))
                .rightColor(Color.YELLOW)  // Use a color that suits your overlay's theme
                .build());

        String bossStatus = isBossSpawned ? "Spawned" : "Not Spawned";
        Color bossStatusColor = isBossSpawned ? Color.WHITE : Color.CYAN;

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Boss:")
                .leftColor(Color.WHITE)
                .right(bossStatus)
                .rightColor(bossStatusColor)
                .build());

        if (client.getGameState() == GameState.LOGGED_IN) {
            String healthStatus = getHealthStatus();
            Color healthColor = healthStatus.equals("Healthy") ? Color.GREEN : Color.RED;

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Health:")
                    .leftColor(Color.WHITE)
                    .right(healthStatus)
                    .rightColor(healthColor)
                    .build());
            String jadStatus = getJadStatus();
            Color jadColor = jadStatus.equals("Yes") ? Color.GREEN : Color.RED;

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Jad:")
                    .leftColor(Color.WHITE)
                    .right(jadStatus)
                    .rightColor(jadColor)
                    .build());
        }


        return super.render(graphics);
    }

}
