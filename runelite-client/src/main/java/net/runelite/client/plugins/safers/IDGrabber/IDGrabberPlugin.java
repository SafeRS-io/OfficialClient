/*
 * Copyright (c) 2018 kulers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.safers.IDGrabber;

import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

@PluginDescriptor(
	name = "<html><font color=#3076DF>[SafeRS] </font>ID Grabber</html>",
	description = "Add the ability to grab IDs from NPCs, Objects, and Items",
	tags = {"ID", "items", "objects", "npc"},
	enabledByDefault = true
)
@Slf4j
public class IDGrabberPlugin extends Plugin
{
	private static final String ITEM_KEY_PREFIX = "item_";
	private static final String TAG_KEY_PREFIX = "tag_";

	@Inject
	private Client client;


	@Inject
	private ConfigManager configManager;


	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Gson gson;

	@Inject
	private ColorPickerManager colorPickerManager;

	@Provides
    IDGrabberConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IDGrabberConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		// Perform HWID validation
		// If HWID is valid, proceed with the plugin's start up procedures
		convertConfig();
		// Any other start up l
	}

	@Override
	protected void shutDown()
	{
	}

	Tag getTag(int itemId)
	{
		String tag = configManager.getConfiguration(IDGrabberConfig.GROUP, TAG_KEY_PREFIX + itemId);
		if (tag == null || tag.isEmpty())
		{
			return null;
		}

		return gson.fromJson(tag, Tag.class);
	}

	void setTag(int itemId, Tag tag)
	{
		String json = gson.toJson(tag);
		configManager.setConfiguration(IDGrabberConfig.GROUP, TAG_KEY_PREFIX + itemId, json);
	}

	void unsetTag(int itemId)
	{
		configManager.unsetConfiguration(IDGrabberConfig.GROUP, TAG_KEY_PREFIX + itemId);
	}

	private void convertConfig()
	{
		String migrated = configManager.getConfiguration(IDGrabberConfig.GROUP, "migrated");
		if (!"1".equals(migrated))
		{
			return;
		}

		int removed = 0;
		List<String> keys = configManager.getConfigurationKeys(IDGrabberConfig.GROUP + "." + ITEM_KEY_PREFIX);
		for (String key : keys)
		{
			String[] str = key.split("\\.", 2);
			if (str.length == 2)
			{
				configManager.unsetConfiguration(str[0], str[1]);
				++removed;
			}
		}

		log.debug("Removed {} old tags", removed);
		configManager.setConfiguration(IDGrabberConfig.GROUP, "migrated", "2");
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getType() == MenuAction.EXAMINE_OBJECT.getId())
		{
			addMenuEntry(event, "Object ID");
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String option = event.getMenuOption();
		if (option.equals("Object ID"))
		{
			int id = event.getId();
			String idString = Integer.toString(id);

			// Copy the ID to the clipboard
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(idString), null);

			// Display the ID in the game chat
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", option + ": " + idString, null);
		}
	}

	private void addMenuEntry(MenuEntryAdded event, String option)
	{
		client.createMenuEntry(-1)
				.setOption(option)
				.setTarget(event.getTarget())
				.setType(MenuAction.of(MenuAction.RUNELITE.getId()))
				.setIdentifier(event.getIdentifier())
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1());
	}


	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
//		if (!client.isKeyPressed(KeyCode.KC_SHIFT))
//		{
//			return;
//		}

		final MenuEntry[] entries = event.getMenuEntries();
		for (int idx = entries.length - 1; idx >= 0; --idx)
		{
			final MenuEntry entry = entries[idx];
			final Widget w = entry.getWidget();

			if (w != null && WidgetUtil.componentToInterface(w.getId()) == InterfaceID.INVENTORY
				&& "Examine".equals(entry.getOption()) && entry.getIdentifier() == 10)
			{
				final int itemId = w.getItemId();
				final Tag tag = getTag(itemId);

				final MenuEntry parent = client.createMenuEntry(idx)
					.setOption("Item ID")
					.setTarget(entry.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						// Send a chat message when "Pick" is clicked
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item ID: " + itemId, null);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(String.valueOf(itemId)), null);


					});


			}
		}
	}

}
