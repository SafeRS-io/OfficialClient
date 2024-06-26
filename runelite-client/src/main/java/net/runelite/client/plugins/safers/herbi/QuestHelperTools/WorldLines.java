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
import net.runelite.api.Constants;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.Line2D;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class WorldLines {
    private static final int TILE_LENGTH = Perspective.LOCAL_TILE_SIZE;
    private static final int MAX_DISTANCE = 15 * TILE_LENGTH; // 15 tiles in local units
    private static final int CLOSE_DISTANCE = 5 * TILE_LENGTH; // 5 tiles in local units
    private static Instant closeToPointTime = null;

    public static void createMinimapLines(Graphics2D graphics, Client client, List<WorldPoint> linePoints,
                                          Color color) {
        if (linePoints == null || linePoints.size() < 2) {
            return;
        }

        for (int i = 0; i < linePoints.size() - 1; i++) {
            LocalPoint startPoint = QuestPerspective.getInstanceLocalPoint(client, linePoints.get(i));
            LocalPoint destinationPoint = QuestPerspective.getInstanceLocalPoint(client, linePoints.get(i + 1));
            if (startPoint == null || destinationPoint == null) {
                continue;
            }

            LocalPoint pointToDraw;
            if (startPoint.distanceTo(destinationPoint) <= MAX_DISTANCE) {
                pointToDraw = destinationPoint;
            } else {
                pointToDraw = getIntermediatePoint(startPoint, destinationPoint, MAX_DISTANCE);
            }

            if (closeToPointTime == null && startPoint.distanceTo(destinationPoint) <= CLOSE_DISTANCE) {
                closeToPointTime = Instant.now();
            }

            if (closeToPointTime != null) {
                if (Duration.between(closeToPointTime, Instant.now()).getSeconds() >= 5) {
                    continue; // Skip drawing the point
                }
            }

            Point pointOnMinimap = Perspective.localToMinimap(client, pointToDraw, 10000000);
            if (pointOnMinimap == null) {
                continue;
            }

            graphics.setColor(color);
            graphics.fillRect(pointOnMinimap.getX() - 2, pointOnMinimap.getY() - 2, 5, 5);
        }
    }

    private static LocalPoint getIntermediatePoint(LocalPoint start, LocalPoint end, int maxDistance) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double ratio = maxDistance / distance;

        int intermediateX = (int) (start.getX() + ratio * dx);
        int intermediateY = (int) (start.getY() + ratio * dy);

        return new LocalPoint(intermediateX, intermediateY);
    }
    public static Line2D.Double getWorldLines(@Nonnull Client client, @Nonnull LocalPoint startLocation, LocalPoint endLocation)
    {
        final int plane = client.getPlane();

        final int startX = startLocation.getX();
        final int startY = startLocation.getY();
        final int endX = endLocation.getX();
        final int endY = endLocation.getY();

        final int sceneX = startLocation.getSceneX();
        final int sceneY = startLocation.getSceneY();

        if (sceneX < 0 || sceneY < 0 || sceneX >= Constants.SCENE_SIZE || sceneY >= Constants.SCENE_SIZE)
        {
            return null;
        }

        final int startHeight = Perspective.getTileHeight(client, startLocation, plane);
        final int endHeight = Perspective.getTileHeight(client, endLocation, plane);

        Point p1 = Perspective.localToCanvas(client, startX, startY, startHeight);
        Point p2 = Perspective.localToCanvas(client, endX, endY, endHeight);

        if (p1 == null || p2 == null)
        {
            return null;
        }

        return new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static void drawLinesOnWorld(Graphics2D graphics, Client client, List<WorldPoint> linePoints,
                                        Color color)
    {
        for (int i = 0; i < linePoints.size() - 1; i++)
        {
            LocalPoint startLp = QuestPerspective.getInstanceLocalPoint(client, linePoints.get(i));
            LocalPoint endLp = QuestPerspective.getInstanceLocalPoint(client, linePoints.get(i+1));
            if (startLp == null || endLp == null)
            {
                continue;
            }

            Line2D.Double newLine = getWorldLines(client, startLp, endLp);
            if (newLine != null)
            {
                OverlayUtil.renderPolygon(graphics, newLine, color);
            }
        }
    }
}