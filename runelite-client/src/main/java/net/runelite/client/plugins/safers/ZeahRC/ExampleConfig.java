package net.runelite.client.plugins.safers.ZeahRC;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("ZeahRC")
public interface ExampleConfig extends Config
{
    @ConfigSection(
            name = "Information",
            description = "",
            position = 0,
            closedByDefault = false
    )
    String readonlyInfo = "readOnlyinfo";

    @ConfigItem(
            keyName = "readOnlyInfo32",
            name = "Plugin Info",
            description = "Information about how to use this plugin",
            position = 1,
            section = readonlyInfo
    )
    default String readOnlyInfo() {
        return	"Supports Bloods/Souls\n" +
                "Turn on/off Pathing if you have issues.  Make sure that you have chisel/pickaxe and NO essence in inventory at the start between the mining resource when toggling. Always start with no essence.";
    }
    @ConfigItem(
            keyName = "enableFeature2",
            name = "Start Pathing.",
            description = "Enable/disable pathing, always start with empty inventory pick/chisel at essence mine.",
            position = 10
    )
    default boolean start()
    {
        return false;
    }
    @ConfigItem(
            keyName = "selectedRuneType",
            name = "Select Rune Type",
            description = "Choose between Blood Runes and Soul Runes.",
            position = 20
    )
    default RuneType selectedRuneType() {
        return RuneType.BLOOD_RUNES; // Default value
    }
}
