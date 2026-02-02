package net.mattlabs.skipnight.plugin;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.*;

import java.io.File;

public class ConfigTest {

    ServerMock server;
    SkipNight plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        SkipNight.testEnabled = true;
        plugin = MockBukkit.load(SkipNight.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Check config file exists")
    public void doesConfigExist() {
        File file = new File(plugin.getDataFolder(), "config.conf");
        Assertions.assertTrue(file.exists());
    }
    @Test
    @DisplayName("Check messages file exists")
    public void doesMessagesExist() {
        File file = new File(plugin.getDataFolder(), "messages.conf");
        Assertions.assertTrue(file.exists());
    }

    @Test
    @DisplayName("Read config value")
    public void readConfigValue() {
        Assertions.assertEquals(true, plugin.getConfiguration().isSkipNight());
        Assertions.assertEquals(30, plugin.getConfiguration().getCooldown());
    }

    @Test
    @DisplayName("Read messages value")
    public void readMessagesValue() {
        Assertions.assertEquals(
                " - Please vote: [Yes] [No]",
                PlainTextComponentSerializer.plainText().serialize(plugin.getMessages().duringVote().voteButtons("night")));
    }
}
