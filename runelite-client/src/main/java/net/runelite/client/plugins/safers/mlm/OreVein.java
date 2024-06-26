package net.runelite.client.plugins.safers.mlm;

public class OreVein {
    public final int x;
    public final int y;
    public final Sector sector;
    private int game_ticks;
    private boolean depleted;

    /**
     * OreVein
     * @param x - World location X.
     * @param y - World location y.
     * @param sector - Custom defined sector (different sectors for upstairs).
     * @param depleted - Is ore vein depleted or not.
     */
    public OreVein(final int x, final int y, final Sector sector, final boolean depleted) {
        this.x = x;
        this.y = y;
        this.sector = sector;
        this.depleted = depleted;
        this.game_ticks = 0;
    }

    public void onGameTick() {
        this.game_ticks++;
    }

    public int getGameTicks() {
        return game_ticks;
    }

    public void resetGameTicks() {
        game_ticks = 0;
    }

    public void setDepleted(final boolean depleted) {
        this.depleted = depleted;
    }

    public boolean isDepleted() {
        return depleted;
    }
}
