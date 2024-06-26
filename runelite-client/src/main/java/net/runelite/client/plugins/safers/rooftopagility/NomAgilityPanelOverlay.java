/*
 * Copyright (c) 2018, Joris K <kjorisje@gmail.com>
 * Copyright (c) 2018, Lasse <cronick@zytex.dk>
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

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

class NomAgilityPanelOverlay extends OverlayPanel
{

	private final Client client;
	private final NomAgilityPlugin plugin;

	@Inject
	private NomAgilityPanelOverlay(Client client, NomAgilityPlugin plugin)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		this.client = client;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().add(TitleComponent.builder()
			.text("SafeRS Agility")
			.color(new Color(0,128,192))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Course:")
			.right(plugin.getCourse() == null ? "Not found" : plugin.getCourse().name())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Mark of Grace:")
			.right(plugin.isShowingMarkOfGrace()+"")
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Status:")
			.right(plugin.getStatus()+"")
			.build());

//		panelComponent.getChildren().add(LineComponent.builder()
//			.left("Tab:")
//			.right(client.getVarcIntValue(VarClientInt.INVENTORY_TAB)+"")
//			.build());


		return super.render(graphics);
	}
}
