package net.mattlabs.skipnight.api;

public interface Scheduler {
    ScheduledTask runTaskLater(ScheduledRunnable task, long ticks);

    ScheduledTask runTaskTimer(ScheduledRunnable task, long delay, long period);
}