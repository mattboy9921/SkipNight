package net.mattlabs.skipnight.util;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class FastForward implements Runnable {

    private World world;
    private Plugin plugin;
    private VoteType voteType;
    private boolean storm;

    public FastForward(World world, Plugin plugin, VoteType voteType) {
        this.world = world;
        this.plugin = plugin;
        this.voteType = voteType;
        storm = voteType == VoteType.NIGHT && (world.getTime() > 23900 || world.getTime() < 12516) && world.hasStorm();
    }

    @Override
    public void run() {
        world.setTime(world.getTime() + 80);

        // Fast-forward day to night with storm
        if (voteType == VoteType.NIGHT && (world.getTime() > 23900 || world.getTime() < 12516) && storm) {
            world.setStorm(true);
            world.setWeatherDuration(1000);
            plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
        }
        // Disable storm once it is night
        if (voteType == VoteType.NIGHT && (world.getTime() > 12516 && world.getTime() < 23900) && storm) {
            world.setStorm(false);
            storm = false;
        }

        if (voteType == VoteType.NIGHT && (world.getTime() > 12516 && world.getTime() < 23900)) plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
        if (voteType == VoteType.DAY && (world.getTime() > 23900 || world.getTime() < 12516)) plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
    }
}
