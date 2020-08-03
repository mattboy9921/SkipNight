package net.mattlabs.skipnight;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.text.MessageFormat;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Messages {

    // Header fields
    @Setting(comment = "Skipnight Messages Configuration\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/SkipNight")
    private boolean __mattIsAwesome = true;

    @Setting(value = "_format-code", comment = "\n")
    char _formatCode = '&';

    // General fields used in many strings
    @Setting(comment = "\nVote text that appears before certain messages.\n" +
            "(Does not accept color codes)")
    private String vote = "Vote";

    @Setting(comment = "\nValue for the word \"day\".\n" +
            "(Does not accept color codes)")
    private String day = "day";

    @Setting(comment = "\nValue for the word \"night\".\n" +
            "(Does not accept color codes)")
    private String night = "night";

    @Setting(comment = "\nValue for the word \"yes\".\n" +
            "(Does not accept color codes)")
    private String yes = "yes";

    @Setting(comment = "\nValue for the word \"no\".\n" +
            "(Does not accept color codes)")
    private String no = "no";

    @Setting(value = "start-vote", comment = "\nButton text for \"Start Vote\" button.\n" +
            "(Does not accept color codes)")
    private String startVote = "Start Vote";

    @Setting(value = "click-to-start-vote", comment = "\nHover text for \"Start Vote\" button.")
    private String clickToStartVote = "&6&lClick here to start a vote";

    // You Must Sleep First
    @Setting(value = "must-sleep", comment = "\nAppears if player attempts to vote after 3 days without sleep.")
    private String mustSleep = "&r&cYou must sleep in a bed first!";

    // No Vote In Progress
    @Setting(value = "no-vote-in-prog", comment = "\nAppears if a vote isn't in progress.\n" +
            "0 = \"Start Vote\" button")
    private String noVoteInProg = "&cNo vote in progress! &9{0}";

    // Player Started A Vote
    @Setting(value = "player-started-vote", comment = "\nAppears once a player has started a vote.\n" +
            "0 = Player's name, 1 = Vote type (Day/Night)")
    private String playerStartedVote = "&r{0} has started a vote to skip the {1}!";

    @Setting(value = "please-vote", comment = "\nAppears before the \"Yes/No\" buttons.")
    private String pleaseVote = "Please vote: ";

    @Setting(value = "click-here-to-vote", comment = "\nAppears in hover text for \"Yes/No\" buttons.\n" +
            "0 = yes/no")
    private String clickHereToVote = "&6&lClick here to vote {0}";

    // You Voted
    @Setting(value = "you-voted", comment = "\nAppears when the player votes yes/no.")
    private String youVote = "&rYou voted &9&l{0}&r.";

    // 10 Seconds Left
    @Setting(value = "ten-seconds-left", comment = "\nAppears when there's 10 seconds left in a vote.")
    private String tenSecondsLeft = "&5&l10 &rseconds left to vote!";

    // Vote Passed
    @Setting(value = "vote-passed", comment = "\nAppears when the vote passes.\n" +
            "0 = Day/Night")
    private String votePassed = "&rVote &9&lpassed&r! Skipping the {0}.";

    // Vote Failed
    @Setting(value = "vote-failed", comment = "\nAppears when the vote fails.\n" +
            "0 = Day/Night")
    private String voteFailed = "&rVote &9&lfailed&r! The {0} will not be skipped.";

    // You Have Already Voted
    @Setting(value = "already-voted", comment = "\nAppears if player attempts to vote more than once.")
    private String alreadyVoted = "&r&cYou have already voted!";

    // Idle
    @Setting(comment = "\nAppears if player attempts to vote while idle.")
    private String idle = "&rYou are &3&lidle&r, your vote will not count.";

    // Away
    @Setting(comment = "\nAppears if player attempts to vote while away.")
    private String away = "&rYou are &1&laway&r, your vote will not count.";

    // Back
    @Setting(comment = "\nAppears if player returns during a vote.")
    private String back = "&rWelcome back.";

    // Left World
    @Setting(value = "left-world", comment = "\nAppears if player leaves the world during a vote.")
    private String leftWorld = "&rYou left the world, your vote will not count.";

    // In Bed Vote Yes
    @Setting(value = "in-bed-voted-yes", comment = "\nAppears if player sleeps during a vote to skip the night.")
    private String inBedVotedYes = "&rYou are now in bed, automatically voting yes.";

    // In Bed No Vote In Progress
    @Setting(value = "in-bed-no-vote-in-prog", comment = "\nAppears if player sleeps with no vote in progress.\n" +
            "0 = \"Start Vote\" button")
    private String inBedNoVoteInProg = "&rStart a vote to skip the night? &9{0}";

    // No Permission
    @Setting(value = "no-perm", comment = "\nAppears if player doesn't have permission to vote.")
    private String noPerm = "&cYou don't have permission to run this!";

    // Must Be In Overworld
    @Setting(value = "wrong-world", comment = "\nAppears if player tries to vote outside of the overworld.")
    private String wrongWorld = "&cYou must be in the overworld to start a vote!";

    // Can Only Vote At Night
    @Setting(value = "can-only-vote-at-night", comment = "\nAppears if player tries to start a vote to skip the night during the day.")
    private String canOnlyVoteAtNight = "&cYou can only start a vote at night!";

    // Can Only Vote At Day
    @Setting(value = "can-only-vote-at-day", comment = "\nAppears if player tries to start a vote to skip the day during the night.")
    private String canOnlyVoteAtDay = "&cYou can only start a vote during the day!";

    // No Vote While Idle
    @Setting(value = "no-vote-while-idle", comment = "\nAppears if player tries to start a vote while idle.")
    private String noVoteWhileIdle = "&cYou cannot start a vote while idle!";

    // No Vote While Away
    @Setting(value = "no-vote-while-away", comment = "\nAppears if player tries to start a vote while away.")
    private String noVoteWhileAway = "&cYou cannot start a vote while away!";

    // Vote In Progress
    @Setting(value = "vote-in-prog", comment = "\nAppears if a vote is already in progress.")
    private String voteInProg = "&cVote already in progress!";

    // Action Bar Messages
    // Player Has Not Slept
    @Setting(value = "player-has-not-slept", comment = "\nAppears in all players' action bar if player tries to vote but hasn't slept in 3 days.\n" +
            "0 = Player's name")
    private String playerHasNotSlept = "&9{0} needs to sleep in a bed before voting!";

    // Player Has Voted
    @Setting(value = "player-has-voted", comment = "\nAppears in all player's action bar when a player votes yes/no.\n" +
            "0 = Player's name, 1 = yes/no")
    private String playerHasVoted = "&9{0} has voted {1}!";

    // Headers
    // [Vote]
    private String voteHeader = "<gray>[<blue>" + vote + "<gray>]<reset> ";

    // -
    private String hyphenHeader = " <blue>-<reset> ";

    public Component noVoteInProg(String voteType) {
        // No vote in progress! [Start Vote] (runs /skipday|night)
        return MiniMessage.get().parse(MessageFormat.format(serialize(noVoteInProg),
                "<click:suggest_command:/skip" + voteType + ">" +
                "<hover:show_text:'" + serialize(clickToStartVote) + "'>" +
                        "<bold>[" + startVote + "]"));
    }

    public Component voteStarted(String name, String voteType) {
        // &7[&9Vote&7] &r%name has started a vote to skip the day|night!
        return MiniMessage.get().parse(voteHeader + MessageFormat.format(serialize(playerStartedVote), name, voteType));
    }

    public Component voteButtons(String voteType) {
        // &9 - &rPlease vote: || &a&l[Yes]||cmd:/skipday|night yes||ttp:&6&lClick &rhere to vote yes.|| &4&l[No]||cmd:/skipday|night no||ttp:&6&lClick &rhere to vote no.
        return MiniMessage.get().parse(hyphenHeader + serialize(pleaseVote) +
                "<green><bold><click:run_command:/skip" + voteType + " yes>" +
                "<hover:show_text:'" + serialize(MessageFormat.format(clickHereToVote, yes)) + "'>" +
                "[" + yes.substring(0, 1).toUpperCase() + yes.substring(1) + "]</hover></click> " +
                "<dark_red><bold><click:run_command:/skip" + voteType + " no>" +
                "<hover:show_text:'" + serialize(MessageFormat.format(clickHereToVote, no)) + "'>" +
                "[" + no.substring(0, 1).toUpperCase() + no.substring(1) + "]");
    }

    public Component youVoteYes() {
        // &9 - &rYou voted &5&lyes&r.
        return MiniMessage.get().parse(hyphenHeader + serialize(MessageFormat.format(youVote, yes)));
    }
    
    public Component youVoteNo() {
        // &9 - &rYou voted &5&lno&r.
        return MiniMessage.get().parse(hyphenHeader + serialize(MessageFormat.format(youVote, no)));
    }

    public Component tenSecondsLeft() {
        // &7[&9Vote&7] &5&l10 &rseconds left to vote!
        return MiniMessage.get().parse(voteHeader + serialize(tenSecondsLeft));
    }

    public Component votePassed(String voteType) {
        // &7[&9Vote&7] &rVote &5&lpassed&r! Skipping the day|night.
        return MiniMessage.get().parse(voteHeader + serialize(MessageFormat.format(votePassed, voteType)));
    }

    public Component voteFailed(String voteType) {
        // &7[&9Vote&7] &rVote &5&lfailed&r! The day|night will not be skipped.
        return MiniMessage.get().parse(voteHeader + serialize(MessageFormat.format(voteFailed, voteType)));
    }

    public Component alreadyVoted() {
        // &9 - &r&cYou have already voted!
        return MiniMessage.get().parse(hyphenHeader + serialize(alreadyVoted));
    }

    public Component mustSleep() {
        // &9 - &r&cYou must sleep in a bed first!
        return MiniMessage.get().parse(hyphenHeader + serialize(mustSleep));
    }

    public Component idle() {
        // &9 - &rYou are &3&lidle&r, your vote will not count.
        return MiniMessage.get().parse(hyphenHeader + serialize(idle));
    }

    public Component away() {
        // &9 - &rYou are &1&laway&r, your vote will not count.
        return MiniMessage.get().parse(hyphenHeader + serialize(away));
    }

    public Component back() {
        // &9 - &rWelcome back.
        return MiniMessage.get().parse(hyphenHeader + serialize(back));
    }

    public Component leftWorld() {
        // &7[&9Vote&7] &rYou left the world, your vote will not count.
        return MiniMessage.get().parse(voteHeader + serialize(leftWorld));
    }

    public Component inBedVotedYes() {
        // &9 - &rYou are now in bed, automatically voting yes.
        return MiniMessage.get().parse(hyphenHeader + serialize(inBedVotedYes));
    }

    public Component inBedNoVoteInProg() {
        // &7[&9Vote&7] &rStart a vote to skip the night? || &5&l[Vote]||cmd:/skipnight||ttp:&6&lClick &rhere to start a vote.
        return MiniMessage.get().parse(voteHeader + MessageFormat.format(serialize(inBedNoVoteInProg),
                        "<click:suggest_command:/skipnight>" +
                        "<hover:show_text:'" + serialize(clickToStartVote) + "'>" +
                        "<bold>[" + startVote + "]"));
    }

    public Component playerHasNotSlept(String name) {
        // &9%name% needs to sleep in a bed before voting!
        return MiniMessage.get().parse(MessageFormat.format(serialize(playerHasNotSlept), name));
    }

    public Component playerHasVotedYes(String name) {
        // &9%name% has voted yes!
        return MiniMessage.get().parse(MessageFormat.format(serialize(playerHasVoted), name, yes));
    }

    public Component playerHasVotedNo(String name) {
        // &9%name% has voted no!
        return MiniMessage.get().parse(MessageFormat.format(serialize(playerHasVoted), name, no));
    }

    public Component noPerm() {
        // &cYou don't have permission to run this!
        return MiniMessage.get().parse(serialize(noPerm));
    }

    public Component wrongWorld() {
        // &cYou must be in the overworld to start a vote!
        return MiniMessage.get().parse(serialize(wrongWorld));
    }

    public Component canOnlyVoteAtNight() {
        // &cYou can only start a vote at night!
        return MiniMessage.get().parse(serialize(canOnlyVoteAtNight));
    }

    public Component canOnlyVoteAtDay() {
        // &cYou can only start a vote during the day!
        return MiniMessage.get().parse(serialize(canOnlyVoteAtDay));
    }

    public Component noVoteWhileIdle() {
        // &cYou cannot start a vote while idle!
        return MiniMessage.get().parse(serialize(noVoteWhileIdle));
    }

    public Component noVoteWhileAway() {
        // &cYou cannot start a vote while away!
        return MiniMessage.get().parse(serialize(noVoteWhileAway));
    }

    public Component voteInProg() {
        // &cVote already in progress!
        return MiniMessage.get().parse(serialize(voteInProg));
    }

    public Component mustSleepNewVote() {
        // &cYou must sleep in a bed first!
        return MiniMessage.get().parse(serialize(mustSleep));
    }

    public String getDayString() {
        return day;
    }

    public String getNightString() {
        return night;
    }
    private String serialize(String string) {
        TextComponent component = LegacyComponentSerializer.legacy(_formatCode).deserialize(string);
        return MiniMessage.get().serialize(component);
    }
}
