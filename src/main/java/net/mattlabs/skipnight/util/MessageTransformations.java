package net.mattlabs.skipnight.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mattlabs.skipnight.SkipNight;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import javax.annotation.Nullable;

public class MessageTransformations {

    private static final int VERSION_LATEST = 1;
    private static char _formatCode = '&';

    private MessageTransformations() {}

    public static ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("_schema-version")
                .addVersion(VERSION_LATEST, zeroToOne(), initialTransform())
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
                        value.set(MiniMessage.miniMessage().serialize(component));
                    }
                    return null;
                }))
                .build();
    }

    // Convert to nested class structure
    private static ConfigurationTransformation zeroToOne() {
        return ConfigurationTransformation.builder()
                // Create general section
                .addAction(NodePath.path(), (path, value) -> {
                    value.node("general").set(new BlankNode());
                    return null;
                })
                // Rename strings
                .addAction(NodePath.path("vote"), TransformAction.rename("vote-string"))
                .addAction(NodePath.path("day"), TransformAction.rename("day-string"))
                .addAction(NodePath.path("night"), TransformAction.rename("night-string"))
                .addAction(NodePath.path("yes"), TransformAction.rename("yes-string"))
                .addAction(NodePath.path("no"), TransformAction.rename("no-string"))
                // Move fields
                .addAction(NodePath.path("vote-string"), (path, value) -> new Object[]{"general", "vote-string"})
                .addAction(NodePath.path("day-string"), (path, value) -> new Object[]{"general", "day-string"})
                .addAction(NodePath.path("night-string"), (path, value) -> new Object[]{"general", "night-string"})
                .addAction(NodePath.path("yes-string"), (path, value) -> new Object[]{"general", "yes-string"})
                .addAction(NodePath.path("no-string"), (path, value) -> new Object[]{"general", "no-string"})
                .addAction(NodePath.path("no-perm"), (path, value) -> new Object[]{"general", "no-perm"})
                // Create before vote section
                .addAction(NodePath.path(), (path, value) -> {
                    value.node("before-vote").set(new BlankNode());
                    return null;
                })
                // Move fields
                .addAction(NodePath.path("start-vote"), (path, value) -> new Object[]{"before-vote", "start-vote"})
                .addAction(NodePath.path("click-to-start-vote"), (path, value) -> new Object[]{"before-vote", "click-to-start-vote"})
                .addAction(NodePath.path("no-vote-in-prog"), (path, value) -> new Object[]{"before-vote", "no-vote-in-prog"})
                .addAction(NodePath.path("in-bed-no-vote-in-prog"), (path, value) -> new Object[]{"before-vote", "in-bed-no-vote-in-prog"})
                .addAction(NodePath.path("must-sleep"), (path, value) -> new Object[]{"before-vote", "must-sleep"})
                .addAction(NodePath.path("world-is-blacklisted"), (path, value) -> new Object[]{"before-vote", "world-is-blacklisted"})
                .addAction(NodePath.path("world-not-overworld"), (path, value) -> new Object[]{"before-vote", "world-not-overworld"})
                .addAction(NodePath.path("can-only-vote-at-night"), (path, value) -> new Object[]{"before-vote", "can-only-vote-at-night"})
                .addAction(NodePath.path("can-only-vote-at-day"), (path, value) -> new Object[]{"before-vote", "can-only-vote-at-day"})
                .addAction(NodePath.path("no-vote-while-idle"), (path, value) -> new Object[]{"before-vote", "no-vote-while-idle"})
                .addAction(NodePath.path("no-vote-while-away"), (path, value) -> new Object[]{"before-vote", "no-vote-while-away"})
                .addAction(NodePath.path("cooldown"), (path, value) -> new Object[]{"before-vote", "cooldown"})
                // Create during vote section
                .addAction(NodePath.path(), (path, value) -> {
                    value.node("during-vote").set(new BlankNode());
                    return null;
                })
                // Move fields
                .addAction(NodePath.path("player-started-vote"), (path, value) -> new Object[]{"during-vote", "player-started-vote"})
                .addAction(NodePath.path("please-vote"), (path, value) -> new Object[]{"during-vote", "please-vote"})
                .addAction(NodePath.path("click-here-to-vote"), (path, value) -> new Object[]{"during-vote", "click-here-to-vote"})
                .addAction(NodePath.path("you-vote"), (path, value) -> new Object[]{"during-vote", "you-vote"})
                .addAction(NodePath.path("already-voted"), (path, value) -> new Object[]{"during-vote", "already-voted"})
                .addAction(NodePath.path("idle"), (path, value) -> new Object[]{"during-vote", "idle"})
                .addAction(NodePath.path("away"), (path, value) -> new Object[]{"during-vote", "away"})
                .addAction(NodePath.path("back"), (path, value) -> new Object[]{"during-vote", "back"})
                .addAction(NodePath.path("left-world"), (path, value) -> new Object[]{"during-vote", "left-world"})
                .addAction(NodePath.path("in-bed-vote-yes"), (path, value) -> new Object[]{"during-vote", "in-bed-vote-yes"})
                .addAction(NodePath.path("vote-in-prog"), (path, value) -> new Object[]{"during-vote", "vote-in-prog"})
                .addAction(NodePath.path("player-has-not-slept"), (path, value) -> new Object[]{"during-vote", "player-has-not-slept"})
                .addAction(NodePath.path("player-has-voted"), (path, value) -> new Object[]{"during-vote", "player-has-voted"})
                .addAction(NodePath.path("current-vote"), (path, value) -> new Object[]{"during-vote", "current-vote"})
                // Create after vote section
                .addAction(NodePath.path(), (path, value) -> {
                    value.node("after-vote").set(new BlankNode());
                    return null;
                })
                // Move fields
                .addAction(NodePath.path("vote-passed"), (path, value) -> new Object[]{"after-vote", "vote-passed"})
                .addAction(NodePath.path("vote-failed"), (path, value) -> new Object[]{"after-vote", "vote-failed"})
                .addAction(NodePath.path("all-players-have-voted"), (path, value) -> new Object[]{"after-vote", "all-players-have-voted"})
                .addAction(NodePath.path("vote-passed-boss-bar"), (path, value) -> new Object[]{"after-vote", "vote-passed-boss-bar"})
                .addAction(NodePath.path("vote-failed-boss-bar"), (path, value) -> new Object[]{"after-vote", "vote-failed-boss-bar"})
                .addAction(NodePath.path("it-is-already"), (path, value) -> new Object[]{"after-vote", "it-is-already"})
                .build();
    }

    @ConfigSerializable
    private static class BlankNode {}
}
