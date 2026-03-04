package net.mattlabs.skipnight.api.world;

import java.util.List;
import java.util.UUID;

/**
 * Provides platform-independent access to world information and actions.
 *
 * <p>Worlds are identified by their {@link UUID} rather than a platform-
 * specific world object.</p>
 */
public interface WorldAdapter {

    /**
     * Gets the name of a world.
     *
     * @param worldUUID the UUID of the world
     * @return the world name
     */
    String getName(UUID worldUUID);

    /**
     * Gets the current time of the world.
     *
     * @param worldUUID the UUID of the world
     * @return the current world time in ticks
     */
    long getTime(UUID worldUUID);

    /**
     * Gets the players currently present in the world.
     *
     * @param worldUUID the UUID of the world
     * @return a list of player UUIDs in the world
     */
    List<UUID> getPlayers(UUID worldUUID);

    /**
     * Checks whether the world currently has an active storm.
     *
     * @param worldUUID the UUID of the world
     * @return {@code true} if the world is storming, otherwise {@code false}
     */
    boolean hasStorm(UUID worldUUID);

    /**
     * Sets the world's time.
     *
     * @param worldUUID the UUID of the world
     * @param time the new world time in ticks
     */
    void setTime(UUID worldUUID, long time);

    /**
     * Sets whether the world is storming.
     *
     * @param worldUUID the UUID of the world
     * @param hasStorm {@code true} to enable a storm, {@code false} to clear it
     */
    void setStorm(UUID worldUUID, boolean hasStorm);
}
