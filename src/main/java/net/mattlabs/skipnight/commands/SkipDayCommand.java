package net.mattlabs.skipnight.commands;

import net.mattlabs.skipnight.SkipNight;
import net.mattlabs.skipnight.Vote;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class SkipDayCommand {

    LegacyPaperCommandManager<CommandSender> commandManager;
    Vote vote;

    public SkipDayCommand(LegacyPaperCommandManager<CommandSender> commandManager, SkipNight plugin) {
        vote = plugin.vote;
        this.commandManager = commandManager;
        commands();
    }

    // Register each command
    private void commands() {
        // Set up builder with permissions
        Command.Builder<CommandSender> builder = commandManager.commandBuilder("skipday", "sd")
                .permission("skipnight.vote.day")
                .senderType(Player.class);

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

    private void baseCommand(CommandContext<CommandSender> context) {
        this.vote.start((Player) context.sender(), VoteType.DAY);
    }

    private void yes(CommandContext<CommandSender> context) {
        this.vote.addYes((Player) context.sender(), VoteType.DAY);
    }

    private void no(CommandContext<CommandSender> context) {
        this.vote.addNo((Player) context.sender(), VoteType.DAY);
    }
}
