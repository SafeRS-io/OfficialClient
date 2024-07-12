package net.runelite.client.plugins.mouselistener;


import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.MouseEvent;

@Slf4j
@PluginDescriptor(
        name = "Mouse Logger",
        description = "Logs mouse events to the console",
        tags = {"mouse", "logger"}
)
public class MouseLoggerPlugin extends Plugin
{
    @Inject
    private MouseManager mouseManager;

    @Inject
    private EventBus eventBus;

    private MouseAdapter mouseAdapter = new MouseAdapter()
    {
        @Override
        public MouseEvent mouseClicked(MouseEvent e)
        {
            log.info(String.valueOf(e));
            return e;
        }

        @Override
        public MouseEvent mousePressed(MouseEvent e)
        {
            log.info(String.valueOf(e));
            return e;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent e)
        {
            log.info(String.valueOf(e));
            return e;
        }

        @Override
        public MouseEvent mouseMoved(MouseEvent e)
        {
            return e;
        }

        @Override
        public MouseEvent mouseDragged(MouseEvent e)
        {
            log.info(String.valueOf(e));
            return e;

        }
    };

    @Provides
    MouseLoggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MouseLoggerConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        mouseManager.registerMouseListener(mouseAdapter);
    }

    @Override
    protected void shutDown() throws Exception
    {
        mouseManager.unregisterMouseListener(mouseAdapter);
    }
}
