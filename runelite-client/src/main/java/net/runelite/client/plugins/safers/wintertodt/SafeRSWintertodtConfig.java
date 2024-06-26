package net.runelite.client.plugins.safers.wintertodt;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("SafeRS Wintertodt")
public interface SafeRSWintertodtConfig extends Config
{
	@ConfigSection(
			name = "Information",
			description = "Read-only information about the plugin",
			position = 1,
			closedByDefault = false
	)
	String informationSection = "information";

	@ConfigItem(
			keyName = "highlightItemId",
			name = "Food Type",
			description = "Select the food item to highlight when HP is low",
			position = 3,
			section = informationSection
	)

	default FoodItem highlightItemId() {
		return FoodItem.CAKES; // Default to cakes
	}
	@ConfigItem(
			keyName = "lowHpThreshold",
			name = "Low HP Threshold",
			description = "Set the HP threshold to trigger the item highlight",
			position = 4,
			section = informationSection
	)
	default int lowHpThreshold() {
		return 5; // Default low HP threshold
	}
	@ConfigItem(
			keyName = "readOnlyInfo",
			name = "Plugin Info",
			description = "Information about how to use this plugin",
			position = 1,
			section = informationSection
	)
	default String readOnlyInfo() {
		return	"Make sure you have a hammer, axe, tinderbox, and knife." +
				" Set your food in the first slot of your bank tab with bank tags plugin enabled." +
				" Always play in fixed interface style for this script.";
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
	@ConfigItem(
			keyName = "tileColorOnMinimap",
			name = "Tile Color On Minimap",
			description = "Color of the tile highlight on the minimap.",
			position = 4,
			section = miscSection
	)
	default Color getTileColorOnMinimap() {
		return new Color(96, 0, 255, 255); // Default color, can be any color
	}

	@ConfigItem(
			keyName = "highlightColor2",
			name = "Food Color",
			description = "Configures the color used for highlighting food",
			position = 8,
			section = miscSection
	)
	default Color FoodColor()
	{
		return new Color(0, 255, 0, 255); // Default color is red
	}
	@ConfigItem(
			keyName = "logColor",
			name = "Inventory Tag Log Color",
			description = "The color of the tag to draw over Log",
			section = miscSection
	)
	default Color LogColor()
	{
		return new Color(255, 0, 0, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "fletchColor",
			name = "Fletch Tag Color",
			description = "The color of the tag to draw over fletched logs",
			section = miscSection
	)
	default Color FletchColor()
	{
		return new Color(255, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "knifeColor",
			name = "Inventory Tag Knife Color",
			description = "The color of the tag to draw over fish",
			section = miscSection
	)
	default Color KnifeColor()
	{
		return new Color(0, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "highlightColor3",
			name = "Bank Color",
			description = "Configures the color of the bank",
			position = 7,
			section = miscSection
	)
	default Color DoorColor()
	{
		return new Color(0, 0, 255, 255); // Default color is blue
	}

	@ConfigItem(
			keyName = "highlightColor4",
			name = "Branch Color",
			description = "Configures the color of the bank",
			position = 7,
			section = miscSection
	)
	default Color BranchColor()
	{
		return new Color(0, 255, 221, 255); // Default color is blue
	}

	@ConfigItem(
			keyName = "highlightColor6",
			name = "Brazier Color On Fire",
			description = "Configures the color of the brazier",
			position = 7,
			section = miscSection
	)
	default Color CauldronColor()
	{
		return new Color(255, 77, 0, 255); // Default color is blue
	}

	@ConfigItem(
			keyName = "highlightColor7",
			name = "Brazier Color Not On Fire",
			description = "Configures the color of the unlit brazier",
			position = 7,
			section = miscSection
	)
	default Color CauldronColor2()
	{
		return new Color(255, 0, 255, 255); // Default color is blue
	}

}
