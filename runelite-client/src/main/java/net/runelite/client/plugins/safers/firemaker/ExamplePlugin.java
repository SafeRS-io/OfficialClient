package net.runelite.client.plugins.safers.firemaker;

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
        name = "<html><font color=#3076DF>[SafeRS] </font>Firemaker</html>"
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
    private WorldPointMarkerOverlay worldPointMarkerOverlay;
    @Inject
    private NearestBankerOverlay nearestBankerOverlay;
    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(nearestBankerOverlay);
        overlayManager.add(worldPointMarkerOverlay);
        log.info("Example started!");
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(nearestBankerOverlay);
        overlayManager.remove(worldPointMarkerOverlay);
        log.info("Example stopped!");
    }

    @Provides
    ExampleConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ExampleConfig.class);
    }
}