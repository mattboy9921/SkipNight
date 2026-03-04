package net.mattlabs.skipnight.impl_current.adapters;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.mattlabs.skipnight.api.messaging.MessagesAdapter;

import java.util.UUID;

public class CurrentMessagesAdapter implements MessagesAdapter {

    private final BukkitAudiences platform;

    public CurrentMessagesAdapter(BukkitAudiences platform) {
        this.platform = platform;
    }

    @Override
    public void sendMessage(UUID recipientUUID, Component message) {
        platform.player(recipientUUID).sendMessage(message);
    }

    @Override
    public void sendActionBar(UUID playerUUID, Component message) {
        platform.player(playerUUID).sendActionBar(message);
    }

    @Override
    public void hideBossBarAll(BossBar bar) {
        platform.all().hideBossBar(bar);
    }

    @Override
    public void showBossBar(UUID playerUUID, BossBar bar) {
        platform.player(playerUUID).showBossBar(bar);
    }
}
