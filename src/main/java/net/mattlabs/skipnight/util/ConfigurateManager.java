package net.mattlabs.skipnight.util;

import io.leangen.geantyref.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ConfigurateManager {

    private Map<String, ConfigNode> configMap;
    private final JavaPlugin instance;

    public ConfigurateManager(JavaPlugin instance) {
        configMap = new HashMap<>();
        this.instance = instance;

        // Create Data Directory
        instance.getDataFolder().mkdir();
    }

    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier) {
        add(fileName, typeToken, configSerializable, configSerializableSupplier, configurationOptions -> configurationOptions.shouldCopyDefaults(true));
    }

    public <T> void add(String fileName, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, UnaryOperator<ConfigurationOptions> configurationOptions) {
        File file = new File(instance.getDataFolder(), fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder()
                        .path(file.toPath())
                        .defaultOptions(configurationOptions).build();
        ConfigNode<T> configNode = new ConfigNode<>(file, typeToken, configSerializable, configSerializableSupplier, loader);
        configMap.put(fileName, configNode);
    }

    public <T> void saveDefaults(String fileName) {
        ConfigNode<T> configNode = configMap.get(fileName);
        File file = configNode.getFile();
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();

        if (!file.exists()) {
            instance.getLogger().info("\"" + fileName + "\" file doesn't exist, creating...");
            try {
                loader.save(loader.createNode().set(configNode.getTypeToken(), configNode.getConfigSerializable()));
            }
            catch (IOException e) {
                instance.getLogger().severe("Failed to save \"" + fileName + "\"!");
                instance.getPluginLoader().disablePlugin(instance);
            }
        }
    }

    public <T> void save(String fileName) {
        ConfigNode<T> configNode = configMap.get(fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();

        try {
            loader.save(loader.createNode().set(configNode.getTypeToken(), configNode.getConfigSerializable()));
        }
        catch (IOException e) {
            instance.getLogger().severe("Failed to save \"" + fileName + "\"!");
        }
    }

    public <T> T load(String fileName) {
        ConfigNode<T> configNode = configMap.get(fileName);
        ConfigurationLoader<CommentedConfigurationNode> loader = configNode.getLoader();
        T t = configNode.getConfigSerializable();

        try {
            t = loader.load().get(configNode.getTypeToken(), configNode.getConfigSerializableSupplier());
            configNode.setConfigSerializable(t);
        }
        catch (IOException e) {
            instance.getLogger().severe("Failed to load \"" + fileName + "\" - using a default!");
        }
        return t;
    }

    public <T> void reload() {
        configMap.forEach((name, node) -> {
            load(name);
            save(name);
        });
    }

    public <T> T get(String fileName) {
        ConfigNode<T> configNode = configMap.get(fileName);
        return configNode.getConfigSerializable();
    }

    private static class ConfigNode<T> {

        private final File file;
        private final TypeToken<T> typeToken;
        private T configSerializable;
        private final Supplier<T> configSerializableSupplier;
        private final ConfigurationLoader<CommentedConfigurationNode> loader;

        public ConfigNode(File file, TypeToken<T> typeToken, T configSerializable, Supplier<T> configSerializableSupplier, ConfigurationLoader<CommentedConfigurationNode> loader) {
            this.file = file;
            this.typeToken = typeToken;
            this.configSerializable = configSerializable;
            this.configSerializableSupplier = configSerializableSupplier;
            this.loader = loader;
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
    }
}
