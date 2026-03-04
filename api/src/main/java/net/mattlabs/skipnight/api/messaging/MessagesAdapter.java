package net.mattlabs.skipnight.api.messaging;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * Abstraction layer for sending messages and managing boss bars for players.
 *
 * <p>All players are referenced by their {@link UUID} to avoid dependencies on
 * platform-specific player objects.</p>
 */
public interface MessagesAdapter {

    /**
     * Sends a standard chat message to a player.
     *
     * @param recipientUUID the UUID of the player receiving the message
     * @param message the message component to send
     */
    void sendMessage(UUID recipientUUID, Component message);

    /**
     * Sends an action bar message to a player.
     *
     * @param playerUUID the UUID of the player receiving the action bar message
     * @param message the action bar message component
     */
    void sendActionBar(UUID playerUUID, Component message);

    /**
     * Hides the specified boss bar from all players currently viewing it.
     *
     * @param bar the boss bar to remove from players' screens
     */
    void hideBossBarAll(BossBar bar);

    /**
     * Displays the specified boss bar to a player.
     *
     * @param playerUUID the UUID of the player who should see the boss bar
     * @param bar the boss bar to display
     */
    void showBossBar(UUID playerUUID, BossBar bar);
}
