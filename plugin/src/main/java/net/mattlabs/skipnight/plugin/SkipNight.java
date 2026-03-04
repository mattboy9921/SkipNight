package net.mattlabs.skipnight.plugin;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.commands.CommandAdapter;
import net.mattlabs.skipnight.api.config.Config;
import net.mattlabs.skipnight.api.config.LegacyConfigHelper;
import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.api.messaging.Messages;
import net.mattlabs.skipnight.api.messaging.MessagesAdapter;
import net.mattlabs.skipnight.api.player.PlayerAdapter;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.util.Versions;
import net.mattlabs.skipnight.api.world.WorldAdapter;
import net.mattlabs.skipnight.api.config.ConfigurateManager;
import net.mattlabs.skipnight.api.util.MessageTransformations;
import net.mattlabs.skipnight.impl_current.adapters.CurrentCommandAdapter;
import net.mattlabs.skipnight.impl_current.adapters.CurrentMessagesAdapter;
import net.mattlabs.skipnight.impl_current.adapters.CurrentPlayerAdapter;
import net.mattlabs.skipnight.impl_current.adapters.CurrentWorldAdapter;
import net.mattlabs.skipnight.impl_current.events.CurrentEventListener;
import net.mattlabs.skipnight.impl_current.scheduler.CurrentScheduler;
import net.mattlabs.skipnight.plugin.util.PluginMessagesContext;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
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
    private PlayerAdapter playerAdapter;
    private WorldAdapter worldAdapter;
    private MessagesAdapter messagesAdapter;
    private CommandAdapter commandAdapter;

    public static boolean testEnabled = false;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        instance = this;

        // Determine version
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

        // Create Scheduler Impl
        scheduler = new CurrentScheduler(this);

        // Create PlayerAdapter Impl
        playerAdapter = new CurrentPlayerAdapter(this);

        // Create WorldAdapter Impl
        worldAdapter = new CurrentWorldAdapter();

        // Create MessagesAdapter Impl
        messagesAdapter = new CurrentMessagesAdapter(platform);

        // Register vote
        vote = new Vote(messages, hasPlayerActivity(), config, scheduler, playerAdapter, worldAdapter, messagesAdapter);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new CurrentEventListener(vote), this);

        // Register Cloud
        if (!testEnabled) {
            commandAdapter = new CurrentCommandAdapter(this);
            commandAdapter.registerFramework();
            if (config.isSkipNight() || testEnabled)
                commandAdapter.registerSkipNightCommand(vote);
            if (config.isSkipDay() || testEnabled)
                commandAdapter.registerSkipDayCommand(vote);
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

    public PlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    public WorldAdapter getWorldAdapter() {
        return worldAdapter;
    }

    public boolean hasPlayerActivity() {
        return getServer().getPluginManager().getPlugin("PlayerActivity") != null;
    }
}
