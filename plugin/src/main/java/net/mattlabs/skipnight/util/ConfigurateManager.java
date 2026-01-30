package net.mattlabs.skipnight.util;

import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
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

/* This class uses the raw instance of the parameterized inner class ConfigNode and performs unchecked assignment.
 *  Warnings are suppressed as all instances of unchecked assignment are correct type. */
public class ConfigurateManager {

    @SuppressWarnings("rawtypes")
    private final Map<String, ConfigNode> configMap;
    private final Plugin plugin;

    public ConfigurateManager(Plugin plugin) {
        configMap = new HashMap<>();
        this.plugin = plugin;

        // Create Data Directory
        //noinspection ResultOfMethodCallIgnored
        plugin.getDataFolder().mkdir();
    }

    // Basic add
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions -> configurationOptions.shouldCopyDefaults(true), null);
    }

    // Add with transformations
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, ConfigurationTransformation.Versioned transformation) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions -> configurationOptions.shouldCopyDefaults(true), transformation);
    }

    // Add with configuration options
    @SuppressWarnings("unused")
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, UnaryOperator<ConfigurationOptions> configurationOptions) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions, null);
    }

    // Add with configuration options and transformations
    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, UnaryOperator<ConfigurationOptions> configurationOptions, ConfigurationTransformation.Versioned transformation) {
        File file = new File(plugin.getDataFolder(), fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder()
                        .path(file.toPath())
                        .defaultOptions(configurationOptions).build();
        ConfigNode<T> configNode = new ConfigNode<>(file, typeToken, configSerializable, configSerializableSupplier, loader, transformation);
        configMap.put(fileName, configNode);
    }

    public <T> void saveDefaults(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        File file = configNode.getFile();
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();

        if (!file.exists()) {
            plugin.getLogger().info("\"" + fileName + "\" file doesn't exist, creating...");
            try {
                loader.save(loader.createNode().set(configNode.getTypeToken(), configNode.getConfigSerializable()));
            }
            catch (IOException | StackOverflowError e) {
                plugin.getLogger().severe("Failed to save \"" + fileName + "\"!");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public <T> void save(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();

        try {
            loader.save(loader.createNode().set(configNode.getTypeToken(), configNode.getConfigSerializable()));
        }
        catch (IOException e) {
            plugin.getLogger().severe("Failed to save \"" + fileName + "\"!");
        }
    }

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
                    plugin.getLogger().info("Updated " + fileName + " schema from " + startVersion + " to " + endVersion);
            }
            // Load
            T t = node.get(configNode.getTypeToken(), configNode.getConfigSerializableSupplier());
            configNode.setConfigSerializable(t);
        }
        catch (IOException e) {
            plugin.getLogger().severe("Failed to load \"" + fileName + "\" - using a default!");
        }
    }

    @SuppressWarnings("unused")
    public void reload() {
        configMap.forEach((name, node) -> {
            load(name);
            save(name);
        });
    }

    public <T> T get(String fileName) {
        @SuppressWarnings("unchecked")
        ConfigNode<T> configNode = configMap.get(fileName);
        return configNode.getConfigSerializable();
    }

    private static class ConfigNode<T> {

        private final File file;
        private final TypeToken<T> typeToken;
        private T configSerializable;
        private final Supplier<T> configSerializableSupplier;
        private final ConfigurationLoader<CommentedConfigurationNode> loader;

        private final ConfigurationTransformation.Versioned transformation;

        public ConfigNode(File file, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationTransformation.Versioned transformation) {
            this.file = file;
            this.typeToken = typeToken;
            this.configSerializable = configSerializable;
            this.configSerializableSupplier = configSerializableSupplier;
            this.loader = loader;
            this.transformation = transformation;
        }

        public File getFile() {
            return file;
        }

        public TypeToken<T> getTypeToken() {
            return typeToken;
        }

        public T getConfigSerializable() {
            return configSerializable;
        }

        public Supplier<T> getConfigSerializableSupplier() {
            return configSerializableSupplier;
        }

        public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
            return loader;
        }

        public void setConfigSerializable(T configSerializable) {
            this.configSerializable = configSerializable;
        }

        public ConfigurationTransformation.Versioned getTransformation() {
            return transformation;
        }
    }
}
