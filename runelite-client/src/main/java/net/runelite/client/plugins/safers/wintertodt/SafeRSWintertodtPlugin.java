package net.runelite.client.plugins.safers.wintertodt;

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
	name = "<html><font color=#3076DF>[SafeRS] </font>Wintertodt</html>"
)
public class SafeRSWintertodtPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SafeRSWintertodtConfig config;

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private HighlightBankStall highlightBankStall;
	@Inject
	private HighlightBigDoor highlightBigDoor;

	@Inject
	private HighlightBranches highlightBranches;
	@Inject
	private HighlightCauldronNotOnFire highlightCauldronNotOnFire;
	@Inject
	private HighlightCauldronOnFire highlightCauldronOnFire;
	@Inject
	private HighlightCauldronBroken highlightCauldronBroken;
	@Inject
	private WorldPointMinimapOverlay worldPointMinimapOverlay;

	@Inject
	private FletchLogTaggerColor fletchLogTaggerColor;
	@Inject
	private LogTaggerColor logTaggerColor;

	@Inject
	private HouseOverlay houseOverlay;
	@Inject
	private WintertodtOverlayPanel wintertodtOverlayPanel;

	@Inject
	private FoodOverlayLowHP foodOverlayLowHP;
	@Inject
	private KnifeTagger knifeTagger;
	@Inject
	private housePoolOverlay housePoolOverlay;
	@Inject
	private TeleBoxOverlay teleBoxOverlay;
	@Inject
	private ConfigManager configManager;


	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(houseOverlay);
		overlayManager.add(housePoolOverlay);
		overlayManager.add(teleBoxOverlay);
		overlayManager.add(wintertodtOverlayPanel);
		overlayManager.add(foodOverlayLowHP);
		overlayManager.add(fletchLogTaggerColor);
		overlayManager.add(highlightBankStall);
		overlayManager.add(highlightCauldronBroken);
		overlayManager.add(highlightBigDoor);
		overlayManager.add(worldPointMinimapOverlay);
		overlayManager.add(highlightBranches);
		overlayManager.add(highlightCauldronNotOnFire);
		overlayManager.add(highlightCauldronOnFire);
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(housePoolOverlay);
		overlayManager.remove(teleBoxOverlay);
		overlayManager.remove(houseOverlay);
		overlayManager.remove(wintertodtOverlayPanel);
		overlayManager.remove(fletchLogTaggerColor);
		overlayManager.remove(foodOverlayLowHP);
		overlayManager.remove(highlightBankStall);
		overlayManager.remove(highlightCauldronBroken);
		overlayManager.remove(highlightBigDoor);
		overlayManager.remove(worldPointMinimapOverlay);
		overlayManager.remove(highlightBranches);
		overlayManager.remove(highlightCauldronNotOnFire);
		overlayManager.remove(highlightCauldronOnFire);
		log.info("Example stopped!");
	}


	@Provides
    SafeRSWintertodtConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SafeRSWintertodtConfig.class);
	}
}
