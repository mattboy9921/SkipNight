package net.mattlabs.skipnight.api.messaging;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface MessagesAdapter {

    void sendMessage(UUID recipientUUID, Component message);

    void sendActionBar(UUID playerUUID, Component message);

    void hideBossBarAll(BossBar bar);

    void showBossBar(UUID playerUUID, BossBar bar);
}
