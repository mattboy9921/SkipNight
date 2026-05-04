package net.mattlabs.skipnight.impl.paper_1_20_6.commands;

import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.api.util.VoteType;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

public class Paper1206SkipDayCommand {

    PaperCommandManager<Source> commandManager;
    Vote vote;

    public Paper1206SkipDayCommand(PaperCommandManager<Source> commandManager, Vote vote) {
        this.vote = vote;
        this.commandManager = commandManager;
        commands();
    }

    // Register each command
    private void commands() {
        // Set up builder with permissions
        Command.Builder<Source> builder = commandManager.commandBuilder("skipday", "sd")
                .permission("skipnight.vote.day")
                .senderType(PlayerSource.class);

        // Base Command
        commandManager.command(builder
                .commandDescription(CommandDescription.commandDescription("Starts a vote to skip the day."))
                .handler(this::baseCommand)
        );

        // Yes
        commandManager.command(builder
                .literal("yes", "y")
                .commandDescription(CommandDescription.commandDescription("Votes yes for the current vote."))
                .handler(this::yes)
        );

        // No
        commandManager.command(builder
                .literal("no", "n")
                .commandDescription(CommandDescription.commandDescription("Votes no for the current vote."))
                .handler(this::no)
        );
    }

    private void baseCommand(CommandContext<Source> context) {
        Player player = (Player) context.sender().source();
        this.vote.start(player.getUniqueId(), VoteType.DAY);
    }

    private void yes(CommandContext<Source> context) {
        Player player = (Player) context.sender().source();
        this.vote.addYes(player.getUniqueId(), VoteType.DAY);
    }

    private void no(CommandContext<Source> context) {
        Player player = (Player) context.sender().source();
        this.vote.addNo(player.getUniqueId(), VoteType.DAY);
    }
}
