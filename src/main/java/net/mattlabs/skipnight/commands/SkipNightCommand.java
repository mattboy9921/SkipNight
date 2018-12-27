package net.mattlabs.skipnight.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.Lists;
import net.mattlabs.skipnight.SkipNight;
import net.mattlabs.skipnight.Vote;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

@CommandAlias("skipnight|sn")
@CommandPermission("skipnight.vote")
public class SkipNightCommand extends BaseCommand {

    Vote vote;

    public SkipNightCommand(SkipNight plugin) {
        vote = plugin.vote;
    }

    @Default
    @Description("Starts a vote to skip the night.")
    public void onSkipNight(CommandSender sender) {
        if (!(sender instanceof Player)) {
            getLogger().info("Vote can't be started from console.");
        }
        else {
            Player player = (Player) sender;
            this.vote.start(player);
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
            this.vote.addYes(player.getUniqueId());
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
            this.vote.addNo(player.getUniqueId());
        }
    }
}
