package net.mattlabs.skipnight;

import java.util.UUID;

public class Voter {

    private UUID uuid;
    private int vote;

    Voter(UUID uuid) {
        this.uuid = uuid;
        vote = 0;
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

    int resetVote() {
        int vote = this.vote;
        this.vote = 0;
        return vote;
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
