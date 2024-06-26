package net.runelite.client.plugins.safers.SetupBuilder;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "<html><font color=#3076DF>[SafeRS] </font>Utilities</html>"
)
public class SetupBuilderPlugin extends Plugin
{
	@Inject private Client client;
	@Inject
	private ClientThread clientThread;

	@Inject private ClientToolbar clientToolbar;
	@Inject
	private ItemManager itemManager; // Inject the ItemManager
	private NavigationButton navButton;
	private SetupBuilderPanel customPanel;

	@Override
	protected void startUp() throws Exception {

		customPanel = new SetupBuilderPanel(this);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/skill_icons/saferssmall.png");

		navButton = NavigationButton.builder()
				.tooltip("Example")
				.icon(icon)
				.priority(1)
				.panel(customPanel)
				.build();

		clientToolbar.addNavigation(navButton);
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(navButton);
		log.info("Example stopped!");
	}

	public void fetchAndDisplayEquippedItems() {
		clientThread.invokeLater(() -> {
			ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

			if (equipment == null) {
				return;
			}

			StringBuilder equippedItemsBuilder = new StringBuilder();
			for (Item item : equipment.getItems()) {
				if (item.getId() != -1) {
					String itemName = client.getItemDefinition(item.getId()).getName();
					equippedItemsBuilder.append(item.getId()).append(" - ").append(itemName).append("\n");
				}
			}

			SwingUtilities.invokeLater(() -> {
				customPanel.updateEquipmentList(equippedItemsBuilder.toString());
			});
		});
	}
	public void fetchAndDisplayPlayerLocation() {
		clientThread.invokeLater(() -> {
			if (client.getLocalPlayer() == null) {
				return;
			}

			WorldPoint location = client.getLocalPlayer().getWorldLocation();
			int x = location.getX();
			int y = location.getY();
			int plane = location.getPlane();
			String locationString =  x + "," + y + "," + plane;

			SwingUtilities.invokeLater(() -> {
				customPanel.updateLocationField(locationString);
			});
		});
	}

	}
