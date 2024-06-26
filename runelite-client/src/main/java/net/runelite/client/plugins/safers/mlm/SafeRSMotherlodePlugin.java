package net.runelite.client.plugins.safers.mlm;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
@PluginDescriptor(
		name = "<html><font color=#3076DF>[SafeRS] </font>Motherlode</html>",
	description = "Better indicators for ore veins",
	tags = { "motherlode", "prospector", "golden", "nugget" }
)
public class SafeRSMotherlodePlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private SafeRSMotherlodeConfig config;

	@Inject
	private OverlayManager overlays;

	@Inject
	private ClientThread client_thread;
	@Inject
	private DepositBoxOverlay depositBoxOverlay;

	@Inject
	private FixedWheelOverlay fixedWheelOverlay;
	@Inject
	private PathfinderOverlay pathfinderOverlay;
	@Inject
	private OreTagger oreTagger;
	@Inject
	private WorldPointMinimapOverlay worldPointMinimapOverlay;
	@Inject
	private StartTileOverlayLower startTileOverlayLower;

	@Provides
	SafeRSMotherlodeConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SafeRSMotherlodeConfig.class);
	}

	private Motherlode motherlode;
	private MotherlodeInventory inventory;
	private MotherlodeSack sack;
	private MotherlodeVeins veins;
	private MotherlodeSackWidget widget_sack;
	private MotherlodeVeinsOverlay overlay_veins;
	private MotherlodeRockfallsOverlay overlay_rockfalls;
	private MotherlodeObjectsOverlay overlay_objects;
	@Inject
	private ConfigManager configManager;


	@Override
	protected void startUp() throws Exception {


		if (motherlode == null) {
			motherlode = new Motherlode(client, config);
			inventory = motherlode.getInventory();
			sack = motherlode.getSack();
			veins = motherlode.getVeins();

			overlay_veins = new MotherlodeVeinsOverlay(config, motherlode, client);
			widget_sack = new MotherlodeSackWidget(config, motherlode, client);
			overlay_rockfalls = new MotherlodeRockfallsOverlay(config, motherlode, client);
			overlay_objects = new MotherlodeObjectsOverlay(config, motherlode);
		}

		client_thread.invokeLater(() -> {
			widget_sack.loadNativeWidget();
			motherlode.updatePayDirtNeeded();
		});
		overlays.add(overlay_veins);
		overlays.add(oreTagger);
		overlays.add(overlay_rockfalls);
		overlays.add(overlay_objects);
		overlays.add(widget_sack);
		overlays.add(depositBoxOverlay);
		overlays.add(fixedWheelOverlay);
		overlays.add(worldPointMinimapOverlay);
		overlays.add(startTileOverlayLower);
		overlays.add(pathfinderOverlay);
	}

	@Override
	protected void shutDown() {
		overlay_veins.clear();
		widget_sack.updateMotherlodeNativeWidget(false);
		overlay_rockfalls.clear();
		overlays.remove(overlay_veins);
		overlays.remove(overlay_rockfalls);
		overlays.remove(overlay_objects);
		overlays.remove(widget_sack);
		overlays.remove(depositBoxOverlay);
		overlays.remove(fixedWheelOverlay);
		overlays.remove(worldPointMinimapOverlay);
		overlays.remove(startTileOverlayLower);
		overlays.remove(pathfinderOverlay);
		overlays.remove(oreTagger);

	}

	@Subscribe
	public void onGameObjectSpawned(final GameObjectSpawned event) {
		overlay_veins.onTileObjectSpawned(event.getGameObject());
		overlay_rockfalls.onTileObjectSpawned(event.getGameObject());
		overlay_objects.onTileObjectSpawned(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(final GameObjectDespawned event) {
		overlay_veins.onTileObjectDespawned(event.getGameObject());
		overlay_rockfalls.onTileObjectDespawned(event.getGameObject());
		overlay_objects.onTileObjectDespawned(event.getGameObject());
	}

	@Subscribe
	public void onGroundObjectSpawned(final GroundObjectSpawned event) {
		overlay_objects.onTileObjectSpawned(event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(final GroundObjectDespawned event) {
		overlay_objects.onTileObjectDespawned(event.getGroundObject());
	}

	@Subscribe
	public void onWallObjectSpawned(final WallObjectSpawned event) {
		veins.onTileObjectSpawned(event.getWallObject());
		overlay_veins.onTileObjectSpawned(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(final WallObjectDespawned event) {
		veins.onTileObjectDespawned(event.getWallObject());
		overlay_veins.onTileObjectDespawned(event.getWallObject());
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event) {
		motherlode.onGameStateChanged(event);
		overlay_veins.onGameStateChanged(event);
		overlay_rockfalls.onGameStateChanged(event);
	}

	@Subscribe
	public void onGameTick(final GameTick event) throws IOException {
		motherlode.onGameTick();
		sack.onGameTick();
		veins.onGameTick();
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event) {
		sack.onVarbitChanged();
	}

	@Subscribe
	public void onWidgetLoaded(final WidgetLoaded event) {
		client_thread.invokeLater(() -> widget_sack.onWidgetLoaded(event));
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event) {
		inventory.onItemContainerChanged(event);
	}

	@Subscribe
	public void onConfigChanged (final ConfigChanged event) throws IOException {
		widget_sack.onConfigChanged(event);

	}
}
