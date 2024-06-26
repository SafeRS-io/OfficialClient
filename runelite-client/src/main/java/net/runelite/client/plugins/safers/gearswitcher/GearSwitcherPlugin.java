package net.runelite.client.plugins.safers.gearswitcher;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
		name = "<html><font color=#3076DF>[SafeRS] </font>PK Tools</html>"
)
public class GearSwitcherPlugin extends Plugin {
	@Inject
	private ConfigManager configManager;







	@Inject
	private Client client;

	@Inject
	private GearSwitcherConfig config;

	@Inject
	private AttackOverlay attackOverlay;
	@Inject
	private WeaponTypeOverlay weaponTypeOverlay;
	@Inject
	private OverlayManager overlayManager;


	private final int[] meleeWeaponIds = {23628,20593,20784,21205,20405,20407,20406,4888,24617,28834,28997,4886,20370,21015,1434,28810,-1,11802, 24225, 13652, 1215, 5698, 11838, 4151, 12006, 21003, 4587, 11804, 26233, 26219, 27021,27660,5680,13652,21003,20784, 4755, 27189};
	private final int[] rangedWeaponIds = {23630,23619,20408,23611,23601,28922,22550,28869,25890,20849,25867,25888,25886,9185, 21902, 11785, 26374, 21012, 861, 11235, 4734, 22804, 22810, 868, 5667, 10156,27655,12788,21902,29000,11235,26374,27186};
	private final int[] magicWeaponIds = {23613,23626,25517,23628,20431,23653,24423,24424,28988,4675, 6912, 6914, 4710, 27624, 27665, 11791, 22296, 21006, 12904, 12000, 11998,27690};
	private final Map<Player, Integer> playerWeaponCache = new HashMap<>();

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return;
		}

		// Tag players for monitoring without redundant logging here
		if (event.getSource() == localPlayer && event.getTarget() instanceof Player) {
			// You might log here if desired, but mainly ensure the player is tagged for monitoring
			playerWeaponCache.put((Player) event.getTarget(), -1); // Initialize with -1 to indicate no prior weapon data
		} else if (event.getTarget() == localPlayer && event.getSource() instanceof Player) {
			playerWeaponCache.put((Player) event.getSource(), -1); // Same as above
		}
	}
	@Subscribe
	public void onGameTick(GameTick tick) {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return;
		}

		// If you have a specific player to monitor, replace 'null' with the player object
		// For demonstration, we're checking the local player's interaction target
		Actor target = localPlayer.getInteracting();
		if (target instanceof Player) {
			logPlayerWeaponId((Player) target, "currently wielding during game tick");
		}

		// Optionally, check if any other player is interacting with the local player
		for (Player p : client.getPlayers()) {
			if (p != localPlayer && p.getInteracting() == localPlayer) {
				logPlayerWeaponId(p, "currently interacting with during game tick");
				// Break here if you're only interested in the first player interacting with the local player
				// Remove the break statement if you wish to log all players interacting with the local player
				break;
			}
		}
	}
	private void getWeaponCategoryColor(int weaponId) {
		if (Arrays.stream(meleeWeaponIds).anyMatch(id -> id == weaponId)) {
			weaponTypeOverlay.setDotColor(Color.RED);
		} else if (Arrays.stream(rangedWeaponIds).anyMatch(id -> id == weaponId)) {
			weaponTypeOverlay.setDotColor(Color.GREEN);
		}else if (Arrays.stream(magicWeaponIds).anyMatch(id -> id == weaponId)){
			weaponTypeOverlay.setDotColor(Color.BLUE);
		}
	}

	private void logPlayerWeaponId(Player player, String interactionType) {
		PlayerComposition composition = player.getPlayerComposition();
		int weaponId = composition.getEquipmentId(KitType.WEAPON);
		System.out.println("You are " + interactionType + " player: " + player.getName() + ", Weapon ID: " + weaponId);

		// Make sure this method is called properly
		getWeaponCategoryColor(weaponId);
	}


	@Override
	protected void startUp() throws Exception {

		overlayManager.add(attackOverlay);
		overlayManager.add(weaponTypeOverlay);


		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception {

		overlayManager.remove(attackOverlay);

		overlayManager.remove(weaponTypeOverlay);
		log.info("Example stopped!");
	}


	// Add a getter for the config
	public GearSwitcherConfig getConfig() {
		return config;
	}

	// Add a method to get the highlight color (you can customize this)
	public Color getHighlightColor() {
		return Color.RED; // Change this color as needed
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
			// This event is triggered whenever there's a change in the inventory
			// The overlay should automatically update if it's designed to react to these changes
		}
	}

	private int startingXp = -1;
	private int xpThreshold = 1000; // Example XP gain threshold for showing the overlay

	@Subscribe
	public void onStatChanged(StatChanged event) {
		if (startingXp == -1) {
			startingXp = client.getSkillExperience(event.getSkill());
		} else {
			int currentXp = client.getSkillExperience(event.getSkill());
			if ((currentXp - startingXp) >= xpThreshold) {
				// XP gain threshold has been reached; you could now trigger something like an overlay to show
			}
		}
	}


	@Provides
	GearSwitcherConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GearSwitcherConfig.class);
	}
}