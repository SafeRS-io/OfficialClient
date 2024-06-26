/*
 * Copyright (c) 2020, dekvall <https://github.com/dekvall>
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
package net.runelite.client.plugins.safers.driftnet;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

class DriftNetOverlay extends Overlay
{
	private final DriftNetConfig config;
	private final DriftNetPlugin plugin;
	private final Client client;
	@Inject
	private DriftNetOverlay(DriftNetConfig config, DriftNetPlugin plugin, Client client)
	{
		this.config = config;
		this.plugin = plugin;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInDriftNetArea())
		{
			return null;
		}

		if (config.highlightUntaggedFish())
		{
			renderFish(graphics);
		}
		if (config.showNetStatus())
		{
			renderNets(graphics);
		}
		if (config.tagAnnetteWhenNoNets())
		{
			renderAnnette(graphics);
		}

		return null;
	}

	private boolean isPlayerInteracting() {
		final Player localPlayer = client.getLocalPlayer();
		// This condition checks if the player's animation or interacting state indicates interaction
		// You might need to adjust the condition based on how your game signifies interaction
		return localPlayer != null && (localPlayer.getAnimation() != -1 || localPlayer.getInteracting() != null);
	}


	private void renderFish(Graphics2D graphics) {
		if (isPlayerInteracting()) {
			return; // Skip rendering if the player is interacting with something
		}

		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
		DriftNet closestNet = null;
		double closestNetDistance = Double.MAX_VALUE;

		// First, find the net closest to the player
		for (DriftNet net : plugin.getNETS()) {
			LocalPoint netLocation = net.getNet().getLocalLocation(); // Assuming getLocalLocation() gives net's location
			int distanceToPlayer = netLocation.distanceTo(playerLocation);

			if (distanceToPlayer < closestNetDistance) {
				closestNet = net;
				closestNetDistance = distanceToPlayer;
			}
		}

		if (closestNet == null) {
			return; // No net found, or no nets are close to the player
		}

		// Now, find the fish closest to the closest net
		NPC closestFishToNet = null;
		double closestDistanceToNet = Double.MAX_VALUE;
		LocalPoint netLocation = closestNet.getNet().getLocalLocation();

		for (NPC fish : plugin.getFish()) {
			if (!plugin.getTaggedFish().containsKey(fish)) {
				LocalPoint fishLocation = fish.getLocalLocation();
				int distanceToNet = fishLocation.distanceTo(netLocation);

				if (distanceToNet < closestDistanceToNet) {
					closestFishToNet = fish;
					closestDistanceToNet = distanceToNet;
				}
			}
		}

		// Highlight the closest fish to the closest net
		if (closestFishToNet != null) {
			LocalPoint fishLocalPoint = closestFishToNet.getLocalLocation();
			if (fishLocalPoint != null) {
				Point canvasPoint = Perspective.localToCanvas(client, fishLocalPoint, client.getPlane());

				if (canvasPoint != null) {
					int squareSize = 5;
					int halfSquareSize = squareSize / 2;
					int x = canvasPoint.getX() - halfSquareSize;
					int y = canvasPoint.getY() - halfSquareSize;

					graphics.setColor(config.untaggedFishColor());
					graphics.fillRect(x, y, squareSize, squareSize);
				}
			}
		}
	}




	private void renderNets(Graphics2D graphics) {
		for (DriftNet net : plugin.getNETS()) {
			final Shape polygon = net.getNet().getConvexHull();

			if (polygon != null) {
				// Calculate the bounding box of the convex hull
				Rectangle bounds = polygon.getBounds();

				// Define the size of the square to be drawn
				int squareSize = 6;
				int halfSquareSize = squareSize / 2;

				// Calculate the position on the top right corner of the bounding box
				int squareX = bounds.x + bounds.width - halfSquareSize; // Adjust to the top right corner
				int squareY = bounds.y - halfSquareSize; // Align with the top edge

				// Set the color for the square and draw it
				Color previousColor = graphics.getColor(); // Save the current color
				graphics.setColor(net.getStatus().getColor()); // Set the color for the square
				graphics.fillRect(squareX-10, squareY+10, squareSize, squareSize); // Draw the square
				graphics.setColor(previousColor); // Restore the previous color
			}

			// Render the count text as before
			String text = net.getFormattedCountText();
			Point textLocation = net.getNet().getCanvasTextLocation(graphics, text, 0);
			if (textLocation != null) {
				OverlayUtil.renderTextLocation(graphics, textLocation, text, config.countColor());
			}
		}
	}

	private void renderAnnette(Graphics2D graphics) {
		GameObject annette = plugin.getAnnette();
		if (annette != null && !plugin.isDriftNetsInInventory()) {
			// Convert the local game location to canvas coordinates
			Point canvasPoint = Perspective.localToCanvas(client, annette.getLocalLocation(), annette.getPlane());

			if (canvasPoint != null) {
				int squareSize = 5;
				int halfSquareSize = squareSize / 2;
				int x = canvasPoint.getX() - halfSquareSize;
				int y = canvasPoint.getY() - halfSquareSize;

				// Draw the square directly
				graphics.setColor(config.annetteTagColor());
				graphics.fillRect(x, y, squareSize, squareSize);
			}
		}
	}

}
