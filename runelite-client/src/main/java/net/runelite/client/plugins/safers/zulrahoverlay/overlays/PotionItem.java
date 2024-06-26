package net.runelite.client.plugins.safers.zulrahoverlay.overlays;

public enum PotionItem {
    PRAYER_POTION(new int[]{2434, 143, 141, 139}, "Prayer Potion"),
    SUPER_RESTORE(new int[]{3030, 3028, 3026, 3024}, "Super Restore");

    private final int[] itemIds;
    private final String displayName;

    PotionItem(int[] itemIds, String displayName) {
        this.itemIds = itemIds;
        this.displayName = displayName;
    }

    public int[] getItemIdsPrayer() {
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
