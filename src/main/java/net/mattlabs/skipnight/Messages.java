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
    private int schemaVersion = 0;

    // General fields used in many strings
    @Comment("\nVote text that appears before certain messages.\n" +
            "(Does not accept color codes)")
    private String vote = "Vote";

    @Comment("\nValue for the word \"day\".\n" +
            "(Does not accept color codes)")
    private String day = "day";

    @Comment("\nValue for the word \"night\".\n" +
            "(Does not accept color codes)")
    private String night = "night";

    @Comment("\nValue for the word \"yes\".\n" +
            "(Does not accept color codes)")
    private String yes = "yes";

    @Comment("\nValue for the word \"no\".\n" +
            "(Does not accept color codes)")
    private String no = "no";

    @Comment("\nButton text for \"Start Vote\" button.\n" +
            "(Does not accept color codes)")
    private String startVote = "Start Vote";

    @Comment("\nHover text for \"Start Vote\" button.")
    private String clickToStartVote = "<gold><bold>Click here to start a vote";

    // You Must Sleep First
    @Comment("\nAppears if player attempts to vote after 3 days without sleep.")
    private String mustSleep = "<reset><red>You must sleep in a bed first!";

    // No Vote In Progress
    @Comment("\nAppears if a vote isn't in progress.\n" +
            "0 = \"Start Vote\" button")
    private String noVoteInProg = "<red>No vote in progress! <blue>{0}";

    // Player Started A Vote
    @Comment("\nAppears once a player has started a vote.\n" +
            "0 = Player's name, 1 = Vote type (Day/Night)")
    private String playerStartedVote = "<reset>{0} has started a vote to skip the {1}!";

    @Comment("\nAppears before the \"Yes/No\" buttons.")
    private String pleaseVote = "Please vote: ";

    @Comment("\nAppears in hover text for \"Yes/No\" buttons.\n" +
            "0 = yes/no")
    private String clickHereToVote = "<gold><bold>Click here to vote {0}";

    // You Voted
    @Comment("\nAppears when the player votes yes/no.")
    private String youVote = "<reset>You voted <blue><bold>{0}<reset>.";

    // 10 Seconds Left
    @Comment("\nAppears when there's 10 seconds left in a vote.")
    private String tenSecondsLeft = "<blue><bold>10 <reset>seconds left to vote!";

    // Vote Passed
    @Comment("\nAppears when the vote passes.\n" +
            "0 = Day/Night")
    private String votePassed = "<reset>Vote <blue><bold>passed<reset>! Skipping the {0}.";

    // Vote Failed
    @Comment("\nAppears when the vote fails.\n" +
            "0 = Day/Night")
    private String voteFailed = "<reset>Vote <blue><bold>failed<reset>! The {0} will not be skipped.";

    // You Have Already Voted
    @Comment("\nAppears if player attempts to vote more than once.")
    private String alreadyVoted = "<reset><red>You have already voted!";

    // Idle
    @Comment("\nAppears if player attempts to vote while idle.")
    private String idle = "<reset>You are <dark_aqua><bold>idle<reset>, your vote will not count.";

    // Away
    @Comment("\nAppears if player attempts to vote while away.")
    private String away = "<reset>You are <dark_blue><bold>away<reset>, your vote will not count.";

    // Back
    @Comment("\nAppears if player returns during a vote.")
    private String back = "<reset>Welcome back.";

    // Left World
    @Comment("\nAppears if player leaves the world during a vote.")
    private String leftWorld = "<reset>You left the world, your vote will not count.";

    // In Bed Vote Yes
    @Comment("\nAppears if player sleeps during a vote to skip the night.")
    private String inBedVotedYes = "<reset>You are now in bed, automatically voting yes.";

    // In Bed No Vote In Progress
    @Comment("\nAppears if player sleeps with no vote in progress.\n" +
            "0 = \"Start Vote\" button")
    private String inBedNoVoteInProg = "<reset>Start a vote to skip the night? <blue>{0}";

    // No Permission
    @Comment("\nAppears if player doesn't have permission to vote.")
    private String noPerm = "<red>You don't have permission to run this!";

    // Must Be In Overworld
    @Comment("\nAppears if player tries to vote outside of the overworld.")
    private String wrongWorld = "<red>You must be in the overworld to start a vote!";

    // Can Only Vote At Night
    @Comment("\nAppears if player tries to start a vote to skip the night during the day.")
    private String canOnlyVoteAtNight = "<red>You can only start a vote at night!";

    // Can Only Vote At Day
    @Comment("\nAppears if player tries to start a vote to skip the day during the night.")
    private String canOnlyVoteAtDay = "<red>You can only start a vote during the day!";

    // No Vote While Idle
    @Comment("\nAppears if player tries to start a vote while idle.")
    private String noVoteWhileIdle = "<red>You cannot start a vote while idle!";

    // No Vote While Away
    @Comment("\nAppears if player tries to start a vote while away.")
    private String noVoteWhileAway = "<red>You cannot start a vote while away!";

    // Vote In Progress
    @Comment("\nAppears if a vote is already in progress.")
    private String voteInProg = "<red>Vote already in progress!";

    // Action Bar Messages
    // Player Has Not Slept
    @Comment("\nAppears in all players' action bar if player tries to vote but hasn't slept in 3 days.\n" +
            "0 = Player's name")
    private String playerHasNotSlept = "<blue>{0} needs to sleep in a bed before voting!";

    // Player Has Voted
    @Comment("\nAppears in all player's action bar when a player votes yes/no.\n" +
            "0 = Player's name, 1 = yes/no")
    private String playerHasVoted = "<blue>{0} has voted {1}!";

    // Boss Bar Messages
    // Vote Progress
    @Comment("\nAppears on the boss bar before the tally of votes.\n" +
            "(Does not accept color codes)")
    private String currentVote = "Current Vote:";

    // All Players Have Voted
    @Comment("\nAppears on the boss bar after all players have voted.\n" +
            "(Does not accept color codes)")
    private String allPlayersHaveVoted = "All players have voted!";

    // Vote Passed Boss Bar
    @Comment("\nAppears on the boss bar when the vote passes.\n" +
            "(Does not accept color codes)")
    private String votePassedBossBar = "Vote passed!";

    // Vote Failed Boss Bar
    @Comment("\nAppears on the boss bar when the vote fails.\n" +
            "(Does not accept color codes)")
    private String voteFailedBossBar = "Vote failed!";

    // It is already day/night
    @Comment("\nAppears on the boss bar if it becomes day/night during opposite vote.\n" +
            "0 = Day/Night\n" +
            "(Does not accept color codes)")
    private String itIsAlready = "It is already {0}!";

    // Headers
    // [Vote]
    private transient String voteHeader = "<gray>[<blue>" + vote + "<gray>]<reset> ";

    // -
    private transient String hyphenHeader = " <blue>-<reset> ";

    public Component noVoteInProg(String voteType) {
        // No vote in progress! [Start Vote] (runs /skipday|night)
        return MiniMessage.get().parse(MessageFormat.format(noVoteInProg,
                "<click:suggest_command:/skip" + voteType + ">" +
                "<hover:show_text:'" + clickToStartVote + "'>" +
                        "<bold>[" + startVote + "]"));
    }

    public Component voteStarted(String name, String voteType) {
        // <gray>[<blue>Vote<gray>] <reset>%name has started a vote to skip the day|night!
        return MiniMessage.get().parse(voteHeader + MessageFormat.format(playerStartedVote, name, voteType));
    }

    public Component voteButtons(String voteType) {
        // <blue> - <reset>Please vote: || <green><bold>[Yes]||cmd:/skipday|night yes||ttp:<gold><bold>Click <reset>here to vote yes.|| <dark_red><bold>[No]||cmd:/skipday|night no||ttp:<gold><bold>Click <reset>here to vote no.
        return MiniMessage.get().parse(hyphenHeader + pleaseVote +
                "<green><bold><click:run_command:/skip" + voteType + " yes>" +
                "<hover:show_text:'" + MessageFormat.format(clickHereToVote, yes) + "'>" +
                "[" + yes.substring(0, 1).toUpperCase() + yes.substring(1) + "]</hover></click> " +
                "<dark_red><bold><click:run_command:/skip" + voteType + " no>" +
                "<hover:show_text:'" + MessageFormat.format(clickHereToVote, no) + "'>" +
                "[" + no.substring(0, 1).toUpperCase() + no.substring(1) + "]");
    }

    public Component youVoteYes() {
        // <blue> - <reset>You voted <blue><bold>yes<reset>.
        return MiniMessage.get().parse(hyphenHeader + MessageFormat.format(youVote, yes));
    }

    public Component youVoteNo() {
        // <blue> - <reset>You voted <blue><bold>no<reset>.
        return MiniMessage.get().parse(hyphenHeader + MessageFormat.format(youVote, no));
    }

    public Component tenSecondsLeft() {
        // <gray>[<blue>Vote<gray>] <blue><bold>10 <reset>seconds left to vote!
        return MiniMessage.get().parse(voteHeader + tenSecondsLeft);
    }

    public Component votePassedBossBar(String voteType) {
        // <gray>[<blue>Vote<gray>] <reset>Vote <blue><bold>passed<reset>! Skipping the day|night.
        return MiniMessage.get().parse(voteHeader + MessageFormat.format(votePassed, voteType));
    }

    public Component voteFailedBossBar(String voteType) {
        // <gray>[<blue>Vote<gray>] <reset>Vote <blue><bold>failed<reset>! The day|night will not be skipped.
        return MiniMessage.get().parse(voteHeader + MessageFormat.format(voteFailed, voteType));
    }

    public Component alreadyVoted() {
        // <blue> - <reset><red>You have already voted!
        return MiniMessage.get().parse(hyphenHeader + alreadyVoted);
    }

    public Component mustSleep() {
        // <blue> - <reset><red>You must sleep in a bed first!
        return MiniMessage.get().parse(hyphenHeader + mustSleep);
    }

    public Component idle() {
        // <blue> - <reset>You are <dark_aqua><bold>idle<reset>, your vote will not count.
        return MiniMessage.get().parse(hyphenHeader + idle);
    }

    public Component away() {
        // <blue> - <reset>You are <dark_blue><bold>away<reset>, your vote will not count.
        return MiniMessage.get().parse(hyphenHeader + away);
    }

    public Component back() {
        // <blue> - <reset>Welcome back.
        return MiniMessage.get().parse(hyphenHeader + back);
    }

    public Component leftWorld() {
        // <gray>[<blue>Vote<gray>] <reset>You left the world, your vote will not count.
        return MiniMessage.get().parse(voteHeader + leftWorld);
    }

    public Component inBedVotedYes() {
        // <blue> - <reset>You are now in bed, automatically voting yes.
        return MiniMessage.get().parse(hyphenHeader + inBedVotedYes);
    }

    public Component inBedNoVoteInProg() {
        // <gray>[<blue>Vote<gray>] <reset>Start a vote to skip the night? || <blue><bold>[Vote]||cmd:/skipnight||ttp:<gold><bold>Click <reset>here to start a vote.
        return MiniMessage.get().parse(voteHeader + MessageFormat.format(inBedNoVoteInProg,
                        "<click:suggest_command:/skipnight>" +
                        "<hover:show_text:'" + clickToStartVote + "'>" +
                        "<bold>[" + startVote + "]"));
    }

    public Component playerHasNotSlept(String name) {
        // <blue>%name% needs to sleep in a bed before voting!
        return MiniMessage.get().parse(MessageFormat.format(playerHasNotSlept, name));
    }

    public Component playerHasVotedYes(String name) {
        // <blue>%name% has voted yes!
        return MiniMessage.get().parse(MessageFormat.format(playerHasVoted, name, yes));
    }

    public Component playerHasVotedNo(String name) {
        // <blue>%name% has voted no!
        return MiniMessage.get().parse(MessageFormat.format(playerHasVoted, name, no));
    }

    public Component noPerm() {
        // <red>You don't have permission to run this!
        return MiniMessage.get().parse(noPerm);
    }

    public Component wrongWorld() {
        // <red>You must be in the overworld to start a vote!
        return MiniMessage.get().parse(wrongWorld);
    }

    public Component canOnlyVoteAtNight() {
        // <red>You can only start a vote at night!
        return MiniMessage.get().parse(canOnlyVoteAtNight);
    }

    public Component canOnlyVoteAtDay() {
        // <red>You can only start a vote during the day!
        return MiniMessage.get().parse(canOnlyVoteAtDay);
    }

    public Component noVoteWhileIdle() {
        // <red>You cannot start a vote while idle!
        return MiniMessage.get().parse(noVoteWhileIdle);
    }

    public Component noVoteWhileAway() {
        // <red>You cannot start a vote while away!
        return MiniMessage.get().parse(noVoteWhileAway);
    }

    public Component voteInProg() {
        // <red>Vote already in progress!
        return MiniMessage.get().parse(voteInProg);
    }

    public Component mustSleepNewVote() {
        // <red>You must sleep in a bed first!
        return MiniMessage.get().parse(mustSleep);
    }

    public Component currentVotePA(int yes, int no, int idle, int away) {
        // Current Vote: Yes - X No - X Idle - X Away - X
        return MiniMessage.get().parse(currentVote +
                " <bold><green>" + this.yes.substring(0, 1).toUpperCase() + this.yes.substring(1) + "<reset> - " + yes +
                " <bold><dark_red>" + this.no.substring(0, 1).toUpperCase() + this.no.substring(1) + "<reset> - " + no +
                " <bold><dark_aqua>Idle<reset> - " + idle +
                " <bold><blue>Away<reset> - " + away);
    }

    public Component currentVote(int yes, int no) {
        // Current Vote: Yes - X No - X
        return MiniMessage.get().parse(currentVote +
                " <bold><green>" + this.yes.substring(0, 1).toUpperCase() + this.yes.substring(1) + "<reset> - " + yes +
                " <bold><dark_red>" + this.no.substring(0, 1).toUpperCase() + this.no.substring(1) + "<reset> - " + no);
    }

    public Component allPlayersHaveVoted() {
        // All players have voted!
        return MiniMessage.get().parse("<gold>" +  allPlayersHaveVoted);
    }

    public Component votePassedBossBar() {
        // Vote passed!
        return MiniMessage.get().parse("<green>" + votePassedBossBar);
    }

    public Component voteFailedBossBar() {
        // Vote failed!
        return MiniMessage.get().parse("<dark_red>" + voteFailedBossBar);
    }

    public Component itIsAlreadyDay() {
        // It is already day!
        return MiniMessage.get().parse(MessageFormat.format("<blue>" + itIsAlready, day));
    }

    public Component itIsAlreadyNight() {
        // It is already night!
        return MiniMessage.get().parse(MessageFormat.format("<blue>" + itIsAlready, night));
    }

    public String getDayString() {
        return day;
    }

    public String getNightString() {
        return night;
    }
}
