package net.mattlabs.skipnight;

import io.papermc.paper.block.bed.BedEnterAction;
import io.papermc.paper.block.bed.BedEnterProblem;
import io.papermc.paper.block.bed.BedRuleResult;
import net.kyori.adventure.text.Component;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

@SuppressWarnings("ConstantConditions")
public class NightVoteTest extends VoteTest {

    @Override
    VoteType voteType() {
        return VoteType.NIGHT;
    }

    @Override
    long startTime() {
        return 12516;
    }

    @Override
    long endTime() {
        return 23900;
    }

    BedEnterAction bedEnterAction = new BedEnterAction() {
        @Override
        public BedRuleResult canSleep() {
            return BedRuleResult.ALLOWED;
        }

        @Override
        public BedRuleResult canSetSpawn() {
            return BedRuleResult.ALLOWED;
        }

        @Override
        public @Nullable BedEnterProblem problem() {
            return null;
        }

        @Override
        public @Nullable Component errorMessage() {
            return null;
        }
    };

    @Test
    @DisplayName("Test vote during the day")
    public void voteDuringDay() {
        onePlayerSetup();
        world.setTime(8000);

        // Player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().canOnlyVoteAtNight()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure it is still day
        Assertions.assertTrue(world.getTime() < startTime || world.getTime() > endTime);
    }

    @Test
    @DisplayName("Test vote during thunderstorm")
    public void voteDuringStorm() {
        onePlayerSetup();
        world.setTime(8000);
        world.setStorm(true);

        // Player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteStarted(player1.getName(), voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().youVoteYes()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Let vote process
        server.getScheduler().performTicks(60 * 20);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().afterVote().votePassedBossBar(voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time fast forwards
        Assertions.assertTrue(world.getTime() < startTime || world.getTime() > endTime);
    }

    @Test
    @DisplayName("Test start vote needs to sleep")
    public void voteStartVoteNeedSleep() {
        onePlayerSetup();
        // Set player has not slept in over 3 days
        player1.setStatistic(Statistic.TIME_SINCE_REST, 73000);
        world.setTime(13000);

        // Player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().mustSleepNewVote()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time does not fast-forward
        Assertions.assertTrue(world.getTime() > 12516 && world.getTime() < 23900);
    }

    @Test
    @DisplayName("Test vote in progress needs to sleep")
    public void voteVoteNeedSleep() {
        twoPlayerSetup();
        // Set player has not slept in over 3 days
        player2.setStatistic(Statistic.TIME_SINCE_REST, 73000);
        world.setTime(13000);

        // First player starts vote
        vote.start(player1, voteType);

        // Wait, then have second player vote yes
        server.getScheduler().performTicks(10 * 20);
        vote.addYes(player2, voteType);

        // Burn messages
        for (int i = 0; i < 2; i++)
            player2.nextComponentMessage();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().mustSleep()),
                plain.serialize(player2.nextComponentMessage())
        );
    }

    @Test
    @DisplayName("Test one player sleep message")
    public void voteOnePlayerSleep() {
        onePlayerSetup();
        world.setTime(13000);

        // Place bed and sleep
        Block bed = player1.simulateBlockPlace(Material.RED_BED, player1.getLocation()).getBlockPlaced();
        Future<?> future = server.getScheduler().executeAsyncEvent(new PlayerBedEnterEvent(player1, bed, PlayerBedEnterEvent.BedEnterResult.OK, bedEnterAction));

        // Wait for event to execute
        while (!future.isDone()) server.getScheduler().performOneTick();

        // Check for no message on bed enter
        Assertions.assertNull(player1.nextComponentMessage());
    }

    @Test
    @DisplayName("Test two player sleep message")
    public void voteTwoPlayerSleep() {
        twoPlayerSetup();
        world.setTime(13000);

        // Place bed and sleep
        Block bed = player1.simulateBlockPlace(Material.RED_BED, player1.getLocation()).getBlockPlaced();
        player1.setSleeping(true);
        Future<?> future = server.getScheduler().executeAsyncEvent(new PlayerBedEnterEvent(player1, bed, PlayerBedEnterEvent.BedEnterResult.OK, bedEnterAction));

        // Wait for event to execute
        while (!future.isDone()) server.getScheduler().performOneTick();

        // Check for no message on bed enter
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().inBedNoVoteInProg()),
                plain.serialize(player1.nextComponentMessage())
        );
    }

    @Test
    @DisplayName("Test bed enter during vote")
    public void voteBedEnterDuringVote() {
        twoPlayerSetup();
        world.setTime(13000);

        // First player starts vote
        vote.start(player1, voteType);

        // Second player places bed and sleeps
        Block bed = player2.simulateBlockPlace(Material.RED_BED, player2.getLocation()).getBlockPlaced();
        Future<?> future = server.getScheduler().executeAsyncEvent(new PlayerBedEnterEvent(player2, bed, PlayerBedEnterEvent.BedEnterResult.OK, bedEnterAction));
        player2.setSleeping(true);

        // Wait for event to execute
        while (!future.isDone()) server.getScheduler().performOneTick();

        // Burn messages
        for (int i = 0; i < 2; i++)
            player2.nextComponentMessage();

        // Check for message on bed enter
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().inBedVotedYes()),
                plain.serialize(player2.nextComponentMessage())
        );
    }

    @Test
    @DisplayName("Test bed enter before vote")
    public void voteBedEnterBeforeVote() {
        twoPlayerSetup();
        world.setTime(13000);

        // First player places bed and sleeps
        Block bed = player1.simulateBlockPlace(Material.RED_BED, player1.getLocation()).getBlockPlaced();
        Future<?> future = server.getScheduler().executeAsyncEvent(new PlayerBedEnterEvent(player1, bed, PlayerBedEnterEvent.BedEnterResult.OK, bedEnterAction));
        player1.setSleeping(true);

        // Wait for event to execute
        while (!future.isDone()) server.getScheduler().performOneTick();

        // Check for message on bed enter
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().inBedNoVoteInProg()),
                plain.serialize(player1.nextComponentMessage())
        );
    }
}