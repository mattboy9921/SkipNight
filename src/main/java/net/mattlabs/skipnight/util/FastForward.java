package net.mattlabs.skipnight.util;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class FastForward implements Runnable {

    private World world;
    private Plugin plugin;
    private VoteType voteType;

    public FastForward(World world, Plugin plugin, VoteType voteType) {
        this.world = world;
        this.plugin = plugin;
        this.voteType = voteType;
    }

    @Override
    public void run() {
        world.setTime(world.getTime() + 80);
        if (voteType == VoteType.NIGHT && world.getTime() > 12516 && world.getTime() < 23900) plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
        if (voteType == VoteType.DAY && (world.getTime() > 23900 || world.getTime() < 12516)) plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
    }
}
