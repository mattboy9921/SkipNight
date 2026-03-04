package net.mattlabs.skipnight.api.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Utility class for migrating legacy SkipNight configurations from YAML format
 * to the newer HOCON-based {@code config.conf} format.
 *
 * <p>This helper exists to support older installations of SkipNight that
 * previously used a {@code config.yml}. When called, it checks for the
 * presence of the YAML file, loads and converts its contents into a HOCON
 * configuration node, saves that node as {@code config.conf}, and deletes
 * the legacy file.</p>
 *
 * <p>If the YAML file does not exist, this method does nothing.</p>
 */
public class LegacyConfigHelper {

    /**
     * Converts a legacy YAML configuration file into the modern HOCON format.
     *
     * <p>If the specified YAML file exists, it will be read using a
     * {@link YamlConfigurationLoader}, then written to
     * {@code config.conf} using a {@link HoconConfigurationLoader}.
     * The original YAML file will be deleted after a successful conversion.</p>
     *
     * <p>Conversion steps:</p>
     * <ol>
     *     <li>Check if the YAML file exists; return if it does not.</li>
     *     <li>Load the YAML contents into a {@link ConfigurationNode}.</li>
     *     <li>Write that node to {@code config.conf}.</li>
     *     <li>Delete the old YAML file.</li>
     * </ol>
     *
     * @param yamlConfigFile the legacy YAML configuration file (e.g. {@code config.yml})
     * @param dataFolder     the plugin's data folder where {@code config.conf} should be stored
     * @param logger         the logger used to output conversion status messages
     */
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
