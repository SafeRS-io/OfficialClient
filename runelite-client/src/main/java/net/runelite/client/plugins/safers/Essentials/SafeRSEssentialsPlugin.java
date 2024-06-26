package net.runelite.client.plugins.safers.Essentials;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "SafeRS Essentials"
)
public class SafeRSEssentialsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SafeRSConfig config;

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private SafeRSOverlay safeRSOverlay;

	@Inject
	private ObjectTooltipOverlay objectTooltipOverlay;
	@Inject
	private AttackingOverlay attackingOverlay;

	@Inject
	private CompassDotOverlay compassDotOverlay;
	@Inject
	private QuickPrayerOrbOverlay quickPrayerOrbOverlay;

	@Inject
	private NpcTooltipOverlay npcTooltipOverlay;
	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		overlayManager.add(safeRSOverlay);
		overlayManager.add(objectTooltipOverlay);
		overlayManager.add(npcTooltipOverlay);
		overlayManager.add(attackingOverlay);
		overlayManager.add(compassDotOverlay);
		overlayManager.add(quickPrayerOrbOverlay);

	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		overlayManager.remove(safeRSOverlay);
		overlayManager.remove(objectTooltipOverlay);
		overlayManager.remove(npcTooltipOverlay);
		overlayManager.remove(attackingOverlay);
		overlayManager.remove(compassDotOverlay);
		overlayManager.remove(quickPrayerOrbOverlay);
	}

	@Provides
    SafeRSConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SafeRSConfig.class);
	}
}
