package net.mattlabs.skipnight;

import co.aikar.commands.PaperCommandManager;
import com.google.common.reflect.TypeToken;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.commands.SkipDayCommand;
import net.mattlabs.skipnight.commands.SkipNightCommand;
import net.mattlabs.skipnight.util.Versions;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SkipNight extends JavaPlugin {

    public Vote vote;
    private PaperCommandManager manager;
    private Config config;
    private Messages messages;
    private static SkipNight instance;
    private BukkitAudiences platform;
    private String version;

    public void onEnable() {
        instance = this;

        // Determine version
        version = Bukkit.getVersion();
        int start = version.indexOf("MC: ") + 4;
        int end = version.length() - 1;
        version = version.substring(start, end);

        if (Versions.versionCompare("1.9.0", version) >= 0) {
            getLogger().severe("You are running MC " + version + ". This plugin requires MC 1.9.0 or higher, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Configuration Section
        File configFile = new File(this.getDataFolder(), "config.conf");
        File messasgesFile = new File(this.getDataFolder(), "messages.conf");

        ConfigurationLoader<CommentedConfigurationNode> configLoader =
                HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
        ConfigurationLoader<CommentedConfigurationNode> messagesLoader =
                HoconConfigurationLoader.builder().setPath(messasgesFile.toPath()).build();

        // Convert old YAML file if it exists still
        convertConfigFormat(new File(this.getDataFolder(), "config.yml"), configLoader);

        config = null;
        messages = null;

        // Load config from file location, otherwise use values from Config class
        try {
            config = configLoader.load().<Config>getValue(TypeToken.of(Config.class), Config::new);
        }
        catch (ObjectMappingException | IOException e) {
            getLogger().severe("Failed to load the config - Using a default!");
        }

        // Load Messages from file location, otherwise use values from Messages class
        try {
            messages = messagesLoader.load().<Messages>getValue(TypeToken.of(Messages.class), Messages::new);
        }
        catch (ObjectMappingException | IOException e) {
            getLogger().severe("Failed to load the messages config - Using a default!");
        }

        // Save config to file
        try {
            configLoader.save(configLoader.createEmptyNode().setValue(TypeToken.of(Config.class), config));
        }
        catch (ObjectMappingException | IOException e){
            getLogger().severe("Failed to save the config!");
        }

        // Save Messages to file
        try {
            messagesLoader.save(messagesLoader.createEmptyNode().setValue(TypeToken.of(Messages.class), messages));
        }
        catch (ObjectMappingException | IOException e){
            getLogger().severe("Failed to save the messages config!");
        }

        // Register Audience (Messages)
        platform = BukkitAudiences.create(this);

        // Register vote
        vote = new Vote(this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(vote, this);

        // Register ACF
        manager = new PaperCommandManager(this);

        // Register Commands with ACF
        if (config.isSkipNight())
            manager.registerCommand(new SkipNightCommand(this));
        if (config.isSkipDay())
            manager.registerCommand(new SkipDayCommand(this));

        // bStats
        Metrics metrics = new Metrics(this,  	5796);

        // PlayerActivity Integration
        if (!hasPlayerActivity()) getLogger().info("PlayerActivity not found, disabling Idle/Away features");

        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz, Aikar and mbaxter)");
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

    public boolean hasPlayerActivity() {
        return getServer().getPluginManager().getPlugin("PlayerActivity") != null;
    }

    /**
     * Helper method to convert the old YAML configuration file to the new HOCON file
     * @param yamlConfigFile The file representing the location of the YML file
     * @param hoconLoader The HoconConfigurationLoader instance
     */
    private void convertConfigFormat(File yamlConfigFile, ConfigurationLoader hoconLoader) {

        // Check if YAML file exists
        if (yamlConfigFile.exists()) {
            getLogger().info("Old config format found, converting...");

            // Build YAML loader
            YAMLConfigurationLoader yamlLoader = YAMLConfigurationLoader.builder().setPath(yamlConfigFile.toPath()).build();

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
