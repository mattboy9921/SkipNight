package net.mattlabs.skipnight.api.messaging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * Configuration-backed message definitions for SkipNight.
 *
 * <p>This class represents the full set of user-facing messages used by SkipNight,
 * organized into logical sections:
 * {@link Messages.General}, {@link Messages.BeforeVote},
 * {@link Messages.DuringVote}, and {@link Messages.AfterVote}.</p>
 *
 * <p>Each section contains both:</p>
 * <ul>
 *     <li>String fields that are serialized/deserialized via Configurate, and</li>
 *     <li>Convenience methods that convert those strings into Adventure
 *         {@link Component} instances using MiniMessage placeholders and styling.</li>
 * </ul>
 *
 * <p>The {@code Messages} instance itself is serialized as part of the plugin's
 * messages configuration, and a {@link MessagesContext} must be provided via
 * {@link #Initialize(MessagesContext)} before any static helper methods (such as
 * {@link #voteHeader()} or {@link #hyphenHeader()}) are used.</p>
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Messages {

    private static MessagesContext messagesContext;

    /**
     * Initializes the static {@link MessagesContext} used by this class.
     *
     * <p>This context provides access to configuration values (such as whether
     * the header is disabled) and to the root {@link Messages} instance for helper
     * methods like {@link #voteHeader()} and {@link #hyphenHeader()}.</p>
     *
     * <p>This method should be called once during plugin startup, after the
     * messages configuration has been loaded.</p>
     *
     * @param messagesContext the context containing the loaded {@link Messages}
     *                        and additional messaging configuration
     */
    public static void Initialize(MessagesContext messagesContext) {
        Messages.messagesContext = messagesContext;
    }

    // Header fields
    @SuppressWarnings("unused")
    @Setting(value = "_schema-version")
    @Comment("""
            #######################################################################################################
            Skipnight Messages Configuration
            By mattboy9921
            https://github.com/mattboy9921/SkipNight

            This configuration contains every string of text found in this plugin.

            For values that contain variables, they are shown as "<some_value>"
            and the possible tags are shown in the comment above the line.
            It is not necessary to include every variable, but certain strings won't make sense otherwise.

            Colors and text style can be specified using XML-like tags, for example: "<white>".
            Standard Minecraft colors/styles are available. Hex colors can be specified with "<color:#XXXXXX>".
            Please note, some values cannot use color codes ("<white>") as denoted in the comment above the value.
            #######################################################################################################

            Config version. Do not change this!""")
    private int schemaVersion = 2;

    /*================================================================================

                                         General

    ================================================================================*/

    /**
     * Message values that are used throughout the plugin, such as the words for
     * "day", "night", "yes", "no", and a generic "Vote" label.
     *
     * <p>Most of these are returned as raw {@link String} values (often without
     * color codes) and are used by other sections when constructing messages.</p>
     */
    @ConfigSerializable
    public static class General {
        @Comment("""

                Vote text that appears before certain messages.
                (Does not accept color codes)""")
        private String voteString = "Vote";

        /**
         * @return the configured "Vote" label (no color codes)
         */
        public String voteString() {
            return voteString;
        }

        @Comment("""

                Value for the word "day".
                (Does not accept color codes)""")
        private String dayString = "day";

        /**
         * @return the configured word for "day" (no color codes)
         */
        public String dayString() {
            return dayString;
        }

        @Comment("""

                Value for the word "night".
                (Does not accept color codes)""")
        private String nightString = "night";

        /**
         * @return the configured word for "night" (no color codes)
         */
        public String nightString() {
            return nightString;
        }

        @Comment("""

                Value for the word "yes".
                (Does not accept color codes)""")
        String yesString = "yes";

        /**
         * @return the configured word for "yes" (no color codes)
         */
        public String yesString() {
            return yesString;
        }

        @Comment("""

                Value for the word "no".
                (Does not accept color codes)""")
        private String noString = "no";

        /**
         * @return the configured word for "no" (no color codes)
         */
        public String noString() {
            return noString;
        }

        // No Permission
        @Comment("\nAppears if player doesn't have permission to vote.")
        private String noPerm = "<red>You don't have permission to run this!";

        /**
         * Builds the "no permission" message as a {@link Component}.
         *
         * @return a component for the no-permission message
         */
        public Component noPerm() {
            // <red>You don't have permission to run this!
            return MiniMessage.miniMessage().deserialize(noPerm);
        }
    }

    @Comment("""
            ================================================================================

                                               General

            ================================================================================

            General strings that may be used throughout the plugin.""")
    private General general = new General();

    /**
     * Returns the {@link General} section of the messages configuration.
     *
     * @return the general message configuration section
     */
    public General general() {
        return general;
    }

    /*================================================================================

                                        Before Vote

    ================================================================================*/

    @ConfigSerializable
    public static class BeforeVote {
        // No vote in progress
        @Comment("""

                Button text for "Start Vote" button.
                (Does not accept color codes)""")
        private String startVote = "Start Vote";

        @Comment("\nHover text for \"Start Vote\" button.")
        private String clickToStartVote = "<gold><bold>Click here to start a vote";

        @Comment("""

                Appears if a vote isn't in progress.
                Possible tags: <start_vote> = "Start Vote" button""")
        private String noVoteInProg = "<red>No vote in progress! <blue><start_vote>";

        @Comment("""

                Appears if player sleeps with no vote in progress.
                Possible tags: <start_vote> = "Start Vote" button""")
        private String inBedNoVoteInProg = "Start a vote to skip the night? <blue><start_vote>";

        /**
         * Builds the "no vote in progress" message, including a clickable
         * "Start Vote" button for the given vote type.
         *
         * @param voteType the vote type keyword
         * @return a {@link Component} with the start-vote button
         */
        public Component noVoteInProg(String voteType) {
            return MiniMessage.miniMessage().deserialize(noVoteInProg,
                    Placeholder.component("start_vote",
                            Component.text("[" + startVote + "]", Style.style(TextDecoration.BOLD))
                                    .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(clickToStartVote)))
                                    .clickEvent(ClickEvent.runCommand("/skip" + voteType))));
        }

        /**
         * Builds the message shown when a player is in bed but no vote is in progress.
         *
         * @return a {@link Component} including the vote header and start-vote button
         */
        public Component inBedNoVoteInProg() {
            return Messages.voteHeader().append(MiniMessage.miniMessage().deserialize(inBedNoVoteInProg,
                    Placeholder.component("start_vote",
                            Component.text("[" + startVote + "]", Style.style(TextDecoration.BOLD))
                                    .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(clickToStartVote)))
                                    .clickEvent(ClickEvent.runCommand("/skipnight")))).colorIfAbsent(NamedTextColor.WHITE));
        }

        // Must sleep before voting
        @Comment("\nAppears if player attempts to vote after 3 days without sleep.")
        private String mustSleep = "<red>You must sleep in a bed first!";

        /**
         * Builds the "must sleep" message, prefixed with the hyphen header.
         *
         * @return the {@link Component} message
         */
        public Component mustSleep() {
            return Messages.hyphenHeader().append(MiniMessage.miniMessage().deserialize(mustSleep));
        }

        /**
         * Builds the plain "must sleep" message with no header.
         *
         * @return the {@link Component} message
         */
        public Component mustSleepNewVote() {
            return MiniMessage.miniMessage().deserialize(mustSleep);
        }

        // World Is Blacklisted
        @Comment("\nAppears if player tries to vote in a blacklisted world.")
        private String worldIsBlacklisted = "<red>You cannot start a vote in this world!";

        public Component worldIsBlacklisted() {
            return MiniMessage.miniMessage().deserialize(worldIsBlacklisted);
        }

        // Must Be In Overworld
        @Comment("\nAppears if player tries to vote outside of the overworld.")
        private String worldNotOverworld = "<red>You must be in the overworld to start a vote!";

        public Component worldNotOverworld() {
            return MiniMessage.miniMessage().deserialize(worldNotOverworld);
        }

        // Can Only Vote At Night
        @Comment("\nAppears if player tries to start a vote to skip the night during the day.")
        private String canOnlyVoteAtNight = "<red>You can only start a vote at night!";

        public Component canOnlyVoteAtNight() {
            return MiniMessage.miniMessage().deserialize(canOnlyVoteAtNight);
        }

        // Can Only Vote At Day
        @Comment("\nAppears if player tries to start a vote to skip the day during the night.")
        private String canOnlyVoteAtDay = "<red>You can only start a vote during the day!";

        public Component canOnlyVoteAtDay() {
            return MiniMessage.miniMessage().deserialize(canOnlyVoteAtDay);
        }

        // No Vote While Idle
        @Comment("\nAppears if player tries to start a vote while idle.")
        private String noVoteWhileIdle = "<red>You cannot start a vote while idle!";

        public Component noVoteWhileIdle() {
            return MiniMessage.miniMessage().deserialize(noVoteWhileIdle);
        }

        // No Vote While Away
        @Comment("\nAppears if player tries to start a vote while away.")
        private String noVoteWhileAway = "<red>You cannot start a vote while away!";

        public Component noVoteWhileAway() {
            return MiniMessage.miniMessage().deserialize(noVoteWhileAway);
        }

        // Vote cooldown
        @Comment("\nAppears when a player tries to start a vote too quickly after a failed vote.")
        private String cooldown = "<red>You cannot start another vote, a vote just failed!";

        public Component cooldown() {
            return MiniMessage.miniMessage().deserialize(cooldown);
        }
    }

    @Comment("""
            ================================================================================

                                              Before Vote

            ================================================================================

            Strings used in messages appearing before a vote starts.""")
    private BeforeVote beforeVote = new BeforeVote();

    /**
     * Returns the {@link BeforeVote} section of the messages configuration.
     *
     * @return the before-vote message configuration section
     */
    public BeforeVote beforeVote() {
        return beforeVote;
    }

    /*================================================================================

                                       During Vote

    ================================================================================*/

    /**
     * Message values used while a vote is in progress.
     *
     * <p>This includes messages for starting a vote, button prompts, "you voted"
     * confirmations, idle/away notifications, action bar messages, and boss bar
     * display text such as current vote tallies.</p>
     */
    @ConfigSerializable
    public static class DuringVote {
        // Player Started A Vote
        @Comment("""

                Appears once a player has started a vote.
                Possible tags: <player_name> = Player's name, <vote_type> = Vote type (Day/Night)""")
        private String playerStartedVote = "<player_name> has started a vote to skip the <vote_type>!";

        @Comment("\nAppears before the \"Yes/No\" buttons.")
        private String pleaseVote = "Please vote: ";

        @Comment("""

                Appears in hover text for "Yes/No" buttons.
                Possible tags: <vote_value> = yes/no""")
        private String clickHereToVote = "<gold><bold>Click here to vote <vote_value>";

        /**
         * Builds the message shown when a player has started a vote.
         *
         * @param name     the player name
         * @param voteType the vote type display string
         * @return the {@link Component} message including the vote header
         */
        public Component voteStarted(String name, String voteType) {
            TagResolver tagResolver = TagResolver.resolver(
                    Placeholder.parsed("player_name", name),
                    Placeholder.parsed("vote_type", voteType)
            );
            return Messages.voteHeader().append(MiniMessage.miniMessage().deserialize(playerStartedVote, tagResolver).colorIfAbsent(NamedTextColor.WHITE));
        }

        /**
         * Builds the "Please vote" line followed by Yes/No buttons for the given vote type.
         *
         * @param voteType the vote type keyword (e.g. {@code "day"} or {@code "night"})
         * @return a {@link Component} containing interactive Yes/No buttons
         */
        public Component voteButtons(String voteType) {
            String yes = lgeneral().yesString().substring(0, 1).toUpperCase() + lgeneral().yesString().substring(1);
            String no = lgeneral().noString().substring(0, 1).toUpperCase() + lgeneral().noString().substring(1);
            return Messages.hyphenHeader().append(MiniMessage.miniMessage().deserialize(pleaseVote).colorIfAbsent(NamedTextColor.WHITE))
                    .append(Component.text("[" + yes + "]", NamedTextColor.GREEN, TextDecoration.BOLD)
                        .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(clickHereToVote,
                                Placeholder.parsed("vote_value", lgeneral().yesString()))))
                        .clickEvent(ClickEvent.runCommand("/skip" + voteType + " yes")))
                    .append(Component.text(" [" + no + "]", NamedTextColor.DARK_RED, TextDecoration.BOLD)
                        .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(clickHereToVote,
                                Placeholder.parsed("vote_value", lgeneral().noString()))))
                        .clickEvent(ClickEvent.runCommand("/skip" + voteType + " no")));
        }

        // You Voted
        @Comment("""

                Appears when the player votes yes/no.
                Possible tags: <vote_value> = yes/no""")
        private String youVote = "You voted <blue><bold><vote_value></bold></blue>.";

        public Component youVoteYes() {
            return Messages.hyphenHeader()
                    .append(MiniMessage.miniMessage().deserialize(youVote, Placeholder.parsed("vote_value", lgeneral().yesString())).colorIfAbsent(NamedTextColor.WHITE));
        }

        public Component youVoteNo() {
            return Messages.hyphenHeader()
                    .append(MiniMessage.miniMessage().deserialize(youVote, Placeholder.parsed("vote_value", lgeneral().noString())).colorIfAbsent(NamedTextColor.WHITE));
        }

        // 10 Seconds Left
        @Comment("\nAppears when there's 10 seconds left in a vote.")
        private String tenSecondsLeft = "<blue><bold>10</bold></blue> seconds left to vote!";

        public Component tenSecondsLeft() {
            return Messages.voteHeader().append(MiniMessage.miniMessage().deserialize(tenSecondsLeft).colorIfAbsent(NamedTextColor.WHITE));
        }

        // You Have Already Voted
        @Comment("\nAppears if player attempts to vote more than once.")
        private String alreadyVoted = "<red>You have already voted!";

        public Component alreadyVoted() {
            return Messages.voteHeader().append(MiniMessage.miniMessage().deserialize(alreadyVoted));
        }

        // Idle
        @Comment("\nAppears if player attempts to vote while idle.")
        private String idle = "You are <dark_aqua><bold>idle</bold></dark_aqua>, your vote will not count.";

        public Component idle() {
            return Messages.hyphenHeader().append(MiniMessage.miniMessage().deserialize(idle).colorIfAbsent(NamedTextColor.WHITE));
        }

        // Away
        @Comment("\nAppears if player attempts to vote while away.")
        private String away = "You are <dark_blue><bold>away</bold></dark_blue>, your vote will not count.";

        public Component away() {
            return Messages.hyphenHeader().append(MiniMessage.miniMessage().deserialize(away).colorIfAbsent(NamedTextColor.WHITE));
        }

        // Back
        @Comment("\nAppears if player returns during a vote.")
        private String back = "Welcome back.";

        public Component back() {
            return Messages.hyphenHeader().append(MiniMessage.miniMessage().deserialize(back).colorIfAbsent(NamedTextColor.WHITE));
        }

        // Left World
        @Comment("\nAppears if player leaves the world during a vote.")
        private String leftWorld = "You left the world, your vote will not count.";

        public Component leftWorld() {
            return Messages.voteHeader().append(MiniMessage.miniMessage().deserialize(leftWorld).colorIfAbsent(NamedTextColor.WHITE));
        }

        // In Bed Vote Yes
        @Comment("\nAppears if player sleeps during a vote to skip the night.")
        private String inBedVotedYes = "You are now in bed, automatically voting yes.";

        public Component inBedVotedYes() {
            return Messages.hyphenHeader().append(MiniMessage.miniMessage().deserialize(inBedVotedYes).colorIfAbsent(NamedTextColor.WHITE));
        }

        // Vote In Progress
        @Comment("\nAppears if a vote is already in progress.")
        private String voteInProg = "<red>Vote already in progress!";

        public Component voteInProg() {
            return MiniMessage.miniMessage().deserialize(voteInProg);
        }

        // Action Bar Messages
        // Player Has Not Slept
        @Comment("""

                Appears in all players' action bar if player tries to vote but hasn't slept in 3 days.
                Possible tags: <player_name> = Player's name""")
        private String playerHasNotSlept = "<blue><player_name> needs to sleep in a bed before voting!";

        public Component playerHasNotSlept(String name) {
            return MiniMessage.miniMessage().deserialize(playerHasNotSlept, Placeholder.parsed("player_name", name));
        }

        // Player Has Voted
        @Comment("""

                Appears in all player's action bar when a player votes yes/no.
                Possible tags: <player_name> = Player's name, <vote_value> = yes/no""")
        private String playerHasVoted = "<blue><player_name> has voted <vote_value>!";

        public Component playerHasVotedYes(String name) {
            return MiniMessage.miniMessage().deserialize(playerHasVoted,
                    TagResolver.resolver(
                            Placeholder.parsed("player_name", name),
                            Placeholder.parsed("vote_value", lgeneral().yesString())
                    ));
        }

        public Component playerHasVotedNo(String name) {
            return MiniMessage.miniMessage().deserialize(playerHasVoted,
                    TagResolver.resolver(
                            Placeholder.parsed("player_name", name),
                            Placeholder.parsed("vote_vale", lgeneral().noString())
                    ));
        }

        // Boss Bar Messages
        // Vote Progress
        @Comment("""

                Appears on the boss bar before the tally of votes.
                (Does not accept color codes)""")
        private String currentVote = "Current Vote:";

        /**
         * Builds the boss bar text showing the full vote breakdown when player activity
         * tracking is enabled (yes, no, idle, away).
         *
         * @param yes   count of yes votes
         * @param no    count of no votes
         * @param idle  count of idle players
         * @param away  count of away players
         * @return a {@link Component} representing the current vote breakdown
         */
        public Component currentVotePA(int yes, int no, int idle, int away) {
            String yesStr = lgeneral().yesString().substring(0, 1).toUpperCase() + lgeneral().yesString().substring(1);
            String noStr = lgeneral().noString().substring(0, 1).toUpperCase() + lgeneral().noString().substring(1);
            return MiniMessage.miniMessage().deserialize(currentVote)
                    .append(Component.text(" " + yesStr, NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" - " + yes, NamedTextColor.WHITE))
                    .append(Component.text(" " + noStr, NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .append(Component.text(" - " + no, NamedTextColor.WHITE))
                    .append(Component.text(" Idle", NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
                    .append(Component.text(" - " + idle, NamedTextColor.WHITE))
                    .append(Component.text(" Away", NamedTextColor.BLUE, TextDecoration.BOLD))
                    .append(Component.text(" - " + away, NamedTextColor.WHITE));
        }

        /**
         * Builds the boss bar text showing the basic vote breakdown (yes/no only).
         *
         * @param yes count of yes votes
         * @param no  count of no votes
         * @return a {@link Component} representing the current vote breakdown
         */
        public Component currentVote(int yes, int no) {
            String yesStr = lgeneral().yesString().substring(0, 1).toUpperCase() + lgeneral().yesString().substring(1);
            String noStr = lgeneral().noString().substring(0, 1).toUpperCase() + lgeneral().noString().substring(1);
            return MiniMessage.miniMessage().deserialize(currentVote)
                    .append(Component.text(" " + yesStr, NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" - " + yes, NamedTextColor.WHITE))
                    .append(Component.text(" " + noStr, NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .append(Component.text(" - " + no, NamedTextColor.WHITE));
        }
    }

    @Comment("""
            ================================================================================

                                              During Vote

            ================================================================================

            Strings used in messages appearing during a vote.""")
    private DuringVote duringVote = new DuringVote();

    /**
     * Returns the {@link DuringVote} section of the messages configuration.
     *
     * @return the during-vote message configuration section
     */
    public DuringVote duringVote() {
        return duringVote;
    }

    /*================================================================================

                                       After Vote

    ================================================================================*/

    /**
     * Message values used after a vote has completed.
     *
     * <p>This includes messages for passed/failed votes, "all players have voted",
     * and boss bar text for final states and "already day/night" conditions.</p>
     */
    @ConfigSerializable
    public static class AfterVote {
        // Vote Passed
        @Comment("""

                Appears when the vote passes.
                Possible tags: <vote_type> = Day/Night""")
        private String votePassed = "Vote <blue><bold>passed</bold></blue>! Skipping the <vote_type>.";

        public Component votePassedBossBar(String voteType) {
            return Messages.voteHeader()
                    .append(MiniMessage.miniMessage().deserialize(votePassed, Placeholder.parsed("vote_type", voteType)).colorIfAbsent(NamedTextColor.WHITE));
        }

        // Vote Failed
        @Comment("""

                Appears when the vote fails.
                Possible tags: <vote_type> = Day/Night""")
        private String voteFailed = "Vote <blue><bold>failed</bold></blue>! The <vote_type> will not be skipped.";

        public Component voteFailedBossBar(String voteType) {
            return Messages.voteHeader()
                    .append(MiniMessage.miniMessage().deserialize(voteFailed, Placeholder.parsed("vote_type", voteType)).colorIfAbsent(NamedTextColor.WHITE));
        }

        // All Players Have Voted
        @Comment("""

                Appears on the boss bar after all players have voted.
                (Does not accept color codes)""")
        private String allPlayersHaveVoted = "All players have voted!";

        public Component allPlayersHaveVoted() {
            return Component.text(allPlayersHaveVoted, NamedTextColor.GOLD);
        }

        // Vote Passed Boss Bar
        @Comment("""

                Appears on the boss bar when the vote passes.
                (Does not accept color codes)""")
        private String votePassedBossBar = "Vote passed!";

        public Component votePassedBossBar() {
            return Component.text(votePassedBossBar, NamedTextColor.GREEN);
        }

        // Vote Failed Boss Bar
        @Comment("""

                Appears on the boss bar when the vote fails.
                (Does not accept color codes)""")
        private String voteFailedBossBar = "Vote failed!";

        public Component voteFailedBossBar() {
            return Component.text(voteFailedBossBar, NamedTextColor.DARK_RED);
        }

        // It is already day/night
        @Comment("""

                Appears on the boss bar if it becomes day/night during opposite vote.
                Possible tags: <vote_type> = Day/Night
                (Does not accept color codes)""")
        private String itIsAlready = "It is already <vote_type>!";

        public Component itIsAlreadyDay() {
            return Component.text("", NamedTextColor.BLUE)
                    .append(MiniMessage.miniMessage().deserialize(itIsAlready, Placeholder.parsed("vote_type", lgeneral().dayString())));
        }

        public Component itIsAlreadyNight() {
            return Component.text("", NamedTextColor.BLUE)
                    .append(MiniMessage.miniMessage().deserialize(itIsAlready, Placeholder.parsed("vote_type", lgeneral().nightString())));
        }
    }

    @Comment("""
            ================================================================================

                                              After Vote

            ================================================================================

            Strings used in messages appearing after a vote.""")
    private AfterVote afterVote = new AfterVote();

    /**
     * Returns the {@link AfterVote} section of the messages configuration.
     *
     * @return the after-vote message configuration section
     */
    public AfterVote afterVote() {
        return afterVote;
    }

    // Headers

    /**
     * Builds the standard "[Vote]" header component.
     *
     * <p>If the header is disabled in the {@link MessagesContext}, this will return
     * an empty component instead.</p>
     *
     * @return the vote header component, or an empty component if disabled
     */
    public static Component voteHeader() {
        Component header = Component.text("[", NamedTextColor.GRAY)
                .append(Component.text(lgeneral().voteString(), NamedTextColor.BLUE))
                .append(Component.text("] ", NamedTextColor.GRAY));
        return messagesContext.isHeaderDisabled() ? Component.text("") : header;
    }

    /**
     * Builds the " - " hyphen prefix used in many messages.
     *
     * @return the hyphen header component
     */
    public static Component hyphenHeader() {
        return Component.text(" - ", NamedTextColor.BLUE)
                .append(Component.text("", NamedTextColor.WHITE));
    }

    /**
     * Returns the configured word for "day" from the {@link General} section.
     *
     * @return the configured day string
     */
    public String getDayString() {
        return general().dayString();
    }

    /**
     * Returns the configured word for "night" from the {@link General} section.
     *
     * @return the configured night string
     */
    public String getNightString() {
        return general().nightString();
    }

    /**
     * Convenience accessor for the {@link General} section of the globally
     * initialized {@link Messages} instance.
     *
     * @return the {@link General} section from the current {@link MessagesContext}
     */
    private static General lgeneral() {
        return messagesContext.general();
    }
}
