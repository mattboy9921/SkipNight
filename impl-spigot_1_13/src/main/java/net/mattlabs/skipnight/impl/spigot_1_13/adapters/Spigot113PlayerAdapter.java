package net.mattlabs.skipnight.impl.spigot_1_13.adapters;

import net.mattlabs.skipnight.impl.base.adapters.BasePlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class Spigot113PlayerAdapter extends BasePlayerAdapter {

    public Spigot113PlayerAdapter(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean playerMustSleep(UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID).getStatistic(Statistic.TIME_SINCE_REST) >= 72000;
    }
}
