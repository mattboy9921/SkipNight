package net.mattlabs.skipnight.impl.spigot_1_13;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.player.PlayerAdapter;
import net.mattlabs.skipnight.impl.spigot_1_13.adapters.Spigot113PlayerAdapter;
import net.mattlabs.skipnight.impl.spigot_1_8.Spigot18ImplFactory;
import org.bukkit.plugin.Plugin;

public class Spigot113ImplFactory extends Spigot18ImplFactory {
    private final Plugin plugin;

    public Spigot113ImplFactory(Plugin plugin, BukkitAudiences platform) {
        super(plugin, platform);
        this.plugin = plugin;
    }

    @Override
    public PlayerAdapter playerAdapter() {
        return new Spigot113PlayerAdapter(plugin);
    }
}
