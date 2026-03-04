package net.mattlabs.skipnight.api.util;

/**
 * Represents the type of vote currently being conducted.
 *
 * <p>This determines which portion of the Minecraft day/night cycle
 * the vote will attempt to skip.</p>
 */
public enum VoteType {

    /**
     * Vote to skip the current day and advance the world to night.
     */
    DAY,

    /**
     * Vote to skip the current night and advance the world to day.
     */
    NIGHT
}
