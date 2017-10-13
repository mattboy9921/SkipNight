package net.mattlabs.skipnight;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import javax.xml.soap.Text;

public class Messages {

    public Messages() {

    }

    public TextComponent noVoteInProg() {
        // No vote in progress! [Start Vote] (runs /skipnight)
        TextComponent warn = new TextComponent("No vote in progress! ");
        warn.setColor(net.md_5.bungee.api.ChatColor.RED);

        TextComponent button = new TextComponent("[Start Vote]");
        button.setColor(net.md_5.bungee.api.ChatColor.BLUE);
        button.setBold(true);
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click here to start a vote").create()));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skipnight"));

        warn.addExtra(button);

        return warn;
    }

    public TextComponent voteStarted() {
        //&7[&9Vote&7] &rA vote to skip the night has started!
        TextComponent openBracket = new TextComponent("[");
        openBracket.setColor(ChatColor.GRAY);

        TextComponent vote = new TextComponent("Vote");
        vote.setColor(ChatColor.BLUE);

        TextComponent closeBracket = new TextComponent("] ");
        closeBracket.setColor(ChatColor.GRAY);

        TextComponent voteStarted = new TextComponent("A vote to skip the night has started!");
        voteStarted.setColor(ChatColor.WHITE);

        openBracket.addExtra(vote);
        openBracket.addExtra(closeBracket);
        openBracket.addExtra(voteStarted);

        return openBracket;
    }

    public TextComponent voteButtons() {
        //&9 - &rPlease vote: || &a&l[Yes]||cmd:/skipnight yes||ttp:&6&lClick &rhere to vote yes.|| &4&l[No]||cmd:/skipnight no||ttp:&6&lClick &rhere to vote no.
        TextComponent hyphen = new TextComponent(" - ");
        hyphen.setColor(ChatColor.BLUE);

        TextComponent pleaseVote = new TextComponent("Please vote: ");
        pleaseVote.setColor(ChatColor.GRAY);

        TextComponent yes = new TextComponent("[Yes]");
        yes.setColor(ChatColor.GREEN);
        yes.setBold(true);
        yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click").color(ChatColor.BLUE).bold(true)
                        .append(" here to vote yes").create()));
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skipnight yes"));

        TextComponent no = new TextComponent(" [No]");
        no.setColor(ChatColor.DARK_RED);
        no.setBold(true);
        no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click").color(ChatColor.BLUE).bold(true)
                        .append(" here to vote no").create()));
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skipnight no"));

        hyphen.addExtra(pleaseVote);
        hyphen.addExtra(yes);
        hyphen.addExtra(no);

        return hyphen;
    }

    public TextComponent youVoteYes() {
        // &9 - &rYou voted &5&lyes&r.
        TextComponent hyphen = new TextComponent(" - ");
        hyphen.setColor(ChatColor.BLUE);

        TextComponent youVoted = new TextComponent("You voted ");
        youVoted.setColor(ChatColor.WHITE);

        TextComponent yes = new TextComponent("yes");
        yes.setColor(ChatColor.BLUE);
        yes.setBold(true);

        TextComponent period = new TextComponent(".");
        period.setColor(ChatColor.WHITE);

        hyphen.addExtra(youVoted);
        hyphen.addExtra(yes);
        hyphen.addExtra(period);

        return hyphen;
    }

    public TextComponent youVoteNo() {
        // &9 - &rYou voted &5&lno&r.
        TextComponent hyphen = new TextComponent(" - ");
        hyphen.setColor(ChatColor.BLUE);

        TextComponent youVoted = new TextComponent("You voted ");
        youVoted.setColor(ChatColor.WHITE);

        TextComponent no = new TextComponent("no");
        no.setColor(ChatColor.BLUE);
        no.setBold(true);

        TextComponent period = new TextComponent(".");
        period.setColor(ChatColor.WHITE);

        hyphen.addExtra(youVoted);
        hyphen.addExtra(no);
        hyphen.addExtra(period);

        return hyphen;
    }

    public TextComponent tenSecondsLeft() {
        // &7[&9Vote&7] &5&l10 &rseconds left to vote!
        TextComponent openBracket = new TextComponent("[");
        openBracket.setColor(ChatColor.GRAY);

        TextComponent vote = new TextComponent("Vote");
        vote.setColor(ChatColor.BLUE);

        TextComponent closeBracket = new TextComponent("] ");
        closeBracket.setColor(ChatColor.GRAY);

        TextComponent ten = new TextComponent("10");
        ten.setColor(ChatColor.BLUE);
        ten.setBold(true);

        TextComponent seconds = new TextComponent(" seconds left to vote!");
        seconds.setColor(ChatColor.WHITE);

        openBracket.addExtra(vote);
        openBracket.addExtra(closeBracket);
        openBracket.addExtra(ten);
        openBracket.addExtra(seconds);

        return openBracket;
    }

    public TextComponent votePassed() {
        // &7[&9Vote&7] &rVote &5&lpassed&r! Skipping the night.
        TextComponent openBracket = new TextComponent("[");
        openBracket.setColor(ChatColor.GRAY);

        TextComponent vote = new TextComponent("Vote");
        vote.setColor(ChatColor.BLUE);

        TextComponent closeBracket = new TextComponent("] ");
        closeBracket.setColor(ChatColor.GRAY);

        TextComponent vote2 = new TextComponent("Vote ");
        vote2.setColor(ChatColor.WHITE);

        TextComponent passed = new TextComponent("passed");
        passed.setColor(ChatColor.BLUE);
        passed.setBold(true);

        TextComponent skip = new TextComponent("! Skipping the night.");
        skip.setColor(ChatColor.WHITE);

        openBracket.addExtra(vote);
        openBracket.addExtra(closeBracket);
        openBracket.addExtra(vote2);
        openBracket.addExtra(passed);
        openBracket.addExtra(skip);

        return openBracket;
    }

    public TextComponent voteFailed() {
        // &7[&9Vote&7] &rVote &5&lfailed&r! The night will not be skipped.
        TextComponent openBracket = new TextComponent("[");
        openBracket.setColor(ChatColor.GRAY);

        TextComponent vote = new TextComponent("Vote");
        vote.setColor(ChatColor.BLUE);

        TextComponent closeBracket = new TextComponent("] ");
        closeBracket.setColor(ChatColor.GRAY);

        TextComponent vote2 = new TextComponent("Vote ");
        vote2.setColor(ChatColor.WHITE);

        TextComponent failed = new TextComponent("failed");
        failed.setColor(ChatColor.BLUE);
        failed.setBold(true);

        TextComponent noSkip = new TextComponent("! The night will not be skipped.");
        noSkip.setColor(ChatColor.WHITE);

        openBracket.addExtra(vote);
        openBracket.addExtra(closeBracket);
        openBracket.addExtra(vote2);
        openBracket.addExtra(failed);
        openBracket.addExtra(noSkip);

        return openBracket;
    }

    public TextComponent alreadyVoted() {
        // &9 - &r&cYou have already voted!
        TextComponent hyphen = new TextComponent(" - ");
        hyphen.setColor(ChatColor.BLUE);

        TextComponent youVoted = new TextComponent("You have already voted!");
        youVoted.setColor(ChatColor.RED);

        hyphen.addExtra(youVoted);

        return hyphen;
    }
}
