package net.mattlabs.skipnight.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.mattlabs.skipnight.SkipNight;
import net.mattlabs.skipnight.Vote;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

@CommandAlias("skipday|sd")
@CommandPermission("skipnight.vote.day")
public class SkipDayCommand extends BaseCommand {

    Vote vote;

    public SkipDayCommand(SkipNight plugin) {
        vote = plugin.vote;
    }

    @Default
    @Description("Starts a vote to skip the day.")
    public void onSkipNight(CommandSender sender) {
        if (!(sender instanceof Player)) {
            getLogger().info("Vote can't be started from console.");
        }
        else {
            Player player = (Player) sender;
            this.vote.start(player, VoteType.DAY);
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
            this.vote.addYes(player.getUniqueId(), VoteType.DAY);
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
            this.vote.addNo(player.getUniqueId(), VoteType.DAY);
        }
    }
}
