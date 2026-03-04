package net.mattlabs.skipnight.api.scheduler;

/**
 * Represents a handle to a scheduled task created by the SkipNight scheduler.
 *
 * <p>This interface provides a platform-independent way to manage scheduled
 * tasks. Implementations typically wrap the task objects provided by the
 * underlying server platform (for example, {@code BukkitTask} in Bukkit-based
 * environments).</p>
 *
 * <p>Instances of this interface are usually assigned to a
 * {@link ScheduledRunnable} when it is scheduled, allowing the runnable to
 * cancel itself via {@link ScheduledRunnable#cancel()}.</p>
 */
public interface ScheduledTask {

    /**
     * Cancels the scheduled task, preventing any future executions.
     */
    void cancel();
}
