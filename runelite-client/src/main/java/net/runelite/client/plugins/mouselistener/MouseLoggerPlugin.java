package net.runelite.client.plugins.mouselistener;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

@Slf4j
@PluginDescriptor(
        name = "Mouse and Key Logger",
        description = "Logs mouse and key events to the console",
        tags = {"mouse", "key", "logger"}
)
public class MouseLoggerPlugin extends Plugin
{
    @Inject
    private MouseManager mouseManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private EventBus eventBus;

    private MouseAdapter mouseAdapter = new MouseAdapter()
    {
        @Override
        public MouseEvent mouseClicked(MouseEvent e)
        {
            log.info("Mouse Clicked: {}", e);
            return e;
        }

        @Override
        public MouseEvent mousePressed(MouseEvent e)
        {
            log.info("Mouse Pressed: {}", e);
            return e;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent e)
        {
            log.info("Mouse Released: {}", e);
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
            log.info("Mouse Dragged: {}", e);
            return e;
        }
    };

    private KeyListener keyListener = new KeyListener()
    {
        @Override
        public void keyTyped(KeyEvent e)
        {
            log.info("Key Typed: {}", e);
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            log.info("Key Pressed: {}", e);
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            log.info("Key Released: {}", e);
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
        keyManager.registerKeyListener(keyListener);
    }

    @Override
    protected void shutDown() throws Exception
    {
        mouseManager.unregisterMouseListener(mouseAdapter);
        keyManager.unregisterKeyListener(keyListener);
    }
}
