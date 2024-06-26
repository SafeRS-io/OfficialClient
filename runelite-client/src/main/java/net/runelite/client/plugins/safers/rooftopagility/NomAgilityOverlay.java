/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Cas <https://github.com/casvandongen>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.safers.rooftopagility;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.safers.rooftopagility.rooftops.RooftopStage;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

@Slf4j
class NomAgilityOverlay extends Overlay
{
	private final Client client;
	private final NomAgilityPlugin plugin;
	private final NomAgilityConfig config;

	@Inject
	private NomAgilityOverlay(Client client, NomAgilityPlugin plugin, NomAgilityConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGHEST);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}
	private final PanelComponent panelComponent = new PanelComponent();

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isDisableOverlays()) return null;

		final List<Tile> marksOfGrace = plugin.getMarksOfGrace();
		NomCourses course = plugin.getCourse();
		if (course == null) {
			return null;
		}
		Player player = client.getLocalPlayer();

		for (RooftopStage rooftopStage : course.getCourseData().getStageList()) {
			if (!rooftopStage.onArea(client)) {
//				System.out.println("Not on " + rooftopStage.getId());
				continue;
			}
			if (player != null && player.getWorldLocation().getPlane() != 0)
			{
				for (Tile markOfGraceTile : marksOfGrace)
				{
					if (rooftopStage.onArea(markOfGraceTile.getWorldLocation()))
					{
						SquareOverlay.drawTile(client, graphics, markOfGraceTile.getWorldLocation(), new Color(175,117,208), plugin.square());
						plugin.setShowingMarkOfGrace(true);

						return null;
					}
				}
			}
			plugin.setShowingMarkOfGrace(false);

			for (TileObject object : plugin.getObstacles().keySet()) {
				if (object.getId() == rooftopStage.getId()) {
					Shape objectClickbox = object.getClickbox();

					if (objectClickbox == null || objectClickbox.getBounds() == null) {
						return null;
					}
					graphics.setColor(Color.WHITE);
					graphics.draw(objectClickbox);
					SquareOverlay.drawRandomBounds(graphics, object, plugin.square(), config.getOverlayColor());

					return null;
				}
			}
		}
		return null;
	}
}
