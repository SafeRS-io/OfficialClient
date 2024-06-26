package net.runelite.client.plugins.safers.Essentials;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface SafeRSConfig extends Config
{
	@ConfigItem(
			keyName = "featureToggle",
			name = "Load Features?",
			description = "Toggle the necessary features to run SafeRS Scripts by Tom. ",
			position = 1
	)
	default boolean featureToggle()
	{
		return true; // Default is on
	}

	@ConfigItem(
			keyName = "featureToggle2",
			name = "AutoPrayers?",
			description = "Toggle the necessary features to run SafeRS Scripts by Tom. ",
			position = 1
	)
	default boolean featureToggle2()
	{
		return false; // Default is on
	}
}
