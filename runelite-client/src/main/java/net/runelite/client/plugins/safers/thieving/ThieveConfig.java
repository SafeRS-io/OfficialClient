package net.runelite.client.plugins.safers.thieving;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("SafeRS Thieving")
public interface ThieveConfig extends Config
{
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
		return	"Supports fruit/cake stall only for stalls\n" +
				"Make sure in mass world for ardy knight";
	}



	@ConfigItem(
			keyName = "npcColor",
			name = "NPC Color",
			description = "The color of the box on the NPC",
			position = 1,
			section = miscSection
	)
	default Color npcColor()
	{
		return new Color(255, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "selectedNpcType",
			name = "Type to thieve",
			description = "Choose the type of NPCs to highlight",
			position = 0
	)
	default NpcType selectedNpcType()
	{
		return NpcType.ARD_KNIGHTS; // Default value
	}

	@ConfigItem(
			keyName = "npcMarkerDistance",
			name = "Distance to show",
			description = "Set the maximum distance to display markers on NPCs (in tiles)",
			position = 2
	)
	default int npcMarkerDistance()
	{
		return 10; // Default value
	}



	@ConfigItem(
			keyName = "lowHpThreshold",
			name = "Low HP Threshold",
			description = "Set the HP threshold to trigger the item highlight",
			position = 5
	)
	default int lowHpThreshold() {
		return 5; // Default low HP threshold
	}

	@ConfigItem(
			keyName = "highlightItemId",
			name = "Food Type",
			description = "Select the food item to highlight when HP is low",
			position = 3
	)
	default FoodItem highlightItemId() {
		return FoodItem.CAKES; // Default to cakes
	}

	@ConfigSection(
			name = "Misc",
			description = "Miscellaneous settings",
			position = 10,
			closedByDefault = true
	)
	String miscSection = "misc";

	@ConfigItem(
			keyName = "highlightColor1",
			name = "Bank Color",
			description = "Configures the color of the bank",
			position = 7,
			section = miscSection
	)
	default Color BankColor()
	{
		return new Color(0, 0, 255, 255); // Default color is blue
	}

}
