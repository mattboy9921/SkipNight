package net.mattlabs.skipnight.plugin;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.SkipNightImplFactory;
import net.mattlabs.skipnight.api.config.Config;
import net.mattlabs.skipnight.api.config.LegacyConfigHelper;
import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.api.messaging.Messages;
import net.mattlabs.skipnight.api.messaging.MessagesAdapter;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.util.Versions;
import net.mattlabs.skipnight.api.config.ConfigurateManager;
import net.mattlabs.skipnight.api.util.MessageTransformations;
import net.mattlabs.skipnight.impl.paper_1_20_6.Paper1206ImplFactory;
import net.mattlabs.skipnight.impl.spigot_1_13.Spigot113ImplFactory;
import net.mattlabs.skipnight.impl.spigot_1_8.Spigot18ImplFactory;
import net.mattlabs.skipnight.plugin.util.PluginMessagesContext;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkipNight extends JavaPlugin {

    public Vote vote;
    private Config config;
    private Messages messages;
    private static SkipNight instance;
    private BukkitAudiences platform;
    private String version;
    private Scheduler scheduler;
    private net.mattlabs.skipnight.api.player.PlayerAdapter playerAdapter;
    private net.mattlabs.skipnight.api.world.WorldAdapter worldAdapter;
    private MessagesAdapter messagesAdapter;
    private net.mattlabs.skipnight.api.commands.CommandAdapter commandAdapter;
    private SkipNightImplFactory impl;

    public static boolean testEnabled = false;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        instance = this;

        // Disable if version below 1.8.0
        if (Versions.versionCompare("1.8.0", Versions.versionSubstring(Bukkit.getVersion())) >= 0) {
            getLogger().severe("You are running MC " + version + ". This plugin requires MC 1.8.0 or higher, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Configuration Section

        // Convert old YAML file if it exists still
        this.getDataFolder().mkdir();
        LegacyConfigHelper.convertConfig(new File(this.getDataFolder(), "config.yml"), this.getDataFolder(), getLogger());

        config = null;
        messages = null;

        // Configurate
        ConfigurateManager configurateManager = new ConfigurateManager(getDataFolder(), getLogger());

        configurateManager.add("config.conf", TypeToken.get(Config.class), new Config(), Config::new);
        configurateManager.add("messages.conf", TypeToken.get(Messages.class), new Messages(), Messages::new, MessageTransformations.create());

        if (!configurateManager.saveDefaults("config.conf")) getServer().getPluginManager().disablePlugin(this);
        if (!configurateManager.saveDefaults("messages.conf")) getServer().getPluginManager().disablePlugin(this);

        configurateManager.load("config.conf");
        configurateManager.load("messages.conf");

        configurateManager.save("config.conf");
        configurateManager.save("messages.conf");

        config = configurateManager.get("config.conf");
        messages = configurateManager.get("messages.conf");

        // MessagesContext
        Messages.Initialize(new PluginMessagesContext(messages, config));

        // Register Audience (Messages)
        platform = BukkitAudiences.create(this);

        // Check if we are running modern Paper
        boolean isPaper = false;
        try {
            Class.forName("io.papermc.paper.plugin.configuration.PluginMeta");
            isPaper = true;
        }
        catch (ClassNotFoundException ignored){}

        // Get version
        version = Versions.versionSubstring(Bukkit.getVersion());
        getLogger().info("Detected server version: " + version);

        // Check for Paper 1.20.6+
        if (Versions.versionCompare("1.20.6", version) <= 0 && isPaper) {
            getLogger().info("Enabling Paper 1.20.6 features...");
            impl = new Paper1206ImplFactory(this, platform);
        }
        // Check for Spigot/Paper 1.13.0+
        else if (Versions.versionCompare("1.13.0", version) <= 0) {
            getLogger().info("Enabling Spigot 1.13.0+ features...");
            impl = new Spigot113ImplFactory(this, platform);
        }
        // Fallback to Spigot/Paper 1.8.0+
        else {
            getLogger().info("Enabling Spigot 1.8-1.12 features...");
            impl = new Spigot18ImplFactory(this, platform);
        }

        // Create Implementations
        scheduler = impl.scheduler();
        playerAdapter = impl.playerAdapter();
        worldAdapter = impl.worldAdapter();
        messagesAdapter = impl.messagesAdapter();

        // Register Vote
        vote = new Vote(messages, hasPlayerActivity(), config, scheduler, playerAdapter, worldAdapter, messagesAdapter);

        // Register Listeners
        getServer().getPluginManager().registerEvents((Listener) impl.eventListener(vote), this);

        // Commands
        if (!testEnabled) {
            commandAdapter = impl.commandAdapter();
            commandAdapter.registerFramework();
            if (config.isSkipNight() || testEnabled) commandAdapter.registerSkipNightCommand(vote);
            if (config.isSkipDay() || testEnabled) commandAdapter.registerSkipDayCommand(vote);
        }

        // bStats
        if (!testEnabled) new Metrics(this,  	5796);

        // PlayerActivity Integration
        if (!hasPlayerActivity()) getLogger().info("PlayerActivity not found, disabling Idle/Away features");

        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz, Aikar, mbaxter, zml, Selida and ViMaSter)");
    }

    public void onDisable() {
        platform.close();
    }

    public static SkipNight getInstance() {
        return instance;
    }

    public Messages getMessages() {
        return messages;
    }

    public BukkitAudiences getPlatform() {
        return platform;
    }

    public String getVersion() {
        return version;
    }

    public Config getConfiguration() {
        return config;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public net.mattlabs.skipnight.api.player.PlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    public net.mattlabs.skipnight.api.world.WorldAdapter getWorldAdapter() {
        return worldAdapter;
    }

    public boolean hasPlayerActivity() {
        return getServer().getPluginManager().getPlugin("PlayerActivity") != null;
    }

    private void registerVote() {
        vote = new Vote(messages, hasPlayerActivity(), config, scheduler, playerAdapter, worldAdapter, messagesAdapter);
    }
}
