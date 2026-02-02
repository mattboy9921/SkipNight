package net.mattlabs.skipnight.api;

public abstract class ScheduledRunnable implements Runnable {
    private ScheduledTask task;

    public void setTask(ScheduledTask task) {
        this.task = task;
    }

    public void cancel() {
        if (task != null) task.cancel();
    }
}
