package net.runelite.client.plugins;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.infobox.Timer;

public class SquareOverlay {
    public static OverlayLayer OVERLAY_LAYER2 = OverlayLayer.ABOVE_WIDGETS;
    public static OverlayLayer OVERLAY_LAYER = OverlayLayer.UNDER_WIDGETS;

	public static void drawRandomBounds(Graphics2D g, Widget widget, int size, Color color)
	{
		if (widget == null || widget.getBounds() == null || widget.isHidden()) return;
		drawCenterSquare(g,generatePoint(widget.getBounds()),size,color);
	}

	public static void drawRandomBounds(Graphics2D g, Actor actor, int size, Color color)
	{
		drawCenterSquare(g,generatePoint(actor.getConvexHull()),size,color);
	}

    public static void drawRandomBounds(Graphics2D g, Shape shape, int size, Color color)
    {
        drawCenterSquare(g,generatePoint(shape),size,color);
    }

    public static void drawRandomBounds(Graphics2D g, Polygon polygon, int size, Color color)
    {
        drawCenterSquare(g,generatePoint(polygon),size,color);
    }

    public static void drawRandomBounds(Graphics2D g, TileObject tileObject, int size, Color color)
    {
        Shape shape = tileObject.getClickbox();
        if (shape == null) return;
        drawCenterSquare(g,generatePoint(shape),size,color);
    }

    private static Point generatePoint(Shape region) {
        if (region == null) return new Point(0, 0);

        Rectangle r = region.getBounds();
        int centerX = (int)(r.getX() + r.getWidth() / 2);
        int centerY = (int)(r.getY() + r.getHeight() / 2);

        return new Point(centerX, centerY);
    }

	private static int offsetX = 0;
	private static int offsetY = 0;
	private static Instant lastRefresh = Instant.now();
    public static void refreshItemOffset(WidgetItem widgetItem) {
        if (Instant.now().isBefore(lastRefresh.plusMillis(50))) return;
        Rectangle r = widgetItem.getCanvasBounds();
        if (r == null) return;

        // Directly set the offsets to zero to center the item
        offsetX = 0;
        offsetY = 0;
    }
	public static void drawItemSquare(Graphics2D g, WidgetItem widgetItem, int size, Color color)
	{
		Rectangle r = widgetItem.getCanvasBounds();
		if (r == null) return;
		int centerX = (int)(r.getX()+r.getWidth()/2);
		int centerY = (int)(r.getY()+r.getHeight()/2);
		drawCenterSquare(g, new Point(centerX+offsetX, centerY+offsetY), size, color);
	}

    public static double distance(final java.awt.Point p, final Shape s,
                                  final double eps) {
        if (s.contains(p))
            return 0;
        final PathIterator pi = s.getPathIterator(null, eps);
        final Line2D line = new Line2D.Double();
        double bestDistSq = Double.POSITIVE_INFINITY;
        double firstX = Double.NaN;
        double firstY = Double.NaN;
        double lastX = Double.NaN;
        double lastY = Double.NaN;
        final double coords[] = new double[6];
        while (!pi.isDone()) {
            final boolean validLine;
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    lastX = coords[0];/*from ww  w  .  j  a va2s.co  m*/
                    lastY = coords[1];
                    firstX = lastX;
                    firstY = lastY;
                    validLine = false;
                    break;
                case PathIterator.SEG_LINETO: {
                    final double x = coords[0];
                    final double y = coords[1];
                    line.setLine(lastX, lastY, x, y);
                    lastX = x;
                    lastY = y;
                    validLine = true;
                    break;
                }
                case PathIterator.SEG_CLOSE:
                    line.setLine(lastX, lastY, firstX, firstY);
                    validLine = true;
                    break;
                default:
                    throw new AssertionError();
            }
            if (validLine) {
                final double distSq = line.ptSegDistSq(p);
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                }
            }
            pi.next();
        }
        return Math.sqrt(bestDistSq);
    }

    public static void drawCenterSquare(Graphics2D g, int centerX, int centerY, int size, Color color)
    {
        g.setColor(color);
        g.fillRect(centerX - size / 2, centerY - size / 2, size, size);
    }
    public static void drawCenterSquare(Graphics2D g, double centerX, double centerY, int size, Color color)
    {
        drawCenterSquare(g, (int)centerX, (int)centerY, size, color);
    }
    public static void drawCenterSquare(Graphics2D g, Rectangle bounds, int size, Color color)
    {
        drawCenterSquare(g, bounds.getCenterX(), bounds.getCenterY(), size, color);
    }

    public static void drawCenterSquare(Graphics2D g, Rectangle2D bounds, int size, Color color)
    {
        drawCenterSquare(g, bounds.getCenterX(), bounds.getCenterY(), size, color);
    }
    public static void drawCenterSquare(Graphics2D g, Point p, int size, Color color)
    {
        drawCenterSquare(g, p.getX(), p.getY(), size, color);
    }


    public static void drawCenterSquare(Graphics2D g, Actor actor, int size, Color color)
    {
        Point p = actor.getCanvasTextLocation(g, ".", actor.getLogicalHeight() / 2);
        if (p != null)
        drawCenterSquare(g, p.getX(), p.getY(), size, color);
    }

    public static void drawCenterSquare(Graphics2D g, Client client, WorldPoint worldPoint, int size, Color color)
    {
        LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
        if (lp == null) return;
        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) return;
        drawCenterSquare(g, poly, size, color);
    }

	public static void drawRandomTile(Client client, final Graphics2D graphics, final WorldPoint dest, int size,  final Color color)
	{
		if (dest == null)
		{
			return;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(client,dest);

		if (localPoint == null) {
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);

		if (poly == null)
		{
			return;
		}

		drawRandomBounds(graphics, poly, size, color);
	}


	public static WorldPoint inbetweenTile(WorldPoint al, WorldPoint bl, int maxDistance) {
		int distance = Math.min(al.distanceTo(bl),maxDistance);
		double deltaX = bl.getX()-al.getX();
		double deltaY = bl.getY()-al.getY();
		double ratio = distance/Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
		return new WorldPoint((int)(al.getX()+deltaX*ratio),(int)(al.getY()+deltaY*ratio),al.getPlane());
	}

    public static void drawOnMinimap(Client client, Graphics2D graphics, WorldPoint point,int size, Color color)
    {
        Player local = client.getLocalPlayer();
        if (local == null) {
            return;
        }
        WorldPoint playerLocation = local.getWorldLocation();
        int distance = point.distanceTo(playerLocation);

        if (distance >= 100 || point.getPlane() != client.getPlane())
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Point posOnMinimap = Perspective.localToMinimap(client, lp);
        if (posOnMinimap == null)
        {
            return;
        }

        drawCenterSquare(graphics, posOnMinimap, size, color);
    }


    public static void drawCenterSquare(Graphics2D g, Polygon p, int size, Color color)
    {
        Rectangle r = p.getBounds();
        drawCenterSquare(g, (int)r.getCenterX(), (int)r.getCenterY(), size, color);
    }

    public static void drawTile(Client client, Graphics2D graphics, WorldPoint point, Color color, int size)
    {
        Player local = client.getLocalPlayer();
        if (local == null) return;
        WorldPoint playerLocation = local.getWorldLocation();
        int distance = point.distanceTo(playerLocation);
        if (distance >= 100 || point.getPlane() != client.getPlane())
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
        {
            return;
        }

        drawCenterSquare(graphics, poly, size, color);
    }




    private static HashMap<java.awt.Point, Timer> timerHashMap = new HashMap<>();

    public static boolean shouldHide(Client client, Rectangle bounds, Plugin plugin, int delay) {
        if (bounds == null) return false;

        if (delay > 0 &&
                bounds.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
        {
            timerHashMap.put(bounds.getLocation(), new Timer(delay, ChronoUnit.MILLIS, new BufferedImage(1, 1, 1), plugin));
            return true;
        }

        if (timerHashMap.containsKey(bounds.getLocation()))
        {
            Timer timer = timerHashMap.get(bounds.getLocation());
            Duration timeLeft = Duration.between(Instant.now(), timer.getEndTime());
            if (timeLeft.isNegative())
            {
                timerHashMap.remove(bounds.getLocation());
                return false;
            }
            return true;
        }
        return false;
    }
}
