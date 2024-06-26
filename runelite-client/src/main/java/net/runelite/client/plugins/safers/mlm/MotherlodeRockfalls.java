package net.runelite.client.plugins.safers.mlm;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Set;

public class MotherlodeRockfalls {
    private final Motherlode motherlode;

    private final Set<Rockfall> rockfalls = ImmutableSet.of(
        new Rockfall(3748, 5684, Arrays.asList(Sector.UPSTAIRS_W, Sector.UPSTAIRS_NW)),
        new Rockfall(3757, 5677, Arrays.asList(Sector.UPSTAIRS_NE, Sector.UPSTAIRS_E)),
        new Rockfall(3762, 5668, Arrays.asList(Sector.UPSTAIRS_SE, Sector.UPSTAIRS_S))
    );

    public MotherlodeRockfalls(final Motherlode motherlode) {
        this.motherlode = motherlode;
    }

    public Set<Rockfall> getRockfalls() {
        return rockfalls;
    }
}
