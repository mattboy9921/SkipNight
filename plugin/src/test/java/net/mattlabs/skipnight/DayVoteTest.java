package net.mattlabs.skipnight;

import net.mattlabs.skipnight.util.VoteType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ConstantConditions")
public class DayVoteTest extends VoteTest {

    @Override
    VoteType voteType() {
        return VoteType.DAY;
    }

    @Override
    long startTime() {
        return 23900;
    }

    @Override
    long endTime() {
        return 12516;
    }

    @Test
    @DisplayName("Test vote during the night")
    public void voteDuringNight() {
        onePlayerSetup();
        world.setTime(16000);

        // Player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().canOnlyVoteAtDay()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure it is still night
        Assertions.assertTrue(world.getTime() < startTime && world.getTime() > endTime);
    }
}