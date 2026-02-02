package net.mattlabs.skipnight.plugin;

import java.util.UUID;

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

    UUID getUuid() {
        return uuid;
    }

    int getVote() {
        return vote;
    }

    void voteYes() {
        vote = 1;
    }

    void voteNo() {
        vote = -1;
    }

    void resetVote() {
        this.vote = 0;
    }

    boolean isActive() {
        return status == Status.ACTIVE;
    }

    boolean isBed() {
        return status == Status.BED;
    }

    boolean isIdle() {
        return status == Status.IDLE;
    }

    boolean isAway() {
        return status == Status.AWAY;
    }

    void setActive() {
        status = Status.ACTIVE;
    }

    void setBed() {
        status = Status.BED;
    }

    void setIdle() {
        status = Status.IDLE;
    }

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
