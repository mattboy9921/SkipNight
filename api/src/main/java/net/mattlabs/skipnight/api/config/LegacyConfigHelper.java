package net.mattlabs.skipnight.api.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LegacyConfigHelper {

    public static void convertConfig(File yamlConfigFile, File dataFolder, Logger logger) {
        // Check if YAML file exists
        if (yamlConfigFile.exists()) {
            logger.info("Old config format found, converting...");

            // Build HOCON loader
            File configFile = new File(dataFolder, "config.conf");
            ConfigurationLoader<CommentedConfigurationNode> configLoader =
                    HoconConfigurationLoader.builder().path(configFile.toPath()).build();

            // Build YAML loader
            YamlConfigurationLoader yamlLoader = YamlConfigurationLoader.builder().path(yamlConfigFile.toPath()).build();

            // Read YAML file
            ConfigurationNode yamlNode;
            try {
                yamlNode = yamlLoader.load();
            }
            catch (IOException e) {
                logger.severe("Unable to read YAML configuration! " + e.getMessage());
                return;
            }

            // Save to HOCON file
            try {
                configLoader.save(yamlNode);
            }
            catch (IOException e) {
                logger.severe("Unable to save HOCON configuration! " + e.getMessage());
                return;
            }

            // Delete YAML file
            logger.info("Successfully converted configuration, deleting old file...");
            yamlConfigFile.delete();
        }
    }
}
