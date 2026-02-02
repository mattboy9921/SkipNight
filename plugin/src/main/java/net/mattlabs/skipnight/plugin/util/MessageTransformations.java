package net.mattlabs.skipnight.plugin.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import javax.annotation.Nullable;

import static org.spongepowered.configurate.NodePath.path;

public class MessageTransformations {

    private static final int VERSION_LATEST = 2;
    private static char _formatCode = '&';

    private MessageTransformations() {}

    public static ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("_schema-version")
                .addVersion(VERSION_LATEST, oneToTwo())
                .addVersion(1, zeroToOne())
                .addVersion(0, initialTransform())
                .build();
    }

    // Transform ampersand color codes to MiniMessage format
    private static ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder()
                .addAction(path("_formatCode"), ((path, value) -> {
                    final @Nullable String formatCode = value.getString();
                    if (formatCode != null)
                        _formatCode = value.getString().charAt(0);
                    return null;
                }))
                .addAction(path(ConfigurationTransformation.WILDCARD_OBJECT), ((path, value) -> {
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
                .addAction(path(), (path, value) -> {
                    value.node("general").set(new BlankNode());
                    return null;
                })
                // Rename strings
                .addAction(path("vote"), TransformAction.rename("vote-string"))
                .addAction(path("day"), TransformAction.rename("day-string"))
                .addAction(path("night"), TransformAction.rename("night-string"))
                .addAction(path("yes"), TransformAction.rename("yes-string"))
                .addAction(path("no"), TransformAction.rename("no-string"))
                // Move fields
                .addAction(path("vote-string"), (path, value) -> new Object[]{"general", "vote-string"})
                .addAction(path("day-string"), (path, value) -> new Object[]{"general", "day-string"})
                .addAction(path("night-string"), (path, value) -> new Object[]{"general", "night-string"})
                .addAction(path("yes-string"), (path, value) -> new Object[]{"general", "yes-string"})
                .addAction(path("no-string"), (path, value) -> new Object[]{"general", "no-string"})
                .addAction(path("no-perm"), (path, value) -> new Object[]{"general", "no-perm"})
                // Create before vote section
                .addAction(path(), (path, value) -> {
                    value.node("before-vote").set(new BlankNode());
                    return null;
                })
                // Move fields
                .addAction(path("start-vote"), (path, value) -> new Object[]{"before-vote", "start-vote"})
                .addAction(path("click-to-start-vote"), (path, value) -> new Object[]{"before-vote", "click-to-start-vote"})
                .addAction(path("no-vote-in-prog"), (path, value) -> new Object[]{"before-vote", "no-vote-in-prog"})
                .addAction(path("in-bed-no-vote-in-prog"), (path, value) -> new Object[]{"before-vote", "in-bed-no-vote-in-prog"})
                .addAction(path("must-sleep"), (path, value) -> new Object[]{"before-vote", "must-sleep"})
                .addAction(path("world-is-blacklisted"), (path, value) -> new Object[]{"before-vote", "world-is-blacklisted"})
                .addAction(path("world-not-overworld"), (path, value) -> new Object[]{"before-vote", "world-not-overworld"})
                .addAction(path("can-only-vote-at-night"), (path, value) -> new Object[]{"before-vote", "can-only-vote-at-night"})
                .addAction(path("can-only-vote-at-day"), (path, value) -> new Object[]{"before-vote", "can-only-vote-at-day"})
                .addAction(path("no-vote-while-idle"), (path, value) -> new Object[]{"before-vote", "no-vote-while-idle"})
                .addAction(path("no-vote-while-away"), (path, value) -> new Object[]{"before-vote", "no-vote-while-away"})
                .addAction(path("cooldown"), (path, value) -> new Object[]{"before-vote", "cooldown"})
                // Create during vote section
                .addAction(path(), (path, value) -> {
                    value.node("during-vote").set(new BlankNode());
                    return null;
                })
                // Move fields
                .addAction(path("player-started-vote"), (path, value) -> new Object[]{"during-vote", "player-started-vote"})
                .addAction(path("please-vote"), (path, value) -> new Object[]{"during-vote", "please-vote"})
                .addAction(path("click-here-to-vote"), (path, value) -> new Object[]{"during-vote", "click-here-to-vote"})
                .addAction(path("you-vote"), (path, value) -> new Object[]{"during-vote", "you-vote"})
                .addAction(path("already-voted"), (path, value) -> new Object[]{"during-vote", "already-voted"})
                .addAction(path("idle"), (path, value) -> new Object[]{"during-vote", "idle"})
                .addAction(path("away"), (path, value) -> new Object[]{"during-vote", "away"})
                .addAction(path("back"), (path, value) -> new Object[]{"during-vote", "back"})
                .addAction(path("left-world"), (path, value) -> new Object[]{"during-vote", "left-world"})
                .addAction(path("in-bed-vote-yes"), (path, value) -> new Object[]{"during-vote", "in-bed-vote-yes"})
                .addAction(path("vote-in-prog"), (path, value) -> new Object[]{"during-vote", "vote-in-prog"})
                .addAction(path("player-has-not-slept"), (path, value) -> new Object[]{"during-vote", "player-has-not-slept"})
                .addAction(path("player-has-voted"), (path, value) -> new Object[]{"during-vote", "player-has-voted"})
                .addAction(path("current-vote"), (path, value) -> new Object[]{"during-vote", "current-vote"})
                // Create after vote section
                .addAction(path(), (path, value) -> {
                    value.node("after-vote").set(new BlankNode());
                    return null;
                })
                // Move fields
                .addAction(path("vote-passed"), (path, value) -> new Object[]{"after-vote", "vote-passed"})
                .addAction(path("vote-failed"), (path, value) -> new Object[]{"after-vote", "vote-failed"})
                .addAction(path("all-players-have-voted"), (path, value) -> new Object[]{"after-vote", "all-players-have-voted"})
                .addAction(path("vote-passed-boss-bar"), (path, value) -> new Object[]{"after-vote", "vote-passed-boss-bar"})
                .addAction(path("vote-failed-boss-bar"), (path, value) -> new Object[]{"after-vote", "vote-failed-boss-bar"})
                .addAction(path("it-is-already"), (path, value) -> new Object[]{"after-vote", "it-is-already"})
                .build();
    }

    // Convert to placeholders
    private static ConfigurationTransformation oneToTwo() {
        return ConfigurationTransformation.builder()
                .addAction(path("before-vote", "no-vote-in-prog"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<start_vote>"));
                    return null;
                })
                .addAction(path("before-vote", "in-bed-no-vote-in-prog"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<start_vote>"));
                    return null;
                })
                .addAction(path("during-vote", "player-started-vote"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null)
                        value.set(val.replace("{0}", "<player_name>")
                                .replace("{1}", "<vote_type>"));
                    return null;
                })
                .addAction(path("during-vote", "click-here-to-vote"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_value>"));
                    return null;
                })
                .addAction(path("during-vote", "you-vote"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_value>"));
                    return null;
                })
                .addAction(path("during-vote", "player-has-not-slept"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<player_name>"));
                    return null;
                })
                .addAction(path("during-vote", "player-has-voted"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null)
                        value.set(val.replace("{0}", "<player_name>")
                                .replace("{1}", "<vote_value>"));
                    return null;
                })
                .addAction(path("after-vote", "vote-passed"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_type>"));
                    return null;
                })
                .addAction(path("after-vote", "vote-failed"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_type>"));
                    return null;
                })
                .addAction(path("after-vote", "it-is-already"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_type>"));
                    return null;
                })
                .build();
    }

    @ConfigSerializable
    private static class BlankNode {}
}
