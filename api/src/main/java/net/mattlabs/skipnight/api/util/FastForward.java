package net.mattlabs.skipnight.api.util;

import net.mattlabs.skipnight.api.scheduler.ScheduledRunnable;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.world.WorldAdapter;

import java.util.UUID;

public class FastForward extends ScheduledRunnable {

    private final WorldAdapter worldAdapter;
    private final UUID worldUUID;
    private final Scheduler scheduler;
    private final VoteType voteType;

    public FastForward(WorldAdapter worldAdapter, UUID worldUUID, Scheduler scheduler, VoteType voteType) {
        this.worldAdapter = worldAdapter;
        this.worldUUID = worldUUID;
        this.scheduler = scheduler;
        this.voteType = voteType;
    }

    @Override
    public void run() {
        long totalTime = voteType == VoteType.DAY ? 12541 - worldAdapter.getTime(worldUUID) : 24000 - worldAdapter.getTime(worldUUID);
        worldAdapter.setTime(worldUUID, worldAdapter.getTime(worldUUID) + 80);
        totalTime -= 80;
        if (totalTime < 80 && (voteType == VoteType.NIGHT && worldAdapter.hasStorm(worldUUID))) worldAdapter.setStorm(worldUUID, false);
        if (totalTime > 0) scheduler.runTaskLater(this, 1);
    }
}
