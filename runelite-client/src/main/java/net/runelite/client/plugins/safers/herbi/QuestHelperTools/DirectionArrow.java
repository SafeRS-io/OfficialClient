/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package net.runelite.client.plugins.safers.herbi.QuestHelperTools;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class DirectionArrow
{
    public static void renderMinimapArrow(Graphics2D graphics, Client client, WorldPoint worldPoint, Color color)
    {
        final int MAX_DRAW_DISTANCE = 16;
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return;
        }

        WorldPoint playerLocation = player.getWorldLocation();

        WorldPoint wp = QuestPerspective.getInstanceWorldPoint(client, worldPoint);

        if (wp == null)
        {
            return;
        }

        if (wp.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
        {
            createMinimapDirectionArrow(graphics, client, wp, color);
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, wp);
        if (lp == null)
        {
            return;
        }

        Point posOnMinimap = Perspective.localToMinimap(client, lp);
        if (posOnMinimap == null)
        {
            return;
        }

        Line2D.Double line = new Line2D.Double(posOnMinimap.getX(), posOnMinimap.getY(), posOnMinimap.getX(),
                posOnMinimap.getY());

        drawMinimapArrow(graphics, line, color);
    }

    protected static void createMinimapDirectionArrow(Graphics2D graphics, Client client, WorldPoint wp, Color color) {
        Player player = client.getLocalPlayer();
        if (wp == null || player == null) {
            return;
        }

        final int MAX_MINIMAP_DISTANCE = 12;
        WorldPoint playerLocation = player.getWorldLocation();

        int distance = playerLocation.distanceTo(wp);
        if (distance <= MAX_MINIMAP_DISTANCE) {
            drawSquareOnMinimap(graphics, client, wp, color);
        } else {
            double angle = Math.atan2(wp.getY() - playerLocation.getY(), wp.getX() - playerLocation.getX());
            int newX = (int) (playerLocation.getX() + MAX_MINIMAP_DISTANCE * Math.cos(angle));
            int newY = (int) (playerLocation.getY() + MAX_MINIMAP_DISTANCE * Math.sin(angle));
            WorldPoint intermediatePoint = new WorldPoint(newX, newY, playerLocation.getPlane());

            drawSquareOnMinimap(graphics, client, intermediatePoint, color);
        }
    }

    private static void drawSquareOnMinimap(Graphics2D graphics, Client client, WorldPoint wp, Color color) {
        LocalPoint localPoint = LocalPoint.fromWorld(client, wp);
        if (localPoint == null) {
            return;
        }

        Point minimapPoint = Perspective.localToMinimap(client, localPoint);
        if (minimapPoint == null) {
            return;
        }

        int squareSize = 5;
        int halfSquareSize = squareSize / 2;
        graphics.setColor(color);
        graphics.fillRect(minimapPoint.getX() - halfSquareSize, minimapPoint.getY() - halfSquareSize, squareSize, squareSize);
    }
    public static void drawMinimapArrow(Graphics2D graphics, Line2D.Double line, Color color) {
        // Size of the square
        int squareSize = 3;
        int halfSquareSize = squareSize / 2;

        // Assuming line.x1 and line.y1 are the center of the tile
        int centerX = (int) line.getX1();
        int centerY = (int) line.getY1();

        // Draw the square right on the center of the tile
        graphics.setColor(color);
        graphics.fillRect(centerX - halfSquareSize, centerY - halfSquareSize, squareSize, squareSize);
    }


    public static void drawWorldArrowHead(Graphics2D g2d, Line2D.Double line, int extraSizeHeight, int extraSizeWidth)
    {
        AffineTransform tx = new AffineTransform();

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 6 + extraSizeHeight);
        arrowHead.addPoint(-6 - extraSizeWidth, -1 - extraSizeHeight);
        arrowHead.addPoint(6 + extraSizeWidth, -1 - extraSizeHeight);

        tx.setToIdentity();
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        tx.translate(line.x2, line.y2);
        tx.rotate((angle - Math.PI / 2d));

        Graphics2D g = (Graphics2D) g2d.create();
        g.setTransform(tx);
        g.fill(arrowHead);
        g.dispose();
    }

    public static void drawLineArrowHead(Graphics2D g2d, Line2D.Double line) {
        AffineTransform tx = new AffineTransform();

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint( 0,0);
        arrowHead.addPoint( -3, -6);
        arrowHead.addPoint( 3,-6);

        tx.setToIdentity();
        double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
        tx.translate(line.x2, line.y2);
        tx.rotate((angle-Math.PI/2d));

        Graphics2D g = (Graphics2D) g2d.create();
        g.setTransform(tx);
        g.fill(arrowHead);
        g.dispose();
    }

    public static void drawLine(Graphics2D graphics, Line2D.Double line, Color color, Rectangle clippingRegion)
    {
        graphics.setStroke(new BasicStroke(1));
        graphics.setClip(clippingRegion);
        graphics.setColor(color);
        graphics.draw(line);

        drawLineArrowHead(graphics, line);
    }
}