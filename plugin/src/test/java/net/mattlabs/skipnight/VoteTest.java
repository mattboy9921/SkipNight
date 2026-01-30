package net.mattlabs.skipnight;

import net.mattlabs.skipnight.util.VoteType;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.junit.jupiter.api.*;

@SuppressWarnings("ConstantConditions")
public abstract class VoteTest {
    
    ServerMock server;
    SkipNight plugin;
    World world;
    PlayerMock player1;
    PlayerMock player2;
    VoteType voteType;
    String voteTypeString;
    Vote vote;
    long startTime;
    long endTime;
    PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();

    abstract VoteType voteType();

    abstract long startTime();

    abstract long endTime();

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        SkipNight.testEnabled = true;
        plugin = MockBukkit.load(SkipNight.class);
        vote = SkipNight.getInstance().vote;
        startTime = startTime();
        endTime = endTime();
        voteType = voteType();
        voteTypeString = voteType().toString().toLowerCase();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test one player vote pass")
    public void votePassOne() {
        onePlayerSetup();
        world.setTime(startTime + 200);

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

        // Make sure time fast forwards
        Assertions.assertTrue(world.getTime() < startTime || world.getTime() > endTime);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().afterVote().votePassedBossBar(voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );
    }

    @Test
    @DisplayName("Test two player vote pass")
    public void votePassTwo() {
        twoPlayerSetup();
        world.setTime(startTime + 200);

        // First player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteStarted(player1.getName(), voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().youVoteYes()),
                plain.serialize(player1.nextComponentMessage())
        );

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteStarted(player1.getName(), voteTypeString)),
                plain.serialize(player2.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteButtons(voteTypeString)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Wait, then have second player vote yes
        server.getScheduler().performTicks(10 * 20);
        vote.addYes(player2, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().youVoteYes()),
                plain.serialize(player2.nextComponentMessage())
        );

        // Let vote process
        server.getScheduler().performTicks(60 * 20);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().afterVote().votePassedBossBar(voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().afterVote().votePassedBossBar(voteTypeString)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Make sure time fast forwards
        Assertions.assertTrue(world.getTime() < startTime || world.getTime() > endTime);
    }

    @Test
    @DisplayName("Test two player vote fail")
    public void voteFail() {
        twoPlayerSetup();
        world.setTime(startTime + 200);

        // First player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteStarted(player1.getName(), voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().youVoteYes()),
                plain.serialize(player1.nextComponentMessage())
        );

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteStarted(player1.getName(), voteTypeString)),
                plain.serialize(player2.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteButtons(voteTypeString)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Wait, then have second player vote no
        server.getScheduler().performTicks(10 * 20);
        vote.addNo(player2, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().youVoteNo()),
                plain.serialize(player2.nextComponentMessage())
        );

        // Let vote process
        server.getScheduler().performTicks(60 * 20);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().afterVote().voteFailedBossBar(voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().afterVote().voteFailedBossBar(voteTypeString)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Make sure time does not fast-forward
        Assertions.assertTrue(world.getTime() > startTime || world.getTime() < endTime);
    }

    @Test
    @DisplayName("Test vote with world blacklist")
    public void voteWorldBlacklisted() {
        onePlayerSetup();
        // Set up blacklisted world, bring player to it
        World blackListedWorld = server.addSimpleWorld(plugin.getConfiguration().getWorldBlacklist().get(0));
        player1.teleport(blackListedWorld.getSpawnLocation());
        blackListedWorld.setTime(startTime + 200);

        // Player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().worldIsBlacklisted()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time does not fast-forward
        Assertions.assertTrue(blackListedWorld.getTime() > startTime || blackListedWorld.getTime() < endTime);
    }

    @Test
    @DisplayName("Test vote while not in overworld")
    public void voteNotInOverworld() {
        onePlayerSetup();
        // Set up a nether world
        WorldMock nether = new WorldMock();
        nether.setEnvironment(World.Environment.NETHER);
        player1.teleport(nether.getSpawnLocation());

        // Player starts vote
        vote.start(player1, voteType);
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().worldNotOverworld()),
                plain.serialize(player1.nextComponentMessage())
        );
    }

    @Test
    @DisplayName("Test vote during cooldown")
    public void voteCooldown() {
        twoPlayerSetup();
        world.setTime(startTime + 200);

        // First player starts vote
        vote.start(player1, voteType);

        // Wait, then have second player vote no
        server.getScheduler().performTicks(10 * 20);
        vote.addNo(player2, voteType);

        // Let vote process
        server.getScheduler().performTicks(20 * 20);

        // First player starts vote again
        vote.start(player1, voteType);

        // Burn messages
        for (int i = 0; i < 3; i++)
            player1.nextComponentMessage();

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().beforeVote().cooldown()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Wait out cooldown
        server.getScheduler().performTicks(100 * 20);

        // First player starts vote again
        vote.start(player1, voteType);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteStarted(player1.getName(), voteTypeString)),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time does not fast-forward
        Assertions.assertTrue(world.getTime() > startTime || world.getTime() < endTime);
    }

    @Test
    @DisplayName("Test vote during vote")
    public void voteAlreadyInProg() {
        twoPlayerSetup();
        world.setTime(startTime + 200);

        // First player starts vote
        vote.start(player1, voteType);

        // Wait, then have second player start a vote
        server.getScheduler().performTicks(10 * 20);
        vote.start(player2, voteType);

        // Burn messages
        for (int i = 0; i < 2; i++)
            player2.nextComponentMessage();

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().duringVote().voteInProg()),
                plain.serialize(player2.nextComponentMessage())
        );
    }

    // Set up one player with permissions in the default world
    void onePlayerSetup() {
        player1 = server.addPlayer("Player1");
        player1.addAttachment(plugin, "skipnight.vote." + voteType, true);
        world = server.getWorld("world");
    }

    // Set up two players with permissions in the default world
    void twoPlayerSetup() {
        onePlayerSetup();
        player2 = server.addPlayer("Player2");
        player2.addAttachment(plugin, "skipnight.vote." + voteType, true);
    }
}