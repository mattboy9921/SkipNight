package net.mattlabs.skipnight;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ConstantConditions")
public class DayVoteTest extends VoteTest {

    @Override
    String voteType() {
        return "day";
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
        commandManager.executeCommand(player1, "skip" + voteType).join();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().canOnlyVoteAtDay()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure it is still night
        Assertions.assertTrue(world.getTime() < startTime && world.getTime() > endTime);
    }
}