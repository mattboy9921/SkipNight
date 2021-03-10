package net.mattlabs.skipnight.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mattlabs.skipnight.SkipNight;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import javax.annotation.Nullable;

public class Transformations {

    private static final int VERSION_LATEST = 0;
    private static char _formatCode = '&';

    private Transformations() {}

    public static ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("_schema-version")
                .addVersion(VERSION_LATEST, initialTransform())
                .build();
    }

    // Transform ampersand color codes to MiniMessage format
    private static ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder()
                .addAction(NodePath.path("_formatCode"), ((path, value) -> {
                    final @Nullable String formatCode = value.getString();
                    if (formatCode != null)
                        _formatCode = value.getString().charAt(0);
                    return null;
                }))
                .addAction(NodePath.path(ConfigurationTransformation.WILDCARD_OBJECT), ((path, value) -> {
                    final @Nullable String string = value.getString();
                    if (string != null) {
                        TextComponent component = LegacyComponentSerializer.legacy(_formatCode).deserialize(string);
                        value.set(MiniMessage.get().serialize(component));
                    }
                    return null;
                }))
                .build();
    }

    public static <N extends ConfigurationNode> N updateNode(final N node) throws ConfigurateException {
        if (!node.virtual()) {
            final ConfigurationTransformation.Versioned transformation = create();
            final int startVersion = transformation.version(node);
            transformation.apply(node);
            final int endVersion = transformation.version(node);
            if (startVersion != endVersion) {
                SkipNight.getInstance().getLogger().info("Updated messages configuration schema from " + startVersion + " to " + endVersion);
            }
        }
        return node;
    }
}
