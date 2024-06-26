package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

public enum ComboFoodItem {
    KARAMBWAN(new int[]{3144}, "Karambwan"),
    WILD_PIE(new int[]{7208}, "Wild Pie");

    private final int[] itemIds;
    private final String displayName;

    ComboFoodItem(int[] itemIds, String displayName) {
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
