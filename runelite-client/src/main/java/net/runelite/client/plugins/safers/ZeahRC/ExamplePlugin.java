package net.runelite.client.plugins.safers.ZeahRC;

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
        name = "<html><font color=#3076DF>[SafeRS] </font>Zeah RC</html>"
)
public class ExamplePlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ExampleConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PathfinderOverlay pathfinderOverlay;
    @Inject
    private AltarsOverlay altarsOverlay;
    @Inject
    private WorldPointMarkerOverlay worldPointMarkerOverlay;
    @Inject
    private HighlightMineableRuneStone highlightMineableRuneStone;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(pathfinderOverlay);
        overlayManager.add(altarsOverlay);
        overlayManager.add(highlightMineableRuneStone);
        overlayManager.add(worldPointMarkerOverlay);
        log.info("Example started!");
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(pathfinderOverlay);
        overlayManager.remove(altarsOverlay);

        overlayManager.remove(highlightMineableRuneStone);
        overlayManager.remove(worldPointMarkerOverlay);

        log.info("Example stopped!");
    }


    @Provides
    ExampleConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ExampleConfig.class);
    }
}