package net.mattlabs.skipnight.api.util;

import net.mattlabs.skipnight.api.scheduler.ScheduledRunnable;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.world.WorldAdapter;

import java.util.UUID;

/**
 * Task responsible for gradually fast-forwarding a world's time after a vote
 * to skip the day or night has passed.
 *
 * <p>This task advances the world time in small increments each tick rather
 * than instantly changing the time. This creates a visual fast-forward
 * effect for players.</p>
 *
 * <p>The task reschedules itself every tick until the target time is reached.</p>
 *
 * <ul>
 *     <li>Night votes fast-forward to the start of day.</li>
 *     <li>Day votes fast-forward to the start of night.</li>
 * </ul>
 */
public class FastForward extends ScheduledRunnable {

    private final WorldAdapter worldAdapter;
    private final UUID worldUUID;
    private final Scheduler scheduler;
    private final VoteType voteType;

    /**
     * Creates a new fast-forward task.
     *
     * @param worldAdapter adapter used to read and modify world state
     * @param worldUUID the world being fast-forwarded
     * @param scheduler scheduler used to repeatedly run the task
     * @param voteType the vote type that triggered the fast-forward
     */
    public FastForward(WorldAdapter worldAdapter, UUID worldUUID, Scheduler scheduler, VoteType voteType) {
        this.worldAdapter = worldAdapter;
        this.worldUUID = worldUUID;
        this.scheduler = scheduler;
        this.voteType = voteType;
    }

    /**
     * Advances the world time and schedules the next tick of the fast-forward.
     *
     * <p>The world time is increased by {@code 80} ticks per execution until
     * the target time is reached.</p>
     *
     * <p>If skipping a stormy night, the storm is cleared just before the
     * transition to day.</p>
     */
    @Override
    public void run() {
        long totalTime = voteType == VoteType.DAY ? 12541 - worldAdapter.getTime(worldUUID) : 24000 - worldAdapter.getTime(worldUUID);
        worldAdapter.setTime(worldUUID, worldAdapter.getTime(worldUUID) + 80);
        totalTime -= 80;
        if (totalTime < 80 && (voteType == VoteType.NIGHT && worldAdapter.hasStorm(worldUUID))) worldAdapter.setStorm(worldUUID, false);
        if (totalTime > 0) scheduler.runTaskLater(this, 1);
    }
}
