package net.mattlabs.skipnight.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.annotation.*;
import net.mattlabs.skipnight.SkipNight;
import net.mattlabs.skipnight.Vote;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

@CommandAlias("skipnight|sn")
@CommandPermission("skipnight.vote.night")
public class SkipNightCommand extends BaseCommand {

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
        Command.Builder<CommandSender> skipNightBuilder = commandManager.commandBuilder("skipnight", "sn")
                        .senderType(Player.class)
                        .permission(sender -> sender.hasPermission("skipnight.vote.night"));

        // Base Command
        commandManager.command(skipNightBuilder
                .meta(CommandMeta.DESCRIPTION, "Starts a vote to skip the night.")
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
        this.vote.start((Player) context.getSender(), VoteType.NIGHT);
    }

    private void yes(CommandContext<CommandSender> context) {
        this.vote.addYes((Player) context.getSender(), VoteType.NIGHT);
    }

    private void no(CommandContext<CommandSender> context) {
        this.vote.addNo((Player) context.getSender(), VoteType.NIGHT);
    }

    @Default
    @Description("Starts a vote to skip the night.")
    public void onSkipNight(CommandSender sender) {
        if (!(sender instanceof Player)) {
            getLogger().info("Vote can't be started from console.");
        }
        else {
            Player player = (Player) sender;
            this.vote.start(player, VoteType.NIGHT);
        }
    }

    @Subcommand("yes")
    @Description("Votes yes for current vote.")
    public void onYes(CommandSender sender) {
        if (!(sender instanceof Player)) {
            getLogger().info("Vote not allowed from console.");
        }
        else {
            Player player = (Player) sender;
            this.vote.addYes(player, VoteType.NIGHT);
        }
    }

    @Subcommand("no")
    @Description("Votes no for current vote.")
    public void onNo(CommandSender sender) {
        if (!(sender instanceof Player)) {
            getLogger().info("Vote not allowed from console.");
        }
        else {
            Player player = (Player) sender;
            this.vote.addNo(player, VoteType.NIGHT);
        }
    }
}
