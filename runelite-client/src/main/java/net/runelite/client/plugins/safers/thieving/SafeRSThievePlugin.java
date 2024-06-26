package net.runelite.client.plugins.safers.thieving;

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
		name = "<html><font color=#3076DF>[SafeRS] </font>Thieving</html>"
)
public class SafeRSThievePlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private NpcTaggingOverlay npcTaggingOverlay;
	@Inject
	private ObjectOverlay objectOverlay;
	@Inject
	private HighlightBankStall hightlightBankStall;
	@Inject
	private SafeRSThievingOverlay safeRSThievingOverlay;
	@Inject
	private PathfinderOverlay pathfinderOverlay;

	@Inject
	private ThieveConfig config;
	@Inject
	private ConfigManager configManager;



	@Override
	protected void startUp() throws Exception
	{

		overlayManager.add(npcTaggingOverlay);
		overlayManager.add(pathfinderOverlay);
		overlayManager.add(objectOverlay);
		overlayManager.add(hightlightBankStall);
		overlayManager.add(safeRSThievingOverlay);

		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(pathfinderOverlay);
		overlayManager.remove(objectOverlay);
		overlayManager.remove(npcTaggingOverlay);
		overlayManager.remove(hightlightBankStall);
		overlayManager.remove(safeRSThievingOverlay);
		log.info("Example stopped!");
	}

	@Provides
    ThieveConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ThieveConfig.class);
	}
}
