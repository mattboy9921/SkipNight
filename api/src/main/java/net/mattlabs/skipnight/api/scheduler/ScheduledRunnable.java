package net.mattlabs.skipnight.api.scheduler;

/**
 * A {@link Runnable} that can be scheduled and later cancelled through the
 * SkipNight scheduler abstraction.
 *
 * <p>This class allows scheduled tasks to maintain a reference to the
 * {@link ScheduledTask} created by the platform-specific scheduler
 * implementation. Once the task is assigned, the runnable can cancel
 * its own execution using {@link #cancel()}.</p>
 *
 * <p>Platform implementations of {@link Scheduler} are responsible for
 * assigning the {@link ScheduledTask} via {@link #setTask(ScheduledTask)}
 * when the runnable is scheduled.</p>
 */
public abstract class ScheduledRunnable implements Runnable {
    private ScheduledTask task;

    /**
     * Associates the scheduled task handle with this runnable.
     *
     * <p>This method is typically called by the scheduler implementation
     * when the runnable is scheduled.</p>
     *
     * @param task the scheduled task handle
     */
    public void setTask(ScheduledTask task) {
        this.task = task;
    }

    /**
     * Cancels the scheduled task if it is currently active.
     */
    public void cancel() {
        if (task != null) task.cancel();
    }
}
