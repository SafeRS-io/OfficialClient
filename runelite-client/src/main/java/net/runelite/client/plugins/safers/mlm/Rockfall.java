package net.runelite.client.plugins.safers.mlm;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Rockfall that can be predefined if it needs to be shown in specific sector only.
 */
public class Rockfall {
    public final int x;
    public final int y;
    public final List<Sector> sectors;

    /**
     * Rockfall.
     * @param x - World location x.
     * @param y - World location y.
     * @param sectors - Custom defined sector (different sectors for upstairs).
     */
    public Rockfall(final int x, final int y, @Nullable final List<Sector> sectors) {
        this.x = x;
        this.y = y;
        this.sectors = sectors;
    }
}
