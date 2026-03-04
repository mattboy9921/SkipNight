package net.mattlabs.skipnight.api.core;

import java.util.UUID;

/**
 * Represents a single player's participation state within an active vote.
 *
 * <p>A {@code Voter} tracks three pieces of information:</p>
 * <ul>
 *     <li>The player's unique identifier</li>
 *     <li>Their vote choice ({@code 1} = yes, {@code -1} = no, {@code 0} = not yet voted)</li>
 *     <li>Their current activity status ({@code ACTIVE}, {@code BED}, {@code IDLE}, {@code AWAY})</li>
 * </ul>
 *
 * <p>This class is used internally by {@link net.mattlabs.skipnight.api.core.Vote}
 * to determine vote progress, handle automatic voting behavior (e.g. voting yes
 * when entering a bed), and manage per-player state transitions during a vote.</p>
 *
 * <p>Two {@code Voter} instances are considered equal if they represent the same
 * player UUID, allowing them to be used as map keys or set members.</p>
 */
public class Voter {

    private enum Status {
        ACTIVE,
        BED,
        IDLE,
        AWAY
    }

    private final UUID uuid;
    private int vote;

    private Status status;

    Voter(UUID uuid) {
        this.uuid = uuid;
        vote = 0;
        status = null;
    }

    /**
     * Returns the UUID of the player represented by this voter.
     */
    UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the player's vote:
     * <ul>
     *   <li>{@code 1} for yes</li>
     *   <li>{@code -1} for no</li>
     *   <li>{@code 0} if not yet voted</li>
     * </ul>
     */
    int getVote() {
        return vote;
    }

    /**
     * Records a "yes" vote for this player.
     */
    void voteYes() {
        vote = 1;
    }

    /**
     * Records a "no" vote for this player.
     */
    void voteNo() {
        vote = -1;
    }

    /**
     * Resets this player's vote to "not yet voted".
     */
    void resetVote() {
        this.vote = 0;
    }

    /**
     * @return true if the player is marked as active.
     */
    boolean isActive() {
        return status == Status.ACTIVE;
    }

    /**
     * @return true if the player is in a bed.
     */
    boolean isBed() {
        return status == Status.BED;
    }

    /**
     * @return true if the player is marked as idle.
     */
    boolean isIdle() {
        return status == Status.IDLE;
    }

    /**
     * @return true if the player is marked as away.
     */
    boolean isAway() {
        return status == Status.AWAY;
    }

    /**
     * Marks the player as active.
     */
    void setActive() {
        status = Status.ACTIVE;
    }

    /**
     * Marks the player as in a bed.
     */
    void setBed() {
        status = Status.BED;
    }

    /**
     * Mark the player as idle.
     */
    void setIdle() {
        status = Status.IDLE;
    }

    /**
     * Mark the player as away.
     */
    void setAway() {
        status = Status.AWAY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Voter voter = (Voter) o;

        return uuid.equals(voter.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
