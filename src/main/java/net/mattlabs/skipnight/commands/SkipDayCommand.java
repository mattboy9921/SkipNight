package net.mattlabs.skipnight.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import net.mattlabs.skipnight.SkipNight;
import net.mattlabs.skipnight.Vote;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipDayCommand {

    PaperCommandManager<CommandSender> commandManager;
    Vote vote;

    public SkipDayCommand(PaperCommandManager<CommandSender> commandManager, SkipNight plugin) {
        vote = plugin.vote;
        this.commandManager = commandManager;
        commands();
    }

    // Register each command
    private void commands() {
        // Set up builder with permissions
        Command.Builder<CommandSender> skipNightBuilder = commandManager.commandBuilder("skipday", "sd")
                .senderType(Player.class)
                .permission(sender -> sender.hasPermission("skipnight.vote.day"));

        // Base Command
        commandManager.command(skipNightBuilder
                .meta(CommandMeta.DESCRIPTION, "Starts a vote to skip the day.")
                .handler(context -> commandManager.taskRecipe().begin(context).asynchronous(this::baseCommand).execute())
        );

        // Yes
        commandManager.command(skipNightBuilder
                .literal("yes", "y")
                .meta(CommandMeta.DESCRIPTION, "Votes yes for the current vote.")
                .handler(context -> commandManager.taskRecipe().begin(context).asynchronous(this::yes).execute())
        );

        // No
        commandManager.command(skipNightBuilder
                .literal("no", "n")
                .meta(CommandMeta.DESCRIPTION, "Votes no for the current vote.")
                .handler(context -> commandManager.taskRecipe().begin(context).asynchronous(this::no).execute())
        );
    }

    private void baseCommand(CommandContext<CommandSender> context) {
        this.vote.start((Player) context.getSender(), VoteType.DAY);
    }

    private void yes(CommandContext<CommandSender> context) {
        this.vote.addYes((Player) context.getSender(), VoteType.DAY);
    }

    private void no(CommandContext<CommandSender> context) {
        this.vote.addNo((Player) context.getSender(), VoteType.DAY);
    }
}
