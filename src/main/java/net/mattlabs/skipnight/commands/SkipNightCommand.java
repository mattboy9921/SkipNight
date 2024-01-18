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

public class SkipNightCommand {

    PaperCommandManager<CommandSender> commandManager;
    Vote vote;

    public SkipNightCommand(PaperCommandManager<CommandSender> commandManager, SkipNight plugin) {
        vote = plugin.vote;
        this.commandManager = commandManager;
        commands();
    }

    // Register each command
    private void commands() {
        // Set up builder with permissions
        Command.Builder<CommandSender> builder = commandManager.commandBuilder("skipnight", "sn")
                        .senderType(Player.class)
                        .permission("skipnight.vote.night");

        // Base Command
        commandManager.command(builder
                .meta(CommandMeta.DESCRIPTION, "Starts a vote to skip the night.")
                .handler(this::baseCommand)
        );

        // Yes
        commandManager.command(builder
                .literal("yes", "y")
                .meta(CommandMeta.DESCRIPTION, "Votes yes for the current vote.")
                .handler(this::yes)
        );

        // No
        commandManager.command(builder
                .literal("no", "n")
                .meta(CommandMeta.DESCRIPTION, "Votes no for the current vote.")
                .handler(this::no)
        );
    }

    private void baseCommand(CommandContext<CommandSender> context) {
        this.vote.start((Player) context.getSender(), VoteType.NIGHT);
    }

    private void yes(CommandContext<CommandSender> context) {
        this.vote.addYes((Player) context.getSender(), VoteType.NIGHT);
    }

    private void no(CommandContext<CommandSender> context) {
        this.vote.addNo((Player) context.getSender(), VoteType.NIGHT);
    }
}
