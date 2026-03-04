package net.mattlabs.skipnight.api.player;

import java.util.Collection;
import java.util.UUID;

/**
 * Abstraction layer for retrieving player-related information.
 *
 * <p>Players are referenced exclusively by their {@link UUID} to avoid exposing
 * platform-specific player objects to the API layer.</p>
 */
public interface PlayerAdapter {

    /**
     * Returns the display name of the specified player.
     *
     * @param playerUUID the UUID of the player
     * @return the player's name
     */
    String getName(UUID playerUUID);

    /**
     * Returns the current activity tag for the player.
     *
     * <p>This tag represents the player's state during a vote and is used by the
     * voting system to determine whether the player is active, idle, away, or
     * currently in a bed.</p>
     *
     * @param playerUUID the UUID of the player
     * @return a string representing the player's activity state
     */
    String readTag(UUID playerUUID);

    /**
     * Returns the UUID of the world the player is currently in.
     *
     * @param playerUUID the UUID of the player
     * @return the UUID of the player's current world
     */
    UUID getWorldUUID(UUID playerUUID);

    /**
     * Checks whether the player has the specified permission.
     *
     * @param playerUUID the UUID of the player
     * @param permission the permission node to check
     * @return {@code true} if the player has the permission, otherwise {@code false}
     */
    boolean hasPermission(UUID playerUUID, String permission);

    /**
     * Determines whether the player is currently in the overworld.
     *
     * @param playerUUID the UUID of the player
     * @return {@code true} if the player is in the overworld
     */
    boolean isInOverworld(UUID playerUUID);

    /**
     * Determines whether the player is required to sleep due to phantom mechanics.
     *
     * <p>If this returns {@code true}, the player has not slept for 3 in-game
     * days and may be prevented from participating in certain votes depending on
     * plugin configuration.</p>
     *
     * @param playerUUID the UUID of the player
     * @return {@code true} if the player must sleep before voting
     */
    boolean playerMustSleep(UUID playerUUID);

    /**
     * Returns the UUIDs of all currently online players.
     *
     * @return a collection containing the UUID of each online player
     */
    Collection<UUID> getOnlinePlayers();
}
