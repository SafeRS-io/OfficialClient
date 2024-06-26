package net.runelite.client.plugins.safers.thieving;

public enum FoodItem {
    SHARK(new int[]{385}), // Assuming 385 is the item ID for Shark
    LOBSTER(new int[]{377}), // Assuming 377 is the item ID for Lobster
    CAKES(new int[]{1891, 1893, 1895}), // Multiple IDs for Cakes
    WINES(new int[]{1993}); // Assuming 1993 is the item ID for Wines
    // Add other food items here

    private final int[] itemIds;

    FoodItem(int[] itemIds) {
        this.itemIds = itemIds;
    }

    public int[] getItemIds() {
        return itemIds;
    }

    @Override
    public String toString() {
        // Format the name for display in dropdown
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }
}
