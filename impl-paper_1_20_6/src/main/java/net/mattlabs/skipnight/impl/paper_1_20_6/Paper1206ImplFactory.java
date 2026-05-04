package net.mattlabs.skipnight.impl.paper_1_20_6;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.commands.CommandAdapter;
import net.mattlabs.skipnight.impl.paper_1_20_6.adapters.Paper1206CommandAdapter;
import net.mattlabs.skipnight.impl.spigot_1_13.Spigot113ImplFactory;
import org.bukkit.plugin.Plugin;

public class Paper1206ImplFactory extends Spigot113ImplFactory {
    private final Plugin plugin;
    private final BukkitAudiences platform;

    public Paper1206ImplFactory(Plugin plugin, BukkitAudiences platform) {
        super(plugin, platform);
        this.plugin = plugin;
        this.platform = platform;
    }

    @Override
    public CommandAdapter commandAdapter() {
        return new Paper1206CommandAdapter(plugin, platform);
    }
}
