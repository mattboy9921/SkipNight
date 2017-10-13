package net.mattlabs.skipnight;

import org.bukkit.entity.Player;

public class Voter {

    private Player player;
    private int vote;

    public Voter(Player player) {
        this.player = player;
        vote = 0;
    }

    public Player getPlayer() {
        return player;
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

        return player.equals(voter.player);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}
