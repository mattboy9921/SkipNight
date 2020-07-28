package net.mattlabs.skipnight;

import co.aikar.commands.PaperCommandManager;
import com.google.common.reflect.TypeToken;
import net.mattlabs.skipnight.commands.SkipDayCommand;
import net.mattlabs.skipnight.commands.SkipNightCommand;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SkipNight extends JavaPlugin {

    public Vote vote;
    private PaperCommandManager manager;
    private Config config;

    public void onEnable() {
        vote = new Vote(this);

        // Configuration Section
        File configFile = new File(this.getDataFolder(), "config.conf");

        Path potentialFile = configFile.toPath();
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(potentialFile).build();

        convertConfigFormat(new File(this.getDataFolder(), "config.yml"), loader);

        config = null;

        // Load config from file location, otherwise use values from Config class
        try {
            config = loader.load().<Config>getValue(TypeToken.of(Config.class), Config::new);
        }
        catch (ObjectMappingException | IOException e){
            getLogger().severe("Failed to load the config - Using a default!");
        }

        // Save config to file
        try {
            loader.save(loader.createEmptyNode().setValue(TypeToken.of(Config.class), config));
        }
        catch (ObjectMappingException | IOException e){
            getLogger().severe("Failed to save the config!");
        }

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
        Metrics metrics = new Metrics(this);

        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz, Aikar and Foodyling)");
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
