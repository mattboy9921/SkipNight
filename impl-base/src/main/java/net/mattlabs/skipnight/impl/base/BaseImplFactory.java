package net.mattlabs.skipnight.impl.base;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.SkipNightImplFactory;
import net.mattlabs.skipnight.api.commands.CommandAdapter;
import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.api.events.EventListener;
import net.mattlabs.skipnight.api.messaging.MessagesAdapter;
import net.mattlabs.skipnight.api.player.PlayerAdapter;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.world.WorldAdapter;
import net.mattlabs.skipnight.impl.base.adapters.BaseCommandAdapter;
import net.mattlabs.skipnight.impl.base.adapters.BaseMessageAdapter;
import net.mattlabs.skipnight.impl.base.adapters.BasePlayerAdapter;
import net.mattlabs.skipnight.impl.base.adapters.BaseWorldAdapter;
import net.mattlabs.skipnight.impl.base.events.BaseEventListener;
import net.mattlabs.skipnight.impl.base.scheduler.BaseScheduler;
import org.bukkit.plugin.Plugin;

public class BaseImplFactory implements SkipNightImplFactory {
    private final Plugin plugin;
    private final BukkitAudiences platform;

    public BaseImplFactory(Plugin plugin, BukkitAudiences platform) {
        this.plugin = plugin;
        this.platform = platform;
    }

    @Override
    public Scheduler scheduler() {
        return new BaseScheduler(plugin);
    }

    @Override
    public PlayerAdapter playerAdapter() {
        return new BasePlayerAdapter(plugin);
    }

    @Override
    public WorldAdapter worldAdapter() {
        return new BaseWorldAdapter();
    }

    @Override
    public MessagesAdapter messagesAdapter() {
        return new BaseMessageAdapter(platform);
    }

    @Override
    public EventListener eventListener(Vote vote) {
        return new BaseEventListener(vote);
    }

    @Override
    public CommandAdapter commandAdapter() {
        return new BaseCommandAdapter(plugin, platform);
    }
}
