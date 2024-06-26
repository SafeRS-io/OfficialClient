package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

public enum FoodItem {
    MANTARAY(new int[]{391}, "Manta Ray"),
    ANGLERFISH(new int[]{13441}, "Anglerfish"),
    SHARK(new int[]{385}, "Shark"),
    LOBSTER(new int[]{377}, "Lobster"),
    CAKES(new int[]{1891, 1893, 1895}, "Cakes"),
    WINES(new int[]{1993}, "Wines");

    private final int[] itemIds;
    private final String displayName;

    FoodItem(int[] itemIds, String displayName) {
        this.itemIds = itemIds;
        this.displayName = displayName;
    }

    public int[] getItemIdsFood() {
        return itemIds;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
