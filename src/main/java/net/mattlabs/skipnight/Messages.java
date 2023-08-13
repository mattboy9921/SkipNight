package net.mattlabs.skipnight;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.text.MessageFormat;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Messages {

    // Header fields
    @Setting(value = "_schema-version")
    @Comment("#######################################################################################################\n" +
            "Skipnight Messages Configuration\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/SkipNight\n\n" +

            "This configuration contains every string of text found in this plugin.\n\n" +

            "For values that contain variables, they are shown as \"{0}\"\n" +
            "and their value is shown in the comment above the line.\n" +
            "It is not necessary to include every variable, but certain strings won't make sense otherwise.\n\n" +

            "Colors and text style can be specified using XML-like tags, for example: \"<white>\".\n" +
            "Standard Minecraft colors/styles are available. Hex colors can be specified with \"<color:#XXXXXX>\".\n" +
            "Please note, some values cannot use color codes (\"<white>\") as denoted in the comment above the value.\n" +
            "#######################################################################################################\n\n" +
            "Config version. Do not change this!")
    private int schemaVersion = 1;

    /*================================================================================

                                         General

    ================================================================================*/

    @ConfigSerializable
    public static class General {
        @Comment("\nVote text that appears before certain messages.\n" +
                "(Does not accept color codes)")
        private String voteString = "Vote";

        public String voteString() {
            return voteString;
        }

        @Comment("\nValue for the word \"day\".\n" +
                "(Does not accept color codes)")
        private String dayString = "day";

        public String dayString() {
            return dayString;
        }

        @Comment("\nValue for the word \"night\".\n" +
                "(Does not accept color codes)")
        private String nightString = "night";

        public String nightString() {
            return nightString;
        }

        @Comment("\nValue for the word \"yes\".\n" +
                "(Does not accept color codes)")
        String yesString = "yes";

        public String yesString() {
            return yesString;
        }

        @Comment("\nValue for the word \"no\".\n" +
                "(Does not accept color codes)")
        private String noString = "no";

        public String noString() {
            return noString;
        }

        // No Permission
        @Comment("\nAppears if player doesn't have permission to vote.")
        private String noPerm = "<red>You don't have permission to run this!";

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

    public General general() {
        return general;
    }

    /*================================================================================

                                        Before Vote

    ================================================================================*/

    @ConfigSerializable
    public static class BeforeVote {
        @Comment("\nButton text for \"Start Vote\" button.\n" +
                "(Does not accept color codes)")
        private String startVote = "Start Vote";

        @Comment("\nHover text for \"Start Vote\" button.")
        private String clickToStartVote = "<gold><bold>Click here to start a vote";

        @Comment("\nAppears if a vote isn't in progress.\n" +
                "0 = \"Start Vote\" button")
        private String noVoteInProg = "<red>No vote in progress! <blue>{0}";

        @Comment("\nAppears if player sleeps with no vote in progress.\n" +
                "0 = \"Start Vote\" button")
        private String inBedNoVoteInProg = "<reset>Start a vote to skip the night? <blue>{0}";

        public Component noVoteInProg(String voteType) {
            // No vote in progress! [Start Vote] (runs /skipday|night)
            return MiniMessage.miniMessage().deserialize(MessageFormat.format(noVoteInProg,
                    "<click:suggest_command:/skip" + voteType + ">" +
                            "<hover:show_text:'" + clickToStartVote + "'>" +
                            "<bold>[" + startVote + "]"));
        }

        public Component inBedNoVoteInProg() {
            // <gray>[<blue>Vote<gray>] <reset>Start a vote to skip the night? || <blue><bold>[Vote]||cmd:/skipnight||ttp:<gold><bold>Click <reset>here to start a vote.
            return MiniMessage.miniMessage().deserialize(voteHeader() + MessageFormat.format(inBedNoVoteInProg,
                    "<click:run_command:/skipnight>" +
                            "<hover:show_text:'" + clickToStartVote + "'>" +
                            "<bold>[" + startVote + "]"));
        }

        @Comment("\nAppears if player attempts to vote after 3 days without sleep.")
        private String mustSleep = "<reset><red>You must sleep in a bed first!";

        public Component mustSleep() {
            // <blue> - <reset><red>You must sleep in a bed first!
            return MiniMessage.miniMessage().deserialize(hyphenHeader + mustSleep);
        }

        public Component mustSleepNewVote() {
            // <red>You must sleep in a bed first!
            return MiniMessage.miniMessage().deserialize(mustSleep);
        }

        // World Is Blacklisted
        @Comment("\nAppears if player tries to vote in a blacklisted world.")
        private String worldIsBlacklisted = "<red>You cannot start a vote in this world!";

        public Component worldIsBlacklisted() {
            // <red>You cannot start a vote in this world!
            return MiniMessage.miniMessage().deserialize(worldIsBlacklisted);
        }

        // Must Be In Overworld
        @Comment("\nAppears if player tries to vote outside of the overworld.")
        private String worldNotOverworld = "<red>You must be in the overworld to start a vote!";

        public Component worldNotOverworld() {
            // <red>You must be in the overworld to start a vote!
            return MiniMessage.miniMessage().deserialize(worldNotOverworld);
        }

        // Can Only Vote At Night
        @Comment("\nAppears if player tries to start a vote to skip the night during the day.")
        private String canOnlyVoteAtNight = "<red>You can only start a vote at night!";

        public Component canOnlyVoteAtNight() {
            // <red>You can only start a vote at night!
            return MiniMessage.miniMessage().deserialize(canOnlyVoteAtNight);
        }

        // Can Only Vote At Day
        @Comment("\nAppears if player tries to start a vote to skip the day during the night.")
        private String canOnlyVoteAtDay = "<red>You can only start a vote during the day!";

        public Component canOnlyVoteAtDay() {
            // <red>You can only start a vote during the day!
            return MiniMessage.miniMessage().deserialize(canOnlyVoteAtDay);
        }

        // No Vote While Idle
        @Comment("\nAppears if player tries to start a vote while idle.")
        private String noVoteWhileIdle = "<red>You cannot start a vote while idle!";

        public Component noVoteWhileIdle() {
            // <red>You cannot start a vote while idle!
            return MiniMessage.miniMessage().deserialize(noVoteWhileIdle);
        }

        // No Vote While Away
        @Comment("\nAppears if player tries to start a vote while away.")
        private String noVoteWhileAway = "<red>You cannot start a vote while away!";

        public Component noVoteWhileAway() {
            // <red>You cannot start a vote while away!
            return MiniMessage.miniMessage().deserialize(noVoteWhileAway);
        }

        // Vote cooldown
        @Comment("\nAppears when a player tries to start a vote too quickly after a failed vote.\n" +
                "(Does not accept color codes)")
        private String cooldown = "You cannot start another vote, a vote just failed!";

        public Component cooldown() {
            return MiniMessage.miniMessage().deserialize("<red>" + cooldown);
        }
    }

    @Comment("""
            ================================================================================

                                              Before Vote

            ================================================================================

            Strings used in messages appearing before a vote starts.""")
    private BeforeVote beforeVote = new BeforeVote();

    public BeforeVote beforeVote() {
        return beforeVote;
    }

    /*================================================================================

                                       During Vote

    ================================================================================*/

    @ConfigSerializable
    public static class DuringVote {
        // Player Started A Vote
        @Comment("\nAppears once a player has started a vote.\n" +
                "0 = Player's name, 1 = Vote type (Day/Night)")
        private String playerStartedVote = "<reset>{0} has started a vote to skip the {1}!";

        @Comment("\nAppears before the \"Yes/No\" buttons.")
        private String pleaseVote = "Please vote: ";

        @Comment("\nAppears in hover text for \"Yes/No\" buttons.\n" +
                "0 = yes/no")
        private String clickHereToVote = "<gold><bold>Click here to vote {0}";

        public Component voteStarted(String name, String voteType) {
            // <gray>[<blue>Vote<gray>] <reset>%name has started a vote to skip the day|night!
            return MiniMessage.miniMessage().deserialize(voteHeader() + MessageFormat.format(playerStartedVote, name, voteType));
        }

        public Component voteButtons(String voteType) {
            // <blue> - <reset>Please vote: || <green><bold>[Yes]||cmd:/skipday|night yes||ttp:<gold><bold>Click <reset>here to vote yes.|| <dark_red><bold>[No]||cmd:/skipday|night no||ttp:<gold><bold>Click <reset>here to vote no.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + pleaseVote +
                    "<green><bold><click:run_command:/skip" + voteType + " yes>" +
                    "<hover:show_text:'" + MessageFormat.format(clickHereToVote, lgeneral().yesString()) + "'>" +
                    "[" + lgeneral().yesString().substring(0, 1).toUpperCase() + lgeneral().yesString().substring(1) + "]</hover></click> " +
                    "<dark_red><bold><click:run_command:/skip" + voteType + " no>" +
                    "<hover:show_text:'" + MessageFormat.format(clickHereToVote, lgeneral().noString()) + "'>" +
                    "[" + lgeneral().noString().substring(0, 1).toUpperCase() + lgeneral().noString().substring(1) + "]");
        }

        // You Voted
        @Comment("\nAppears when the player votes yes/no.")
        private String youVote = "<reset>You voted <blue><bold>{0}<reset>.";

        public Component youVoteYes() {
            // <blue> - <reset>You voted <blue><bold>yes<reset>.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + MessageFormat.format(youVote, lgeneral().yesString()));
        }

        public Component youVoteNo() {
            // <blue> - <reset>You voted <blue><bold>no<reset>.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + MessageFormat.format(youVote, lgeneral().noString()));
        }

        // 10 Seconds Left
        @Comment("\nAppears when there's 10 seconds left in a vote.")
        private String tenSecondsLeft = "<blue><bold>10 <reset>seconds left to vote!";

        public Component tenSecondsLeft() {
            // <gray>[<blue>Vote<gray>] <blue><bold>10 <reset>seconds left to vote!
            return MiniMessage.miniMessage().deserialize(voteHeader() + tenSecondsLeft);
        }

        // You Have Already Voted
        @Comment("\nAppears if player attempts to vote more than once.")
        private String alreadyVoted = "<reset><red>You have already voted!";

        public Component alreadyVoted() {
            // <blue> - <reset><red>You have already voted!
            return MiniMessage.miniMessage().deserialize(hyphenHeader + alreadyVoted);
        }

        // Idle
        @Comment("\nAppears if player attempts to vote while idle.")
        private String idle = "<reset>You are <dark_aqua><bold>idle<reset>, your vote will not count.";

        public Component idle() {
            // <blue> - <reset>You are <dark_aqua><bold>idle<reset>, your vote will not count.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + idle);
        }

        // Away
        @Comment("\nAppears if player attempts to vote while away.")
        private String away = "<reset>You are <dark_blue><bold>away<reset>, your vote will not count.";

        public Component away() {
            // <blue> - <reset>You are <dark_blue><bold>away<reset>, your vote will not count.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + away);
        }

        // Back
        @Comment("\nAppears if player returns during a vote.")
        private String back = "<reset>Welcome back.";

        public Component back() {
            // <blue> - <reset>Welcome back.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + back);
        }

        // Left World
        @Comment("\nAppears if player leaves the world during a vote.")
        private String leftWorld = "<reset>You left the world, your vote will not count.";

        public Component leftWorld() {
            // <gray>[<blue>Vote<gray>] <reset>You left the world, your vote will not count.
            return MiniMessage.miniMessage().deserialize(voteHeader() + leftWorld);
        }

        // In Bed Vote Yes
        @Comment("\nAppears if player sleeps during a vote to skip the night.")
        private String inBedVotedYes = "<reset>You are now in bed, automatically voting yes.";

        public Component inBedVotedYes() {
            // <blue> - <reset>You are now in bed, automatically voting yes.
            return MiniMessage.miniMessage().deserialize(hyphenHeader + inBedVotedYes);
        }

        // Vote In Progress
        @Comment("\nAppears if a vote is already in progress.")
        private String voteInProg = "<red>Vote already in progress!";

        public Component voteInProg() {
            // <red>Vote already in progress!
            return MiniMessage.miniMessage().deserialize(voteInProg);
        }

        // Action Bar Messages
        // Player Has Not Slept
        @Comment("\nAppears in all players' action bar if player tries to vote but hasn't slept in 3 days.\n" +
                "0 = Player's name")
        private String playerHasNotSlept = "<blue>{0} needs to sleep in a bed before voting!";

        public Component playerHasNotSlept(String name) {
            // <blue>%name% needs to sleep in a bed before voting!
            return MiniMessage.miniMessage().deserialize(MessageFormat.format(playerHasNotSlept, name));
        }

        // Player Has Voted
        @Comment("\nAppears in all player's action bar when a player votes yes/no.\n" +
                "0 = Player's name, 1 = yes/no")
        private String playerHasVoted = "<blue>{0} has voted {1}!";

        public Component playerHasVotedYes(String name) {
            // <blue>%name% has voted yes!
            return MiniMessage.miniMessage().deserialize(MessageFormat.format(playerHasVoted, name, lgeneral().yesString()));
        }

        public Component playerHasVotedNo(String name) {
            // <blue>%name% has voted no!
            return MiniMessage.miniMessage().deserialize(MessageFormat.format(playerHasVoted, name, lgeneral().noString()));
        }

        // Boss Bar Messages
        // Vote Progress
        @Comment("\nAppears on the boss bar before the tally of votes.\n" +
                "(Does not accept color codes)")
        private String currentVote = "Current Vote:";

        public Component currentVotePA(int yes, int no, int idle, int away) {
            // Current Vote: Yes - X No - X Idle - X Away - X
            return MiniMessage.miniMessage().deserialize(currentVote +
                    " <bold><green>" + lgeneral().yesString().substring(0, 1).toUpperCase() + lgeneral().yesString().substring(1) + "<reset> - " + yes +
                    " <bold><dark_red>" + lgeneral().noString().substring(0, 1).toUpperCase() + lgeneral().noString().substring(1) + "<reset> - " + no +
                    " <bold><dark_aqua>Idle<reset> - " + idle +
                    " <bold><blue>Away<reset> - " + away);
        }

        public Component currentVote(int yes, int no) {
            // Current Vote: Yes - X No - X
            return MiniMessage.miniMessage().deserialize(currentVote +
                    " <bold><green>" + lgeneral().yesString().substring(0, 1).toUpperCase() + lgeneral().yesString().substring(1) + "<reset> - " + yes +
                    " <bold><dark_red>" + lgeneral().noString().substring(0, 1).toUpperCase() + lgeneral().noString().substring(1) + "<reset> - " + no);
        }
    }

    @Comment("""
            ================================================================================

                                              During Vote

            ================================================================================

            Strings used in messages appearing during a vote.""")
    private DuringVote duringVote = new DuringVote();

    public DuringVote duringVote() {
        return duringVote;
    }

    /*================================================================================

                                       After Vote

    ================================================================================*/

    @ConfigSerializable
    public static class AfterVote {
        // Vote Passed
        @Comment("\nAppears when the vote passes.\n" +
                "0 = Day/Night")
        private String votePassed = "<reset>Vote <blue><bold>passed<reset>! Skipping the {0}.";

        public Component votePassedBossBar(String voteType) {
            // <gray>[<blue>Vote<gray>] <reset>Vote <blue><bold>passed<reset>! Skipping the day|night.
            return MiniMessage.miniMessage().deserialize(voteHeader() + MessageFormat.format(votePassed, voteType));
        }

        // Vote Failed
        @Comment("\nAppears when the vote fails.\n" +
                "0 = Day/Night")
        private String voteFailed = "<reset>Vote <blue><bold>failed<reset>! The {0} will not be skipped.";

        public Component voteFailedBossBar(String voteType) {
            // <gray>[<blue>Vote<gray>] <reset>Vote <blue><bold>failed<reset>! The day|night will not be skipped.
            return MiniMessage.miniMessage().deserialize(voteHeader() + MessageFormat.format(voteFailed, voteType));
        }

        // All Players Have Voted
        @Comment("\nAppears on the boss bar after all players have voted.\n" +
                "(Does not accept color codes)")
        private String allPlayersHaveVoted = "All players have voted!";

        public Component allPlayersHaveVoted() {
            // All players have voted!
            return MiniMessage.miniMessage().deserialize("<gold>" +  allPlayersHaveVoted);
        }

        // Vote Passed Boss Bar
        @Comment("\nAppears on the boss bar when the vote passes.\n" +
                "(Does not accept color codes)")
        private String votePassedBossBar = "Vote passed!";

        public Component votePassedBossBar() {
            // Vote passed!
            return MiniMessage.miniMessage().deserialize("<green>" + votePassedBossBar);
        }

        // Vote Failed Boss Bar
        @Comment("\nAppears on the boss bar when the vote fails.\n" +
                "(Does not accept color codes)")
        private String voteFailedBossBar = "Vote failed!";

        public Component voteFailedBossBar() {
            // Vote failed!
            return MiniMessage.miniMessage().deserialize("<dark_red>" + voteFailedBossBar);
        }

        // It is already day/night
        @Comment("\nAppears on the boss bar if it becomes day/night during opposite vote.\n" +
                "0 = Day/Night\n" +
                "(Does not accept color codes)")
        private String itIsAlready = "It is already {0}!";

        public Component itIsAlreadyDay() {
            // It is already day!
            return MiniMessage.miniMessage().deserialize(MessageFormat.format("<blue>" + itIsAlready, lgeneral().dayString()));
        }

        public Component itIsAlreadyNight() {
            // It is already night!
            return MiniMessage.miniMessage().deserialize(MessageFormat.format("<blue>" + itIsAlready, lgeneral().nightString()));
        }
    }

    @Comment("""
            ================================================================================

                                              After Vote

            ================================================================================

            Strings used in messages appearing after a vote.""")
    private AfterVote afterVote = new AfterVote();

    public AfterVote afterVote() {
        return afterVote;
    }

    // Headers
    // [Vote]
    private static String voteHeader() {
        return SkipNight.getInstance().getConfiguration().isHeaderDisabled() ? "" : "<gray>[<blue>" + lgeneral().voteString() + "<gray>]<reset> ";
    }

    // -
    private static String hyphenHeader = " <blue>-<reset> ";

    public String getDayString() {
        return general().dayString();
    }

    public String getNightString() {
        return general().nightString();
    }

    private static General lgeneral() {
        return SkipNight.getInstance().getMessages().general();
    }
}
