package net.runelite.client.plugins.safers.thieving;

public enum NpcType {
    ARD_KNIGHTS("Ardy Knights", new int[]{3297, 3298,11936}),
    STALLS("Stalls", new int[]{8725}),
    VYRES("Vyres", new int[]{/* NPC IDs for Net Fishing Spot */});

    private final String name;
    private final int[] npcIds;

    NpcType(String name, int[] npcIds) {
        this.name = name;
        this.npcIds = npcIds;
    }

    public String getName() {
        return name;
    }

    public int[] getNpcIds() {
        return npcIds;
    }

    @Override
    public String toString() {
        return getName();
    }
}
