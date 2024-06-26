package net.runelite.client.plugins.safers.zulrahoverlay;

import net.runelite.client.config.*;
import net.runelite.client.plugins.safers.zulrahoverlay.overlays.ComboFoodItem;
import net.runelite.client.plugins.safers.zulrahoverlay.overlays.FoodItem;
import net.runelite.client.plugins.safers.zulrahoverlay.overlays.PotionItem;

import java.awt.*;

@ConfigGroup("ZulrahConfig")
public interface ZulrahConfig extends Config {

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
		return	"Only banks and restores at ferox\n" +
				"Only uses 4 dose pots. House is for fairy ring only.";
	}
	@ConfigSection(
			name = "SafeRS Zulrah",
			description = "",
			position = 0,
			closedByDefault = false
	)
	String zulrahSection = "zulrahSection";

	@ConfigSection(
			name = "Colors",
			description = "",
			position = 12,
			closedByDefault = true
	)
	String colors = "colors";
	@ConfigSection(
			name = "Miscellaneous",
			description = "",
			position = 11,
			closedByDefault = true
	)
	String miscellaneousSection = "miscellaneousSection";

	@ConfigItem(
			name = "Font Type",
			keyName = "fontType",
			description = "Configure the font for the plugin overlays to use",
			position = 4,
			section = miscellaneousSection
	)
	default FontType fontType() 
	{
		return FontType.SMALL;
	}



	@ConfigItem(
			name = "Total Tick Counter (InfoBox)",
			keyName = "totalTickCounter",
			description = "Displays a total tick counter infobox showing how long Zulrah has been alive for in ticks",
			position = 3,
			section = miscellaneousSection
	)
	default boolean totalTickCounter() 
	{
		return false;
	}

	@ConfigItem(
			name = "Display Zulrah's Tile",
			keyName = "displayZulrahTile",
			description = "Highlights Zulrah's current tile in a 5x5",
			position = 0,
			section = zulrahSection
	)
	default boolean displayZulrahTile() 
	{
		return true;
	}

	@ConfigItem(
			name = "Zulrah's Tile Color",
			keyName = "zulrahTileColor",
			description = "Configures the color for Zulrah's tile highlight",
			position = 5,
			section = colors
	)
	@Alpha
	default Color zulrahTileColor() 
	{
		return new Color(255, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			name = "Zulrah's Tile Color2",
			keyName = "zulrahTileColor2",
			description = "Configures the color for Zulrah's tile highlight",
			position = 5,
			section = colors
	)
	@Alpha
	default Color zulrahTileColor2()
	{
		return new Color(255, 0, 255, 255); // Default color is red
	}


	@ConfigItem(
			name = "staff color",
			keyName = "staffcolor",
			description = "Configures the color for Zulrah's tile highlight",
			position = 5,
			section = colors
	)
	@Alpha
	default Color staffcolor()
	{
		return new Color(32, 32, 100, 255); // Default color is red
	}




	@ConfigItem(
			name = "Prayer Helper",
			keyName = "prayerHelper",
			description = "Displays an overlay showing the correct prayer to use for the entirity of the Zulrah fight<br>Changes color dependent on whether or not you're praying correctly or not",
			position = 0,
			section = miscellaneousSection
	)
	default boolean prayerHelper() 
	{
		return true;
	}

	@ConfigItem(
			keyName = "lowHpThreshold2",
			name = "Combo Low HP Threshold",
			description = "Set the HP threshold to trigger the item highlight",
			position = 5,
			section = zulrahSection
	)
	default int lowHpComboThreshold() {
		return 72; // Default low HP threshold
	}

	@ConfigItem(
			keyName = "highlightItemId1001",
			name = "Food Type",
			description = "Select the food item to highlight when HP is low",
			position = 1,
			section = zulrahSection
	)
	default FoodItem highlightItemId() {
		return FoodItem.MANTARAY; // Default to cakes
	}

	@ConfigItem(
			keyName = "highlightColor2",
			name = "Food Color",
			description = "Configures the color used for highlighting food",
			position = 1,
			section = colors
	)
	default Color FoodColor()
	{
		return new Color(96, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "highlightItemId3",
			name = "Combo Food Type",
			description = "Select the food item to highlight when HP is low",
			position = 2,
			section = zulrahSection
	)
	default ComboFoodItem highlightItemIdCombo() {
		return ComboFoodItem.KARAMBWAN; // Default to cakes
	}
	@ConfigItem(
			keyName = "highlightColor3",
			name = "Food Color",
			description = "Configures the color used for highlighting combo food",
			position = 6,
			section = colors
	)
	default Color ComboColor()
	{
		return new Color(92, 134, 97, 255); // Default color is yellow
	}

	@ConfigItem(
			keyName = "prayerThreshold",
			name = "Prayer Threshold",
			description = "Set the prayer points threshold to trigger the item highlight",
			position = 6,
			section = zulrahSection
	)
	default int prayerThreshold() {
		return 4; // Default threshold
	}

	@ConfigItem(
			keyName = "potionColor",
			name = "Potion Highlight Color",
			description = "Color of the potion highlight",
			section = colors

	)
	default Color potionColor()
	{
		return new Color(82, 160, 181, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "groundItemColor",
			name = "Ground Item Color",
			description = "The color of the highlighted items",
			position = 6, // Adjust the position as needed,
			section = colors
	)
	default Color GroundItemColor() {
		return new Color(0, 200, 149, 255); // Default color, can be any color
	}

	@ConfigItem(
			keyName = "poolColor",
			name = "Pool Highlight Color",
			description = "Color of the potion highlight",
			section = colors

	)
	default Color poolcolor()
	{
		return new Color(255, 0, 255, 255); // Default color is red
	}
	@ConfigItem(
			keyName = "smokecloudColor",
			name = "smoke Highlight Color",
			description = "Color of the potion highlight",
			section = colors

	)
	default Color smokeColor()
	{
		return new Color(242, 239, 131, 255); // Default color is red
	}
	@ConfigItem(
			keyName = "Minimapmarkered",
			name = "Minimap markers color",
			description = "Color of the potion highlight",
			section = colors

	)
	default Color tilecolormmap()
	{
		return new Color(96, 0, 255, 255); // Default color is red
	}



	@ConfigItem(
			keyName = "duelringcolor",
			name = "Duel Ring Color",
			description = "Color of the potion highlight",
			section = colors

	)
	default Color duelringcolor()
	{
		return new Color(255, 34, 120, 255); // Default color is red
	}


	@ConfigItem(
			keyName = "scrollColor",
			name = "Teleport Scroll Color",
			description = "Color of the potion highlight",
			section = colors

	)
	default Color scrollcolor()
	{
		return new Color(45, 65, 25, 255); // Default color is red
	}


	@ConfigItem(
			keyName = "highlightPotionItemId6",
			name = "Potion Type",
			description = "Select the potion item to highlight",
			position = 3,
			section = zulrahSection
	)
	default PotionItem highlightPotionItemId() {
		return PotionItem.SUPER_RESTORE; // Default potion item
	}

	@ConfigItem(
			keyName = "venomColor",
			name = "Inventory Tag Venom Color",
			description = "The color of the tag to draw over venom",
			section = colors

	)
	default Color VenomColor()
	{
		return new Color(255, 255, 255, 255); // Default color is red
	}

	@ConfigItem(
			keyName = "boostColor",
			name = "Inventory Tag Boost Color",
			description = "The color of the tag to draw over Boost",
			section = colors

	)
	default Color BoostColor()
	{
		return new Color(255, 117, 0, 255); // Default color is red
	}



	@ConfigItem(
			keyName = "boatColor",
			name = "Boat Color",
			description = "The color of the tag to draw over the Zulrah Boat",
			section = colors

	)
	default Color BoatColor()
	{
		return new Color(255, 117, 0, 255); // Default color is red
	}



	@ConfigItem(
			keyName = "lowHpThreshold",
			name = "Low HP Threshold",
			description = "Set the HP threshold to trigger the item highlight",
			position = 4,
			section = zulrahSection
	)
	default int lowHpThreshold() {
		return 50; // Default low HP threshold
	}





	@ConfigItem(
			name = "Prayer Marker",
			keyName = "prayerMarker",
			description = "Marks the correct prayer to use in the prayer book to use for the entirity of the Zulrah fight<br>Changes color dependent on whether or not you're praying correctly or not",
			position = 1,
			section = miscellaneousSection
	)
	default boolean prayerMarker() 
	{
		return true;
	}



	@ConfigItem(
			name = "Stand Locations",
			keyName = "standLocations",
			description = "Highlights the tiles to stand on for the current and next Zulrah phase",
			position = 7,
			section = miscellaneousSection
	)
	default boolean standLocations() 
	{
		return true;
	}

	@ConfigItem(
			name = "Stand/Next Tile Color",
			keyName = "standAndNextColor",
			description = "Configure the color for the stand/next GROUPED tile and text",
			position = 8,
			section = colors
	)
	@Alpha
	default Color standAndNextTileColor() 
	{
		return new Color(96, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			name = "Stand Tile Color",
			keyName = "standTileColor",
			description = "Configure the color for the current stand tile and text",
			position = 9,
			section = colors
	)
	@Alpha
	default Color standTileColor() 
	{
		return new Color(96, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			name = "Next Tile Color",
			keyName = "nextStandTileColor",
			description = "Configure the color for the next stand tile and text",
			position = 10,
			section = colors
	)
	@Alpha
	default Color nextTileColor() 
	{
		return new Color(96, 0, 255, 0); // Default color is red
	}

	@ConfigItem(
			name = "Stall Locations",
			keyName = "stallLocations",
			description = "Highlights the tile to pillar stall a Zulrah phase if it supports it",
			position = 11,
			section = miscellaneousSection
	)
	default boolean stallLocations() 
	{
		return false;
	}

	@ConfigItem(
			name = "Stall Tile Color",
			keyName = "stallTileColor",
			description = "Configures the color for the stall tile and text",
			position = 12,
			section = colors
	)
	@Alpha
	default Color stallTileColor() 
	{
		return new Color(96, 0, 255, 255); // Default color is red
	}

	@ConfigItem(
			name = "recoil Color",
			keyName = "recoilcolor",
			description = "Configures the color for the stall tile and text",
			position = 12,
			section = colors
	)
	@Alpha
	default Color recoilcolor()
	{
		return new Color(96, 200, 255, 255); // Default color is red
	}


	@ConfigItem(
			name = "Display Type",
			keyName = "phaseDisplayType",
			description = "Overlay: Displays Zulrah's phases details on an overlay<br>Tile: Displays Zulrah's phases details on tiles",
			position = 0,
			section = miscellaneousSection
	)
	default DisplayType phaseDisplayType() 
	{
		return DisplayType.TILE;
	}

	@ConfigItem(
			name = "Display Mode",
			keyName = "phaseDisplayMode",
			description = "Current: Only displays the current Zulrah phase<br>Next: Only displays the next Zulrah phase<br>",
			position = 1,
			section = miscellaneousSection
	)
	default DisplayMode phaseDisplayMode() 
	{
		return DisplayMode.CURRENT;
	}

	@ConfigItem(
			name = "Rotation Name",
			keyName = "phaseRotationName",
			description = "Requires: Display Type ('Overlay' or 'Both')<br>Displays text above InfoBox overlay showing the rotation name or unidentified",
			position = 2,
			section = miscellaneousSection
	)
	default boolean phaseRotationName() 
	{
		return false;
	}


	@ConfigItem(
			name = "Instance Timer",
			keyName = "instanceTimer",
			description = "Displays an overlay showing how long Zulrah has been alive in minutes:seconds format<br>Timer resets on Zulrah death and/or leaving of the instance of any fashion",
			position = 0,
			section = miscellaneousSection
	)
	default boolean instanceTimer() 
	{
		return false;
	}

	@ConfigItem(
			name = "Snakeling",
			keyName = "snakelingSetting",
			description = "Remove Att. Op.: Removes the 'Attack' option from all the active Snakelings<br>Entity Hider: Hides all the active Snakelings",
			position = 1,
			section = miscellaneousSection
	)
	default SnakelingSettings snakelingSetting() 
	{
		return SnakelingSettings.MES;
	}

	@ConfigItem(
			name = "Snakeling Hotkey",
			keyName = "snakelingMesHotkey",
			description = "Override the Snakeling MES to show attack options while hotkey is pressed",
			position = 2,
			section = miscellaneousSection
	)
	default Keybind snakelingMesHotkey() 
	{
		return Keybind.NOT_SET;
	}

	// Group 1
	@ConfigItem(
			keyName = "itemIDsGroup17",
			name = "melee items",
			description = "Comma-separated list of item IDs for group 1",
			section = miscellaneousSection
	)
	default String itemIDsGroup1() {
		return "";
	}

	@ConfigItem(
			keyName = "highlightColorGroup61",
			name = "Melee Items",
			description = "The color used to highlight items in group 1",
			section = colors
	)
	default Color highlightColorGroup1() {
		return new Color(255, 0, 0); // Default color, red
	}

	// Group 2
	@ConfigItem(
			keyName = "itemIDsGroup25",
			name = "Ranged Item IDs",
			description = "Comma-separated list of item IDs for group 2",
			section = zulrahSection
	)
	default String itemIDsGroup2() {
		return "";
	}

	@ConfigItem(
			keyName = "highlightColorGroup42",
			name = "Ranged item colors",
			description = "The color used to highlight items in group 2",
			section = colors
	)
	default Color highlightColorGroup2() {
		return new Color(0, 255, 0); // Default color, green
	}

	// Group 3
	@ConfigItem(
			keyName = "itemIDsGroup3",
			name = "Magic Item IDs",
			description = "Comma-separated list of item IDs for group 3",
			section = zulrahSection
	)
	default String itemIDsGroup3() {
		return "";
	}

	@ConfigItem(
			keyName = "highlightColorGroup33",
			name = "magic item colors",
			description = "The color used to highlight items in group 3",
			section = colors
	)
	default Color highlightColorGroup3() {
		return new Color(0, 0, 255); // Default color, blue
	}



	public static enum SnakelingSettings 
	{
		OFF("Off"),
		MES("Remove Att. Op."),
		ENTITY("Entity Hider");

		private final String name;

		public String toString() 
		{
			return this.name;
		}

		private SnakelingSettings(String name) 
		{
			this.name = name;
		}

		public String getName() 
		{
			return this.name;
		}
	}

	public static enum DisplayMode 
	{
		CURRENT("Current"),
		NEXT("Next"),
		BOTH("Both");

		private final String name;

		public String toString() 
	{
			return this.name;
		}

		private DisplayMode(String name) 
		{
			this.name = name;
		}

		public String getName() 
		{
			return this.name;
		}
	}

	public static enum DisplayType 
	{
		OFF("Off"),
		OVERLAY("Overlay"),
		TILE("Tile"),
		BOTH("Both");

		private final String name;

		public String toString() 
		{
			return this.name;
		}

		private DisplayType(String name) 
		{
			this.name = name;
		}

		public String getName() 
		{
			return this.name;
		}
	}
}
