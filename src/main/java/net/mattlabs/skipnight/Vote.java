package net.mattlabs.skipnight;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Vote implements Runnable, Listener {

    private enum Timer {
        Init,
        Operation,
        Interrupt,
        Final,
        Complete
    }

    private Timer timer;
    private Messages messages;
    private int yes, no, playerCount, countDown;
    private BossBar bar;
    private Plugin plugin;
    private List voters;
    private Player player;
    private World world;
    private TextComponent messageArray[] = new TextComponent[2];

    public Vote(Plugin plugin) {
        timer = Timer.Complete;
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogoff(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (timer != Timer.Complete) // vote is running
            if (player.hasPermission("skipnight.vote")) { // player has permission
                Voter voter = new Voter(player);
                if (voters.contains(voter)) { // player is in voter list
                    voter = (Voter) voters.get(voters.lastIndexOf(voter));
                    if (voter.getVote() == 1) yes--;
                    if (voter.getVote() == -1) no--;
                    voters.remove(voter);
                }
            }
    }

    public void run() {
        switch (timer) {
            case Init:
                doInit();
                break;
            case Operation:
                doOperation();
                break;
            case Interrupt:
                doInterrupt();
                break;
            case Final:
                doFinal();
                break;
            case Complete:
                doComplete();
                break;
        }
    }

    private void doInit() {
        voters = new ArrayList();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        messageArray[0] = messages.voteStarted();
        messageArray[1] = messages.voteButtons();

        yes = 1;
        no = 0;
        countDown = 30;

        bar = Bukkit.createBossBar("Current Vote: "
                + ChatColor.GREEN + ChatColor.BOLD + "Yes "
                + ChatColor.RESET + "- " + yes
                + ChatColor.DARK_RED + ChatColor.BOLD +  " No "
                + ChatColor.RESET + "- " + no, BarColor.PURPLE, BarStyle.SOLID);

        voters = updateAll(voters, messageArray, player);

        timer = Timer.Operation;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doOperation() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.Interrupt;
        bar.setProgress((double) countDown / 30.0);
        bar.setTitle("Current Vote: "
                + ChatColor.GREEN + ChatColor.BOLD + "Yes "
                + ChatColor.RESET + "- " + yes
                + ChatColor.DARK_RED + ChatColor.BOLD +  " No "
                + ChatColor.RESET + "- " + no);
        voters = updateAll(voters);
        if (countDown == 10) timer = Timer.Final;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    public void doInterrupt() {
        countDown = 0;
        bar.setProgress(1.0);
        bar.setTitle(ChatColor.YELLOW + "All players have voted!");
        bar.setColor(BarColor.YELLOW);

        timer = Timer.Complete;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doFinal() {
        countDown--;
        bar.setProgress((double) countDown / 30.0);
        bar.setTitle("Current Vote: "
                + ChatColor.GREEN + ChatColor.BOLD + "Yes "
                + ChatColor.RESET + "- " + yes
                + ChatColor.DARK_RED + ChatColor.BOLD +  " No "
                + ChatColor.RESET + "- " + no);
        if (countDown == 9) voters = updateAll(voters, messages.tenSecondsLeft());
        else voters = updateAll(voters);

        if (countDown % 2 == 1) bar.setColor(BarColor.WHITE);
        else bar.setColor(BarColor.PURPLE);

        if (countDown == 0) timer = Timer.Complete;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doComplete() {
        countDown--;
        if (countDown == -1) {
            bar.setProgress(1.0);
            if (yes > no) {
                bar.setTitle(ChatColor.GREEN + "Vote passed!");
                bar.setColor(BarColor.GREEN);
                updateAll(voters, messages.votePassed());
                world.setTime(0);
                if (world.hasStorm()) world.setStorm(false);
            }
            else {
                bar.setTitle(ChatColor.DARK_RED + "Vote failed!");
                bar.setColor(BarColor.RED);
                updateAll(voters, messages.voteFailed());
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
        }

        if (countDown == -2) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);

        if (countDown == -3) {
            HandlerList.unregisterAll(this);
            bar.removeAll();
            bar = null;
            voters = null;
        }
    }

    public void addYes(Player player) {
        if (timer != Timer.Complete) {
            Voter voter = new Voter(player);
            if (voters.contains(voter)) {
                voter = (Voter) voters.get(voters.lastIndexOf(voter));
                if (voter.getVote() == 0) {
                    yes++;
                    voter.voteYes();
                    player.spigot().sendMessage(messages.youVoteYes());
                }
                else player.spigot().sendMessage(messages.alreadyVoted());
            }
        }
        else player.spigot().sendMessage(messages.noVoteInProg());
    }

    public void addNo(Player player) {
        if (timer != Timer.Complete) {
            Voter voter = new Voter(player);
            if (voters.contains(voter)) {
                voter = (Voter) voters.get(voters.lastIndexOf(voter));
                if (voter.getVote() == 0) {
                    no++;
                    voter.voteNo();
                    player.spigot().sendMessage(messages.youVoteNo());
                }
                else player.spigot().sendMessage(messages.alreadyVoted());
            }
        }
        else player.spigot().sendMessage(messages.noVoteInProg());
    }

    // Attempts to start a vote if all conditions are met, otherwise informs player why vote can't start
    public void start(Player player, Messages messages) {
        if (!player.hasPermission("skipnight.vote")) // If player doesn't have permission
            player.sendMessage(ChatColor.RED + "You don't have permission to run this!");
        else if (!isInOverworld(player)) // If player isn't in the overworld
            player.sendMessage(ChatColor.RED + "You must be in the overworld to start a vote!");
        else if (player.getWorld().getTime() < 14000) // If it's day
            player.sendMessage(ChatColor.RED + "You can only start a vote at night!");
        else if (!(timer == Timer.Complete)) // If there's a vote happening
            player.sendMessage(ChatColor.RED + "Vote already in progress!");
        else {
            this.messages = messages;
            timer = Timer.Init;
            this.player = player;
            world = player.getWorld();
            player.spigot().sendMessage(messages.voteStarted());
            player.spigot().sendMessage(messages.youVoteYes());
            run();
        }
    }

    // Checks whether player is in overworld
    private boolean isInOverworld(Player player) {
        return player.getWorld().getEnvironment().equals(World.Environment.NORMAL);
    }

    private List updateAll(List voters) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player);
            if (isInOverworld(player) && player.hasPermission("skipnight.vote")) {
                if (!voters.contains(voter)) {
                    for (int i = 0; i < messageArray.length; i++) player.spigot().sendMessage(messageArray[i]);
                    voters.add(voter);
                    bar.addPlayer(player);
                }
            } else {
                if (voters.contains(voter)) {
                    voter = (Voter) voters.get(voters.lastIndexOf(voter));
                    if (voter.getVote() == 1) yes--;
                    if (voter.getVote() == -1) no--;
                    voters.remove(voter);
                }
                bar.removePlayer(player);
            }
        }
        playerCount = voters.size();
        return voters;
    }

    private List updateAll(List voters, TextComponent message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player);
            if (isInOverworld(player) && player.hasPermission("skipnight.vote")) {
                if (!voters.contains(voter)) {
                    for (int i = 0; i < messageArray.length; i++) player.spigot().sendMessage(messageArray[i]);
                    voters.add(voter);
                    bar.addPlayer(player);
                }
                player.spigot().sendMessage(message);
            } else {
                if (voters.contains(voter)) {
                    voter = (Voter) voters.get(voters.lastIndexOf(voter));
                    if (voter.getVote() == 1) yes--;
                    if (voter.getVote() == -1) no--;
                    voters.remove(voter);
                }
                bar.removePlayer(player);
            }
        }
        playerCount = voters.size();
        return voters;
    }

    private List updateAll(List voters, TextComponent[] messageArray, Player sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player);
            if (isInOverworld(player) && player.hasPermission("skipnight.vote")) {
                if (player != sender)
                    for (int i = 0; i < messageArray.length; i++) player.spigot().sendMessage(messageArray[i]);
                if (!voters.contains(voter)) {
                    voters.add(voter);
                    if (player == sender) voter.voteYes();
                    bar.addPlayer(player);
                }
            } else {
                if (voters.contains(voter)) {
                    voter = (Voter) voters.get(voters.lastIndexOf(voter));
                    if (voter.getVote() == 1) yes--;
                    if (voter.getVote() == -1) no--;
                    voters.remove(voter);
                }
                bar.removePlayer(player);
            }
        }
        playerCount = voters.size();
        return voters;
    }
}
