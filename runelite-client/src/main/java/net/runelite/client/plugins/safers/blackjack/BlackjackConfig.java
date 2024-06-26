package net.runelite.client.plugins.safers.blackjack;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("blackjack")
public interface BlackjackConfig extends Config {
	@ConfigSection(
			name = "Information",
			description = "",
			position = 0,
			closedByDefault = false
	)
	String readonlyInfo = "readOnlyinfo";


	@ConfigItem(
			keyName = "readOnlyInfo2",
			name = "Plugin Info",
			description = "Information about how to use this plugin",
			position = 0,
			section = readonlyInfo
	)
	default String readOnlyInfo() {
		return	"One click blackjack!\n" +
				"This is just a utility plugin for now.";
	}


	@ConfigItem(
			keyName = "tagColor",
			name = "Coin Pouch Color",
			description = "The color of the tag to draw when 28 pouches",
			position = 6
	)
	default Color tagColor()
	{
		return new Color(0, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "lowHpThreshold",
			name = "Low HP Threshold",
			description = "Set the HP threshold to trigger the item highlight",
			position = 1

	)
	default int lowHpThreshold() {
		return 5; // Default low HP threshold
	}

	@ConfigItem(
			keyName = "awakeStateColor",
			name = "Awake State Color",
			description = "Change the color of the awake state highlight",
			position = 3
	)
	default Color awakeStateColor() {return new Color(255, 0, 255);}

	@ConfigItem(
			keyName = "knockedOutStateColor",
			name = "Knocked-Out State Color",
			description = "Change the color of the knocked-out state highlight",
			position = 4
	)
	default Color knockedOutStateColor() {return new Color(255, 0, 255);}

}