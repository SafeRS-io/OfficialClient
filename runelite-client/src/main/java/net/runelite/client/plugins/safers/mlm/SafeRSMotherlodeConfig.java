package net.runelite.client.plugins.safers.mlm;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(SafeRSMotherlodeConfig.group)
public interface SafeRSMotherlodeConfig extends Config {
	String group = "tom-motherlode";

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
		return	"Lower level start on green square\n" +
				"Make sure only this motherlode mining plugin is on. Toggle between upstairs and downstairs. Logout and login if you cant see overlay";
	}
	@ConfigSection(
		name = "Colors",
		description = "Highlight ore veins and rockfalls.",
		position = 10,
			closedByDefault = true
	) String colorsEtc = "Colors";

		@Alpha
		@ConfigItem(
			keyName = "ore_veins",
			name = "Mineable Ore Veins",
			description = "Highlight ore veins that can be mined.",
			position = 1,
			section = colorsEtc
		) default Color getOreVeinsColor() { return new Color(255, 0, 255, 255); }
	@Alpha
	@ConfigItem(
			keyName = "obstacles",
			name = "Obstacles",
			description = "Highlight next obstacle.",
			position = 1,
			section = colorsEtc
	) default Color getObstacleColor() { return new Color(0, 0, 255, 255); }

		@Alpha
		@ConfigItem(
			keyName = "ore_veins_depleted",
			name = "Depleted Ore Veins",
			description = "Highlight ore veins that are depleted.",
			position = 2,
			section = colorsEtc
		) default Color getOreVeinsDepletedColor() { return new Color(0, 0, 0, 0); }

		@Alpha
		@ConfigItem(
			keyName = "ore_veins_stop",
			name = "Stopping Ore Veins",
			description = "Highlight ore veins when they shouldn't be mined.",
			position = 3,
			section = colorsEtc
		) default Color getOreVeinsStoppingColor() { return new Color(255, 0, 120, 255); }

		@Alpha
		@ConfigItem(
			keyName = "rockfalls",
			name = "Rockfalls",
			description = "Highlight rockfalls that need to be cleared.",
			position = 4,
			section = colorsEtc
		) default Color getRockfallsColor() { return new Color(255, 100, 0, 255); }

		@ConfigItem(
			keyName = "draw_distance",
			name = "Draw distance",
			description = "Change how far away ore veins and rockfalls will be highlighted.",
			position = 5,
			section = colorsEtc
		) default int getDrawDistance() { return 4000; }

		@ConfigItem(
			keyName = "sack_needed",
			name = "Needed pay-dirt",
			description = "Show number of pay-dirt needed to mine before you should deposit the pay-dirt.",
			position = 3,
			section = colorsEtc
		) default boolean showSackNeeded() { return true; }

	@ConfigSection(
		name = "General",
		description = "General options to improve overall experience.",
		position = 1
	) String general = "Motherlode Mine Settings";

		@ConfigItem(
			keyName = "upstairs_only",
			name = "Upstairs Enabled",
			description = "Highlight only upstairs ore veins and rockfalls.",
			position = 2,
			section = general
		) default boolean upstairsOnly() { return false; }
}
