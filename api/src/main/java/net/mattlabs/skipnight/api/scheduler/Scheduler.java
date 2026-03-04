package net.mattlabs.skipnight.api.scheduler;

/**
 * Platform-independent scheduler used by SkipNight to execute delayed and
 * repeating tasks.
 *
 * <p>This interface abstracts the underlying server scheduler.</p>
 *
 * <p>Tasks are executed using {@link ScheduledRunnable} instances and return
 * a {@link ScheduledTask} handle which can be used to cancel the task.</p>
 */
public interface Scheduler {

    /**
     * Schedules a task to run once after a delay.
     *
     * @param task the task to execute
     * @param ticks the delay before execution in ticks
     * @return a handle representing the scheduled task
     */
    ScheduledTask runTaskLater(ScheduledRunnable task, long ticks);

    /**
     * Schedules a repeating task.
     *
     * @param task the task to execute
     * @param delay the initial delay before the first execution in ticks
     * @param period the interval between subsequent executions in ticks
     * @return a handle representing the scheduled task
     */
    ScheduledTask runTaskTimer(ScheduledRunnable task, long delay, long period);
}