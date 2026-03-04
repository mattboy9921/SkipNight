package net.mattlabs.skipnight.api.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import javax.annotation.Nullable;

/**
 * Provides Configurate schema migrations for SkipNight's messages configuration.
 *
 * <p>This class defines a {@link ConfigurationTransformation.Versioned} chain which can be applied
 * when loading the messages config in order to transparently upgrade older config formats to the
 * latest supported schema.</p>
 *
 * <p>The current schema version is {@value #VERSION_LATEST}.</p>
 *
 * <h2>Migration overview</h2>
 * <ul>
 *     <li><b>Initial transform (pre-versioned)</b>: Converts legacy {@code &}-style (or custom) color codes
 *     into MiniMessage format, serializing the result back into config strings.</li>
 *     <li><b>0 → 1</b>: Restructures the flat config into nested sections (general/before-vote/during-vote/after-vote)
 *     and renames/moves keys to match the new layout.</li>
 *     <li><b>1 → 2</b>: Converts numbered placeholders like {@code {0}} to MiniMessage-style placeholders
 *     such as {@code <player_name>}.</li>
 * </ul>
 *
 * <p>This class is intentionally static/utility-only.</p>
 */
public class MessageTransformations {

    /** Latest supported configuration schema version for the messages config. */
    private static final int VERSION_LATEST = 2;

    /**
     * The legacy formatting code used when converting old-style color codes.
     *
     * <p>Defaults to {@code '&'}, but may be overridden by the {@code _formatCode} node
     * during {@link #initialTransform()}.</p>
     */
    private static char _formatCode = '&';

    private MessageTransformations() {}

    /**
     * Creates the versioned transformation chain used to migrate the messages configuration.
     *
     * <p>The returned transformation uses {@code _schema-version} as its version key and applies
     * upgrades step-by-step until {@value #VERSION_LATEST} is reached.</p>
     *
     * @return a versioned configuration transformation for the messages config
     */
    public static ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("_schema-version")
                .addVersion(VERSION_LATEST, oneToTwo())
                .addVersion(1, zeroToOne())
                .addVersion(0, initialTransform())
                .build();
    }

    /**
     * Performs the initial migration for legacy message configs that used legacy formatting codes.
     *
     * <p>This transformation:</p>
     * <ol>
     *     <li>Reads {@code _formatCode} (if present) to determine which legacy code character is used
     *     (for example {@code &} or {@code §}).</li>
     *     <li>Walks all immediate string nodes and converts legacy formatting into a MiniMessage string
     *     by round-tripping through Adventure components.</li>
     * </ol>
     *
     * <p>This step is applied as part of the versioned chain for schema version {@code 0}.</p>
     *
     * @return the transformation that converts legacy formatting codes to MiniMessage
     */
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

    /**
     * Migrates schema version {@code 0} to {@code 1} by converting the configuration into a nested section layout.
     *
     * <p>This transformation creates top-level sections and then renames/moves keys into their new locations:</p>
     * <ul>
     *     <li>{@code general}</li>
     *     <li>{@code before-vote}</li>
     *     <li>{@code during-vote}</li>
     *     <li>{@code after-vote}</li>
     * </ul>
     *
     * @return the transformation that restructures the config into nested sections
     */
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

    /**
     * Migrates schema version {@code 1} to {@code 2} by converting numbered placeholders
     * (for example {@code {0}} and {@code {1}}) into MiniMessage-style placeholders
     * (for example {@code <player_name>}).
     *
     * <p>This transformation only updates specific known message keys that previously
     * used numbered placeholders.</p>
     *
     * @return the transformation that converts placeholder formats
     */
    private static ConfigurationTransformation oneToTwo() {
        return ConfigurationTransformation.builder()
                .addAction(NodePath.path("before-vote", "no-vote-in-prog"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<start_vote>"));
                    return null;
                })
                .addAction(NodePath.path("before-vote", "in-bed-no-vote-in-prog"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<start_vote>"));
                    return null;
                })
                .addAction(NodePath.path("during-vote", "player-started-vote"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null)
                        value.set(val.replace("{0}", "<player_name>")
                                .replace("{1}", "<vote_type>"));
                    return null;
                })
                .addAction(NodePath.path("during-vote", "click-here-to-vote"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_value>"));
                    return null;
                })
                .addAction(NodePath.path("during-vote", "you-vote"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_value>"));
                    return null;
                })
                .addAction(NodePath.path("during-vote", "player-has-not-slept"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<player_name>"));
                    return null;
                })
                .addAction(NodePath.path("during-vote", "player-has-voted"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null)
                        value.set(val.replace("{0}", "<player_name>")
                                .replace("{1}", "<vote_value>"));
                    return null;
                })
                .addAction(NodePath.path("after-vote", "vote-passed"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_type>"));
                    return null;
                })
                .addAction(NodePath.path("after-vote", "vote-failed"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_type>"));
                    return null;
                })
                .addAction(NodePath.path("after-vote", "it-is-already"), (path, value) -> {
                    final @Nullable String val = value.getString();
                    if (val != null) value.set(val.replace("{0}", "<vote_type>"));
                    return null;
                })
                .build();
    }

    /**
     * Placeholder node used when creating new section objects during transformations.
     *
     * <p>Configurate requires object nodes to be backed by a serializable type
     * when {@code set(Object)} is used for object mapping.</p>
     */
    @ConfigSerializable
    private static class BlankNode {}
}
