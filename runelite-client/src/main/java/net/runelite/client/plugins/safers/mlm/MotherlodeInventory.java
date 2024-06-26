package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;

public class MotherlodeInventory {
    private final Motherlode motherlode;
    private final int PAY_DIRT = 12011;
    private final int INVENTORY_SIZE = 28;

    private int pay_dirt = 0;
    private int pay_dirt_old = 0;
    private int items = 0;

    public MotherlodeInventory(final Motherlode motherlode) {
        this.motherlode = motherlode;
    }

    /**
     * Inventory changed, calculate amount of items and pay-dirt in inventory.
     * @param event - ItemContainerChanged (need to check if container ID is inventory ID).
     */
    public void onItemContainerChanged(final ItemContainerChanged event) {
        if (!motherlode.inRegion()) return;

        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            pay_dirt_old = pay_dirt;
            pay_dirt = 0;
            items = 0;

            for (final Item item : event.getItemContainer().getItems()) {
                if (item != null && item.getId() != -1) {
                    if (item.getId() == PAY_DIRT) pay_dirt++;
                    items++;
                }
            }

            motherlode.updatePayDirtNeeded();
        }
    }

    public int countPayDirtOld() {
        return pay_dirt_old;
    }

    public int countPayDirt() {
        return pay_dirt;
    }

    public int countItems() {
        return items;
    }

    public int getSize() {
        return INVENTORY_SIZE;
    }
}
