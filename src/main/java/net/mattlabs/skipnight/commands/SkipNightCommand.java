package net.mattlabs.skipnight.commands;

import net.mattlabs.skipnight.SkipNight;
import net.mattlabs.skipnight.Vote;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

public class SkipNightCommand implements CommandExecutor {

    Vote vote = SkipNight.getInstance().getVote();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            getLogger().info("Vote can't be started from console.");
            return true;
        }
        Player player = (Player) commandSender;

        if (strings.length == 0) {
            vote.start(player);
        }
        else if (strings[0].equalsIgnoreCase("yes")) vote.addYes(player.getUniqueId());
        else if (strings[0].equalsIgnoreCase("no")) vote.addNo(player.getUniqueId());
        else player.sendMessage(ChatColor.RED + "Invalid usage: /skipnight [yes/no]");

        return true;
    }
}
