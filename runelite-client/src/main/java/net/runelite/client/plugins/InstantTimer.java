package net.runelite.client.plugins;

import java.time.Duration;
import java.time.Instant;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public class InstantTimer {
    private Instant instant;

    public InstantTimer() {
        this.instant = Instant.now();
    }

    public void resetTimer() {
        instant = Instant.now();
    }

	public long getMillis() {
		return Duration.between(instant,Instant.now()).toMillis();
	}

    public boolean runningMoreThan(long millis) {
        return Duration.between(instant,Instant.now()).toMillis() > millis;
    }
	public boolean runningLessThan(long millis) {
		return Duration.between(instant,Instant.now()).toMillis() < millis;
	}

    public void increaseTime(long millis) {
        instant = instant.plusMillis(millis);
    }
	public void decreaseTime(long millis) {
		instant = instant.minusMillis(millis);
	}

	private WorldPoint lastPoint = null;
	public void checkResetMovement(Client client) {
		if (client.getLocalPlayer() == null) return;
		WorldPoint currentPoint = client.getLocalPlayer().getWorldLocation();
		if (!currentPoint.equals(lastPoint)) {
			lastPoint = currentPoint;
			resetTimer();
		}
	}
}
