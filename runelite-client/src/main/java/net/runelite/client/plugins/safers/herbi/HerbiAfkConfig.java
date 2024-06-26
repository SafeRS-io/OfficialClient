package net.runelite.client.plugins.safers.herbi;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("herbiafksafers")
public interface HerbiAfkConfig extends Config
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
		return	"Make sure you start in view of a starting spot\n" +
				"Have herb sack, and staminas. Will not bank yet.";
	}


	@ConfigItem(
		position = 3,
		keyName = "showMiniMapArrow",
		name = "Show arrow on the minimap",
		description = "Show an arrow on the minimap to the next search spot."
	)
	default boolean showMiniMapArrow()
	{
		return true;
	}
	@ConfigItem(
			position = 0,
			keyName = "runEnergyThreshold",
			name = "Run Energy Threshold",
			description = "Set the threshold for run energy below which the overlay will be displayed"

	)
	default int runEnergyThreshold() {
		return 50; // Default threshold, you can change this value
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "arrowColor",
		name = "Minimap arrow color",
		description = "Color of the arrow on the minimap."
	)
	default Color getArrowColor()
	{
		return new Color(255, 0, 255, 255);
	}

	@ConfigItem(
		position = 8,
		keyName = "highlightHerbiHull",
		name = "Highlight herbiboar hull",
		description = "Highlights herbiboar hull at the end of the trail."

	)
	default boolean highlightHerbiHull()
	{
		return true;
	}


	@Alpha
	@ConfigItem(
		position = 10,
		keyName = "herbiboarColor",
		name = "Herbiboar highlight color",
		description = "Color of the herbiboar highlight."
	)
	default Color getHerbiboarColor()
	{
		return new Color(96, 0, 255, 255);
	}

	@ConfigItem(
		position = 11,
		keyName = "pathRelativeToPlayer",
		name = "Path relative to player",
		description = "Make the trail path line relative to the player."
	)
	default boolean pathRelativeToPlayer()
	{
		return true;
	}

	@ConfigItem(
		position = 12,
		keyName = "dynamicMenuEntrySwap",
		name = "Dynamically swap trail menu entries",
		description = "Swap menu entries to only make the correct trail clickable."
	)
	default boolean dynamicMenuEntrySwap()
	{
		return true;
	}

	@ConfigItem(
		position = 13,
		keyName = "npcMenuEntrySwap",
		name = "Hide fossil island npcs menu entries",
		description = "Hide fungi, zygomite and crab interaction menus."
	)
	default boolean npcMenuEntrySwap()
	{
		return true;
	}
}
