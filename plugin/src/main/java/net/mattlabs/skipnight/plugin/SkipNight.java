package net.mattlabs.skipnight.plugin;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.Scheduler;
import net.mattlabs.skipnight.impl_current.CurrentScheduler;
import net.mattlabs.skipnight.plugin.commands.SkipDayCommand;
import net.mattlabs.skipnight.plugin.commands.SkipNightCommand;
import net.mattlabs.skipnight.plugin.util.ConfigurateManager;
import net.mattlabs.skipnight.plugin.util.MessageTransformations;
import net.mattlabs.skipnight.plugin.util.Versions;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class SkipNight extends JavaPlugin {

    public Vote vote;
    private LegacyPaperCommandManager<CommandSender> commandManager;
    private Config config;
    private Messages messages;
    private static SkipNight instance;
    private BukkitAudiences platform;
    private String version;
    private Scheduler scheduler;

    public static boolean testEnabled = false;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        instance = this;

        // Determine version
        version = Bukkit.getVersion();
        int start = version.indexOf("MC: ") + 4;
        int end = version.length() - 1;
        version = version.substring(start, end);

        if (Versions.versionCompare("1.8.0", version) >= 0) {
            getLogger().severe("You are running MC " + version + ". This plugin requires MC 1.8.0 or higher, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Configuration Section

        // Convert old YAML file if it exists still
        this.getDataFolder().mkdir();
        File configFile = new File(this.getDataFolder(), "config.conf");
        ConfigurationLoader<CommentedConfigurationNode> configLoader =
                HoconConfigurationLoader.builder().path(configFile.toPath()).build();
        convertConfigFormat(new File(this.getDataFolder(), "config.yml"), configLoader);

        config = null;
        messages = null;

        // Configurate
        ConfigurateManager configurateManager = new ConfigurateManager(this);

        configurateManager.add("config.conf", TypeToken.get(Config.class), new Config(), Config::new);
        configurateManager.add("messages.conf", TypeToken.get(Messages.class), new Messages(), Messages::new, MessageTransformations.create());

        configurateManager.saveDefaults("config.conf");
        configurateManager.saveDefaults("messages.conf");

        configurateManager.load("config.conf");
        configurateManager.load("messages.conf");

        configurateManager.save("config.conf");
        configurateManager.save("messages.conf");

        config = configurateManager.get("config.conf");
        messages = configurateManager.get("messages.conf");

        // Register Audience (Messages)
        platform = BukkitAudiences.create(this);

        // Create Scheduler Impl
        scheduler = new CurrentScheduler(this);

        // Register vote
        vote = new Vote(this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(vote, this);

        // Register Cloud
        if (!testEnabled) {
            commandManager = LegacyPaperCommandManager.createNative(
                    this,
                    ExecutionCoordinator.coordinatorFor(ExecutionCoordinator.nonSchedulingExecutor())
            );
            // Register Brigadier, fallback to asynchronous completions
            if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER) && !testEnabled) commandManager.registerBrigadier();
            else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) commandManager.registerAsynchronousCompletions();
            // Create Commands
            if (config.isSkipNight() || testEnabled)
                new SkipNightCommand(commandManager, this);
            if (config.isSkipDay() || testEnabled)
                new SkipDayCommand(commandManager, this);
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

    public LegacyPaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public boolean hasPlayerActivity() {
        return getServer().getPluginManager().getPlugin("PlayerActivity") != null;
    }

    /**
     * Helper method to convert the old YAML configuration file to the new HOCON file
     * @param yamlConfigFile The file representing the location of the YML file
     * @param hoconLoader The HoconConfigurationLoader instance
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void convertConfigFormat(File yamlConfigFile, ConfigurationLoader<CommentedConfigurationNode> hoconLoader) {

        // Check if YAML file exists
        if (yamlConfigFile.exists()) {
            getLogger().info("Old config format found, converting...");

            // Build YAML loader
            YamlConfigurationLoader yamlLoader = YamlConfigurationLoader.builder().path(yamlConfigFile.toPath()).build();

            // Read YAML file
            ConfigurationNode yamlNode;
            try {
                yamlNode = yamlLoader.load();
            }
            catch (IOException e) {
                getLogger().severe("Unable to read YAML configuration! " + e.getMessage());
                return;
            }

            // Save to HOCON file
            try {
                hoconLoader.save(yamlNode);
            }
            catch (IOException e) {
                getLogger().severe("Unable to save HOCON configuration! " + e.getMessage());
                return;
            }

            // Delete YAML file
            getLogger().info("Successfully converted configuration, deleting old file...");
            yamlConfigFile.delete();
        }
    }
}
