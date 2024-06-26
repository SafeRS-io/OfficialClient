package net.runelite.client.plugins.safers.ZeahRC;

public enum RuneType {
    BLOOD_RUNES("Blood Runes"),
    SOUL_RUNES("Soul Runes");

    private final String name;

    RuneType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
