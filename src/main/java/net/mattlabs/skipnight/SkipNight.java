package net.mattlabs.skipnight;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SkipNight extends JavaPlugin {

    private Vote vote;
    private Messages messages;

    public void onEnable() {
        vote = new Vote(this);
        messages = new Messages();
        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm and, CRX VrynzX)");
    }

    public void onDisable() {}

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] strings) {
        Player player = (Player) sender;

        if (strings.length == 0) {
            vote.start(player, messages);
        }
        else if (strings[0].equalsIgnoreCase("yes")) vote.addYes(player);
        else if (strings[0].equalsIgnoreCase("no")) vote.addNo(player);
        else player.sendMessage(ChatColor.RED + "Invalid usage: /skipnight [yes/no]");

        return true;
    }
}
