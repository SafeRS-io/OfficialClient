package net.runelite.client.plugins.safers.gearswitcher;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("GearTagger")
public interface GearSwitcherConfig extends Config {

	@ConfigSection(
			name = "PK Settings",
			description = "",
			position = 5,
			closedByDefault = true
	)
	String Pksection = "pksection";

	@ConfigItem(
			keyName = "pkinfo",
			name = "PK Info",
			description = "Information about how to use this",
			position = 0,
			section = Pksection
	)
	default String readOnlyInfoPK() {
		return "Tag a player by attacking or getting attacked" +
				"All settings set in menu.";
	}




	}
