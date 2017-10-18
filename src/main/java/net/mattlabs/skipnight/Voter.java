package net.mattlabs.skipnight;

import java.util.UUID;

public class Voter {

    private UUID uuid;
    private int vote;

    public Voter(UUID uuid) {
        this.uuid = uuid;
        vote = 0;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getVote() {
        return vote;
    }

    public void voteYes() {
        vote = 1;
    }

    public void voteNo() {
        vote = -1;
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
