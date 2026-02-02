package net.mattlabs.skipnight.plugin.util;

import net.mattlabs.skipnight.api.ScheduledRunnable;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class FastForward extends ScheduledRunnable {

    private final World world;
    private final Plugin plugin;
    private final VoteType voteType;

    public FastForward(World world, Plugin plugin, VoteType voteType) {
        this.world = world;
        this.plugin = plugin;
        this.voteType = voteType;
    }

    @Override
    public void run() {
        long totalTime = voteType == VoteType.DAY ? 12541 - world.getTime() : 24000 - world.getTime();
        world.setTime(world.getTime() + 80);
        totalTime -= 80;
        if (totalTime < 80 && (voteType == VoteType.NIGHT && world.hasStorm())) world.setStorm(false);
        if (totalTime > 0) plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
    }
}
