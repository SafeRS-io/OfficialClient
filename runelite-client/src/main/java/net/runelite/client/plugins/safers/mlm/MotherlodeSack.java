package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.Client;

public class MotherlodeSack {
    private final Motherlode motherlode;
    private final MotherlodeInventory inventory;
    private final Client client;

    private final int VARBIT_SACK_UPGRADED = 5556;
    private final int VARBIT_SACK_PAY_DIRT = 5558;
    private final int SACK_SIZE_DEFAULT = 80;
    private final int SACK_SIZE_UPGRADED = 161;
    private final int ANIMATION_HOPPER_DEPOSIT = 832;

    private int sack_pay_dirt = 0;
    private int hopper_pay_dirt = 0;
    private boolean upgraded = false;
    private boolean should_be_emptied = false;

    public MotherlodeSack(final Motherlode motherlode, final MotherlodeInventory inventory, final Client client) {
        this.motherlode = motherlode;
        this.inventory = inventory;
        this.client = client;
    }

    public void onVarbitChanged() {
        if (!motherlode.inRegion()) return;

        updateSackUpgrade();
        updateSackPayDirt();
    }

    public void onGameTick() {
        if (!motherlode.inRegion()) return;

        // Player put pay-dirt inside the hopper, add it to the sack pay-dirt even before it has been cleaned.
        if (hopper_pay_dirt == 0 && client.getLocalPlayer() != null && client.getLocalPlayer().getAnimation() == ANIMATION_HOPPER_DEPOSIT) {
            hopper_pay_dirt += Math.max(inventory.countPayDirt(), inventory.countPayDirtOld());
            motherlode.updatePayDirtNeeded();
        }
    }

    public int countPayDirt() {
        return sack_pay_dirt + hopper_pay_dirt;
    }

    public int getSize() {
        return upgraded ? SACK_SIZE_UPGRADED : SACK_SIZE_DEFAULT;
    }

    public boolean isFullActual() {
        return sack_pay_dirt > getSize();
    }

    public boolean isFull() {
        return countPayDirt() > getSize();
    }

    public boolean shouldBeEmptied() {
        return should_be_emptied;
    }

    private void updateSackUpgrade() {
        upgraded = client.getVarbitValue(VARBIT_SACK_UPGRADED) == 1;
    }

    /**
     * Sack pay-dirt varbit updated. Reset hopper pay-dirt into 0 (since it arrived into the sack).
     */
    private void updateSackPayDirt() {
        final int sack_pay_dirt = client.getVarbitValue(VARBIT_SACK_PAY_DIRT);

        // Update only if the varbit changed.
        if (sack_pay_dirt != this.sack_pay_dirt) {
            this.sack_pay_dirt = sack_pay_dirt;
            this.hopper_pay_dirt = 0;
            motherlode.updatePayDirtNeeded();

            if (isFull()) {
                should_be_emptied = true;
            } else if (this.sack_pay_dirt == 0) {
                should_be_emptied = false;
            }
        }
    }
}
