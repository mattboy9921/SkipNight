package net.mattlabs.skipnight;

import mkremins.fanciful.FancyMessage;

import static org.bukkit.ChatColor.*;

public class Messages {

    private Messages() {

    }

    public static FancyMessage noVoteInProg() {
        // No vote in progress! [Start Vote] (runs /skipnight)
        return new FancyMessage("No vote in progress! ")
                    .color(RED)
                .then("[Start Vote]")
                    .color(BLUE)
                    .style(BOLD)
                    .command("/skipnight")
                    .tooltip("Click here to start a vote");
    }

    public static FancyMessage voteStarted() {
        //&7[&9Vote&7] &rA vote to skip the night has started!
        return new FancyMessage("[")
                    .color(GRAY)
                .then("Vote")
                    .color(BLUE)
                .then("] ")
                    .color(GRAY)
                .then("A vote to skip the night has started!")
                    .color(WHITE);
    }

    public static FancyMessage voteButtons() {
        //&9 - &rPlease vote: || &a&l[Yes]||cmd:/skipnight yes||ttp:&6&lClick &rhere to vote yes.|| &4&l[No]||cmd:/skipnight no||ttp:&6&lClick &rhere to vote no.
        return new FancyMessage(" - ")
                    .color(BLUE)
                .then("Please vote: ")
                    .color(WHITE)
                .then("[Yes]")
                    .color(GREEN)
                    .style(BOLD)
                    .command("/skipnight yes")
                    .tooltip("Click here to vote yes")
                .then(" [No]")
                    .color(DARK_RED)
                    .style(BOLD)
                    .command("/skipnight no")
                    .tooltip("Click here to vote no");
    }

    public static FancyMessage youVoteYes() {
        // &9 - &rYou voted &5&lyes&r.
        return new FancyMessage(" - ")
                    .color(BLUE)
                .then("You voted ")
                    .color(WHITE)
                .then("yes")
                    .color(BLUE)
                    .style(BOLD)
                .then(".")
                    .color(WHITE);
    }

    public static FancyMessage youVoteNo() {
        // &9 - &rYou voted &5&lno&r.
        return new FancyMessage(" - ")
                    .color(BLUE)
                .then("You voted ")
                    .color(WHITE)
                .then("no")
                    .color(BLUE)
                    .style(BOLD)
                .then(".")
                    .color(WHITE);
    }

    public static FancyMessage tenSecondsLeft() {
        // &7[&9Vote&7] &5&l10 &rseconds left to vote!
        return new FancyMessage("[")
                    .color(GRAY)
                .then("Vote")
                    .color(BLUE)
                .then("]")
                    .color(GRAY)
                .then(" 10")
                    .color(BLUE)
                    .style(BOLD)
                .then(" seconds left to vote!")
                    .color(WHITE);
    }

    public static FancyMessage votePassed() {
        // &7[&9Vote&7] &rVote &5&lpassed&r! Skipping the night.
        return new FancyMessage("[")
                    .color(GRAY)
                .then("Vote")
                    .color(BLUE)
                .then("] ")
                    .color(GRAY)
                .then("Vote ")
                    .color(WHITE)
                .then("passed")
                    .color(BLUE)
                    .style(BOLD)
                .then("! Skipping the night.")
                    .color(WHITE);
    }

    public static FancyMessage voteFailed() {
        // &7[&9Vote&7] &rVote &5&lfailed&r! The night will not be skipped.
        return new FancyMessage("[")
                    .color(GRAY)
                .then("Vote")
                    .color(BLUE)
                .then("]")
                    .color(GRAY)
                .then(" Vote ")
                    .color(WHITE)
                .then("failed")
                    .color(BLUE)
                    .style(BOLD)
                .then("! The night will not be skipped.")
                    .color(WHITE);
    }

    public static FancyMessage alreadyVoted() {
        // &9 - &r&cYou have already voted!
        return new FancyMessage(" - ")
                    .color(BLUE)
                .then("You have already voted!")
                    .color(RED);
    }
}
