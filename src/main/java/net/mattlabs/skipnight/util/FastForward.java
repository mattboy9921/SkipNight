package net.mattlabs.skipnight.util;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class FastForward implements Runnable {

    private World world;
    private Plugin plugin;

    public FastForward(World world, Plugin plugin) {
        this.world = world;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        world.setTime(world.getTime() + 80);
        if (world.getTime() < 23900) plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
    }
}
