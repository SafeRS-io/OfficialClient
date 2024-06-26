/*
 * Copyright (c) 2020, dekvall <https://github.com/dekvall>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.safers.driftnet;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("safersdriftnet")
public interface DriftNetConfig extends Config
{
	@ConfigSection(
			name = "Information",
			description = "",
			position = 0,
			closedByDefault = false
	)
	String readonlyInfo = "readOnlyinfo";

	@ConfigSection(
			name = "Colors",
			description = "",
			position = 12,
			closedByDefault = true
	)
	String colors = "colors";



	@ConfigItem(
			keyName = "readOnlyInfo2",
			name = "Plugin Info",
			description = "Information about how to use this plugin",
			position = 0,
			section = readonlyInfo
	)
	default String readOnlyInfo() {
		return	"Make sure you have plenty of nets stored. Left click menu entry swap to withdraw all\n" +
				"Make sure you have 20k numulite paid. Make sure you have a harpoon(trident)/flippers/breathing stuff/wield weapon when in room";
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

	@ConfigItem(
		position = 1,
		keyName = "showNetStatus",
		name = "Show net status",
		description = "Show net status and fish count",
			section = colors
	)
	default boolean showNetStatus()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "countColor",
		name = "Fish count color",
		description = "Color of the fish count text",
			section = colors
	)
	default Color countColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		position = 3,
		keyName = "highlightUntaggedFish",
		name = "Highlight untagged fish",
		description = "Highlight the untagged fish",
			section = colors
	)
	default boolean highlightUntaggedFish()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "timeoutDelay",
		name = "Tagged timeout",
		description = "Time required for a tag to expire",
			section = colors
	)
	@Range(
		min = 1,
		max = 100
	)
	@Units(Units.TICKS)
	default int timeoutDelay()
	{
		return 60;
	}

	@Alpha
	@ConfigItem(
		keyName = "untaggedFishColor",
		name = "Untagged fish color",
		description = "Color of untagged fish",
		position = 5,
			section = colors
	)
	default Color untaggedFishColor()
	{
		return new Color(255,0,255);
	}

	@ConfigItem(
		keyName = "tagAnnette",
		name = "Tag Annette",
		description = "Tag Annette when no nets in inventory",
		position = 6,
			section = colors
	)
	default boolean tagAnnetteWhenNoNets()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "annetteTagColor",
		name = "Annette tag color",
		description = "Color of Annette tag",
		position = 7,
			section = colors
	)
	default Color annetteTagColor()
	{
		return Color.BLUE;
	}
}
