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
public class SkipNightCommand extends BaseCommand {

    Vote vote;

    public SkipNightCommand(SkipNight plugin) {
        vote = plugin.vote;
        plugin.manager.getCommandCompletions().registerStaticCompletion("skipnightOptions",
                Lists.newArrayList("yes", "no"));
    }

    @Default
    @Description("Starts a vote to skip the night.")
    @CommandPermission("skipnight.vote")
    @CommandCompletion("@skipnightOptions")
    public void onDefault(CommandSender sender, @Optional@Values("@skipnightOptions") String vote) {
        if (!(sender instanceof Player)) {
            getLogger().info("Vote can't be started from console.");
        }
        else {
            Player player = (Player) sender;
            if (vote == null) this.vote.start(player);
            else if (vote.equalsIgnoreCase("yes")) this.vote.addYes(player.getUniqueId());
            else if (vote.equalsIgnoreCase("no")) this.vote.addNo(player.getUniqueId());
        }
    }
}
