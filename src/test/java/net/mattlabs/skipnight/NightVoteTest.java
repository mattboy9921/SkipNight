package net.mattlabs.skipnight;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;

public class NightVoteTest {

    private static ServerMock server;
    private static SkipNight plugin;
    private World world;
    private static PlayerMock player1;
    private static PlayerMock player2;
    private static String voteType;
    private PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        SkipNight.testEnabled = true;
        plugin = MockBukkit.load(SkipNight.class);
        voteType = plugin.getMessages().getNightString();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test one player night vote pass")
    public void voteSkipNightPassOne() {
        onePlayerSetup();
        world.setTime(13000);

        // Player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteStarted(player1.getName(), voteType)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().youVoteYes()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Let vote process
        server.getScheduler().performTicks(60 * 20);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().votePassedBossBar(voteType)),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time fast forwards
        Assertions.assertTrue(world.getTime() < 12516 || world.getTime() > 23900);
    }

    @Test
    @DisplayName("Test two player night vote pass")
    public void voteSkipNightPassTwo() {
        twoPlayerSetup();
        world.setTime(13000);

        // First player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteStarted(player1.getName(), voteType)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().youVoteYes()),
                plain.serialize(player1.nextComponentMessage())
        );

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteStarted(player1.getName(), voteType)),
                plain.serialize(player2.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteButtons(voteType)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Wait, then have second player vote yes
        server.getScheduler().performTicks(10 * 20);
        server.execute("skipnight", player2, "yes").assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().youVoteYes()),
                plain.serialize(player2.nextComponentMessage())
        );

        // Let vote process
        server.getScheduler().performTicks(60 * 20);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().votePassedBossBar(voteType)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().votePassedBossBar(voteType)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Make sure time fast forwards
        Assertions.assertTrue(world.getTime() < 12516 || world.getTime() > 23900);
    }

    @Test
    @DisplayName("Test two player night vote fail")
    public void voteSkipNightFail() {
        twoPlayerSetup();
        world.setTime(13000);

        // First player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteStarted(player1.getName(), voteType)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().youVoteYes()),
                plain.serialize(player1.nextComponentMessage())
        );

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteStarted(player1.getName(), voteType)),
                plain.serialize(player2.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteButtons(voteType)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Wait, then have second player vote no
        server.getScheduler().performTicks(10 * 20);
        server.execute("skipnight", player2, "no").assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().youVoteNo()),
                plain.serialize(player2.nextComponentMessage())
        );

        // Let vote process
        server.getScheduler().performTicks(60 * 20);

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteFailedBossBar(voteType)),
                plain.serialize(player1.nextComponentMessage())
        );
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().voteFailedBossBar(voteType)),
                plain.serialize(player2.nextComponentMessage())
        );

        // Make sure time does not fast forward
        Assertions.assertTrue(world.getTime() > 12516 && world.getTime() < 23900);
    }

    @Test
    @DisplayName("Test permission")
    public void voteSkipNightNoPerm() {
        onePlayerSetup();
        player1.addAttachment(plugin, "skipnight.vote." + voteType, false);
        world.setTime(13000);

        // Player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                "I'm sorry, but you do not have permission to perform this command.",
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time does not fast forward
        Assertions.assertTrue(world.getTime() > 12516 && world.getTime() < 23900);
    }

    @Test
    @DisplayName("Test world blacklist")
    public void voteSkipNightWorldBlacklisted() {
        onePlayerSetup();
        // Set up blacklisted world, bring player to it
        World blackListedWorld = server.addSimpleWorld(plugin.getConfiguration().getWorldBlacklist().get(0));
        player1.teleport(blackListedWorld.getSpawnLocation());
        blackListedWorld.setTime(13000);

        // Player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().worldIsBlacklisted()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time does not fast forward
        Assertions.assertTrue(blackListedWorld.getTime() > 12516 && blackListedWorld.getTime() < 23900);
    }

    @Test
    @DisplayName("Test not in overworld")
    public void voteSkipNightNotInOverworld() {
        onePlayerSetup();
        // Set up a nether world
        WorldMock nether = new WorldMock();
        nether.setEnvironment(World.Environment.NETHER);
        player1.teleport(nether.getSpawnLocation());

        // Player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().worldNotOverworld()),
                plain.serialize(player1.nextComponentMessage())
        );
    }

    @Test
    @DisplayName("Test vote during the day")
    public void voteSkipNightDuringDay() {
        onePlayerSetup();
        world.setTime(8000);

        // Player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().canOnlyVoteAtNight()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure it is still day
        Assertions.assertTrue(world.getTime() < 12516 || world.getTime() > 23900);
    }

    @Test
    @DisplayName("Test vote fail cooldown")
    public void voteSkipNightCooldown() {
        twoPlayerSetup();
        world.setTime(13000);

        // First player starts vote
        server.execute("skipnight", player1).assertSucceeded();
        server.getLogger().info(player1.nextMessage());

        // Wait, then have second player vote no
        server.getScheduler().performTicks(10 * 20);
        server.execute("skipnight", player2, "no").assertSucceeded();

        // Let vote process
        server.getScheduler().performTicks(20 * 20);

        // First player starts vote again
        server.execute("skipnight", player1).assertSucceeded();

        // Burn messages
        for (int i = 0; i < 3; i++)
            player1.nextComponentMessage();

        Assertions.assertEquals(
                plain.serialize(plugin.getMessages().cooldown()),
                plain.serialize(player1.nextComponentMessage())
        );

        // Make sure time does not fast forward
        Assertions.assertTrue(world.getTime() > 12516 && world.getTime() < 23900);
    }

    // Set up one player with permissions in the default world
    private void onePlayerSetup() {
        player1 = server.addPlayer("Player1");
        player1.addAttachment(plugin, "skipnight.vote." + voteType, true);
        world = server.getWorld("world");
    }

    // Set up two players with permissions in the default world
    private void twoPlayerSetup() {
        onePlayerSetup();
        player2 = server.addPlayer("Player2");
        player2.addAttachment(plugin, "skipnight.vote." + voteType, true);
    }
}
