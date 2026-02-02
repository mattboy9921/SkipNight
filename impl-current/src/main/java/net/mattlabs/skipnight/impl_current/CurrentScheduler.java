package net.mattlabs.skipnight.impl_current;

import net.mattlabs.skipnight.api.ScheduledRunnable;
import net.mattlabs.skipnight.api.ScheduledTask;
import net.mattlabs.skipnight.api.Scheduler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class CurrentScheduler implements Scheduler {

    private Plugin plugin;

    public CurrentScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ScheduledTask runTaskLater(ScheduledRunnable task, long ticks) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskLater(plugin, task, ticks);

        ScheduledTask handle = new BukkitScheduledTask(bukkitTask);
        task.setTask(handle);
        return handle;
    }

    @Override
    public ScheduledTask runTaskTimer(ScheduledRunnable task, long delay, long period) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);

        ScheduledTask handle = new BukkitScheduledTask(bukkitTask);
        task.setTask(handle);
        return handle;
    }

    private static class BukkitScheduledTask implements ScheduledTask {
        private final BukkitTask delegate;

        private BukkitScheduledTask(BukkitTask delegate) {
            this.delegate = delegate;
        }

        @Override
        public void cancel() {
            delegate.cancel();
        }
    }
}
