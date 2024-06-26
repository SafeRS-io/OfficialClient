package net.runelite.client.plugins.safers.gearswitcher;

public enum FoodItem {
    MANTARAY(new int[]{391,24589}, "Manta Ray"),
    ANGLERFISH(new int[]{13441,24592}, "Anglerfish"),
    SHARK(new int[]{385,20390}, "Shark"),
    LOBSTER(new int[]{377}, "Lobster"),
    CAKES(new int[]{1891, 1893, 1895}, "Cakes"),
    KARAMBWAN(new int[]{1993}, "Karambwan");

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
