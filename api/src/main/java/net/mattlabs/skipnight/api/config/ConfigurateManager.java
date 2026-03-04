package net.mattlabs.skipnight.api.config;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

/* This class uses the raw instance of the parameterized inner class ConfigNode and performs unchecked assignment.
 *  Warnings are suppressed as all instances of unchecked assignment are correct type. */

/**
 * Manages Configurate-backed configuration files for SkipNight
 *
 * <p>This class acts as a registry of configuration files stored in the plugin's data folder. Each entry is
 * represented by a {@link ConfigNode}, which knows how to serialize and deserialize a specific configuration type
 * using Configurate.</p>
 *
 * <p>The manager supports optional {@link ConfigurationTransformation.Versioned} instances to handle schema
 * migration when loading configuration files.</p>
 */
public class ConfigurateManager {

    @SuppressWarnings("rawtypes")
    private final Map<String, ConfigNode> configMap;
    private final Logger logger;
    private final File dataFolder;

    /**
     * Creates a new {@code ConfigurateManager} for the given data folder and logger.
     *
     * <p>The data folder will be created if it does not already exist.</p>
     *
     * @param dataFolder the plugin's data directory where configuration files are stored
     * @param logger     the logger used to report configuration load/save events and errors
     */
    public ConfigurateManager(File dataFolder, Logger logger) {
        configMap = new HashMap<>();
        this.logger = logger;
        this.dataFolder = dataFolder;

        // Create Data Directory
        //noinspection ResultOfMethodCallIgnored
        dataFolder.mkdir();
    }

    /**
     * Registers a new configuration file with default options and no transformation.
     *
     * <p>This is a convenience overload that enables {@link ConfigurationOptions#shouldCopyDefaults(boolean)}
     * and does not apply any transformations.</p>
     *
     * @param fileName                   the file name of the configuration (relative to the data folder)
     * @param typeToken                  the type token describing the configuration type
     * @param configSerializable         the initial configuration instance to be serialized
     * @param configSerializableSupplier a supplier used to create a default configuration instance when loading
     * @param <T>                        the type of the configuration object
     */
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions -> configurationOptions.shouldCopyDefaults(true), null);
    }

    /**
     * Registers a new configuration file with default options and a versioned transformation.
     *
     * <p>This is a convenience overload that enables {@link ConfigurationOptions#shouldCopyDefaults(boolean)}
     * and applies the given {@link ConfigurationTransformation.Versioned} on load.</p>
     *
     * @param fileName                   the file name of the configuration (relative to the data folder)
     * @param typeToken                  the type token describing the configuration type
     * @param configSerializable         the initial configuration instance to be serialized
     * @param configSerializableSupplier a supplier used to create a default configuration instance when loading
     * @param transformation             the versioned transformation to apply when loading, or {@code null} if none
     * @param <T>                        the type of the configuration object
     */
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, ConfigurationTransformation.Versioned transformation) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions -> configurationOptions.shouldCopyDefaults(true), transformation);
    }

    /**
     * Registers a new configuration file with custom {@link ConfigurationOptions} and no transformation.
     *
     * @param fileName                   the file name of the configuration (relative to the data folder)
     * @param typeToken                  the type token describing the configuration type
     * @param configSerializable         the initial configuration instance to be serialized
     * @param configSerializableSupplier a supplier used to create a default configuration instance when loading
     * @param configurationOptions       a function used to customize the default {@link ConfigurationOptions}
     * @param <T>                        the type of the configuration object
     */
    @SuppressWarnings("unused")
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, UnaryOperator<ConfigurationOptions> configurationOptions) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions, null);
    }

    /**
     * Registers a new configuration file with custom {@link ConfigurationOptions} and an optional transformation.
     *
     * <p>This is the most general {@code add} method. All other overloads delegate here.</p>
     *
     * @param fileName                   the file name of the configuration (relative to the data folder)
     * @param typeToken                  the type token describing the configuration type
     * @param configSerializable         the initial configuration instance to be serialized
     * @param configSerializableSupplier a supplier used to create a default configuration instance when loading
     * @param configurationOptions       a function used to customize the default {@link ConfigurationOptions}
     * @param transformation             the versioned transformation to apply when loading, or {@code null} if none
     * @param <T>                        the type of the configuration object
     */
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, UnaryOperator<ConfigurationOptions> configurationOptions, ConfigurationTransformation.Versioned transformation) {
        File file = new File(dataFolder, fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder()
                        .path(file.toPath())
                        .defaultOptions(configurationOptions).build();
        ConfigNode<T> configNode = new ConfigNode<>(file, typeToken, configSerializable, configSerializableSupplier, loader, transformation);
        configMap.put(fileName, configNode);
    }

    /**
     * Saves the default configuration to disk if the configuration file does not already exist.
     *
     * <p>If the file already exists, no action is taken and this method returns {@code true}.</p>
     *
     * @param fileName the name of the configuration file to save
     * @param <T>      the type of the configuration object
     * @return {@code true} if the defaults are saved successfully or the file already exists,
     *         {@code false} if saving fails
     */
    public <T> boolean saveDefaults(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        File file = configNode.getFile();
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();

        if (!file.exists()) {
            logger.info("\"" + fileName + "\" file doesn't exist, creating...");
            try {
                loader.save(loader.createNode().set(configNode.getTypeToken(), configNode.getConfigSerializable()));
            }
            catch (IOException | StackOverflowError e) {
                logger.severe("Failed to save \"" + fileName + "\"!");
                return false;
            }
        }
        return true;
    }

    /**
     * Saves the current configuration instance to disk, overwriting any existing file.
     *
     * @param fileName the name of the configuration file to save
     * @param <T>      the type of the configuration object
     */
    public <T> void save(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();

        try {
            loader.save(loader.createNode().set(configNode.getTypeToken(), configNode.getConfigSerializable()));
        }
        catch (IOException e) {
            logger.severe("Failed to save \"" + fileName + "\"!");
        }
    }

    /**
     * Loads the configuration from disk and updates the registered configuration instance.
     *
     * <p>If a {@link ConfigurationTransformation.Versioned} was provided for this configuration,
     * it will be applied before deserialization. If loading fails, the existing configuration
     * instance in memory is left unchanged and a default will be used instead.</p>
     *
     * @param fileName the name of the configuration file to load
     * @param <T>      the type of the configuration object
     */
    public <T> void load(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();
        CommentedConfigurationNode node;
        ConfigurationTransformation.Versioned transformation = configNode.getTransformation();

        try {
            node = loader.load();
            // Transformations
            if (transformation != null) {
                int startVersion = transformation.version(node);
                transformation.apply(node);
                int endVersion = transformation.version(node);
                if (startVersion != endVersion)
                    logger.info("Updated " + fileName + " schema from " + startVersion + " to " + endVersion);
            }
            // Load
            T t = node.get(configNode.getTypeToken(), configNode.getConfigSerializableSupplier());
            configNode.setConfigSerializable(t);
        }
        catch (IOException e) {
            logger.severe("Failed to load \"" + fileName + "\" - using a default!");
        }
    }

    /**
     * Reloads all registered configuration files from disk and immediately saves them back.
     *
     * <p>This will apply any schema transformations and re-serialize the updated configuration
     * state for every registered file.</p>
     */
    @SuppressWarnings("unused")
    public void reload() {
        configMap.forEach((name, node) -> {
            load(name);
            save(name);
        });
    }

    /**
     * Retrieves the current configuration instance associated with a given file name.
     *
     * <p>The configuration must have been registered via one of the {@code add} methods prior
     * to calling this method.</p>
     *
     * @param fileName the name of the configuration file whose instance should be returned
     * @param <T>      the type of the configuration object
     * @return the configuration instance associated with the given file name
     */
    public <T> T get(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        return configNode.getConfigSerializable();
    }

    /**
     * Internal holder for a single configuration file and its associated metadata.
     *
     * @param <T> the type of the configuration object
     */
    private static class ConfigNode<T> {

        private final File file;
        private final TypeToken<T> typeToken;
        private T configSerializable;
        private final Supplier<T> configSerializableSupplier;
        private final ConfigurationLoader<CommentedConfigurationNode> loader;

        private final ConfigurationTransformation.Versioned transformation;

        /**
         * Creates a new {@code ConfigNode} instance.
         *
         * @param file                        the backing file of the configuration
         * @param typeToken                   the type token describing the configuration type
         * @param configSerializable          the initial configuration instance
         * @param configSerializableSupplier  a supplier used to create default configuration instances
         * @param loader                      the Configurate loader used to read/write the configuration
         * @param transformation              the versioned transformation to apply on load, or {@code null} if none
         */
        public ConfigNode(File file, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationTransformation.Versioned transformation) {
            this.file = file;
            this.typeToken = typeToken;
            this.configSerializable = configSerializable;
            this.configSerializableSupplier = configSerializableSupplier;
            this.loader = loader;
            this.transformation = transformation;
        }

        /**
         * @return the backing file of this configuration
         */
        public File getFile() {
            return file;
        }

        /**
         * @return the type token describing the configuration type
         */
        public TypeToken<T> getTypeToken() {
            return typeToken;
        }

        /**
         * @return the current configuration instance
         */
        public T getConfigSerializable() {
            return configSerializable;
        }

        /**
         * @return the supplier used to create default configuration instances
         */
        public Supplier<T> getConfigSerializableSupplier() {
            return configSerializableSupplier;
        }

        /**
         * @return the loader used to read and write this configuration
         */
        public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
            return loader;
        }

        /**
         * Updates the in-memory configuration instance.
         *
         * @param configSerializable the new configuration instance
         */
        public void setConfigSerializable(T configSerializable) {
            this.configSerializable = configSerializable;
        }

        /**
         * @return the versioned transformation applied when loading, or {@code null} if none
         */
        public ConfigurationTransformation.Versioned getTransformation() {
            return transformation;
        }
    }
}
