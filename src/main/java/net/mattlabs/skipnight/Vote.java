package net.mattlabs.skipnight;

import net.mattlabs.skipnight.util.FastForward;
import net.mattlabs.skipnight.util.VoteType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vote implements Runnable, Listener {

    private enum Timer {
        INIT,
        OPERATION,
        INTERRUPT,
        FINAL,
        COMPLETE
    }

    private Timer timer;
    private VoteType voteType;
    private int yes, no, playerCount, countDown, away, idle;
    private BossBar bar;
    private Plugin plugin;
    private List<Voter> voters;
    private List<Voter> awayVoters;
    private List<Voter> idleVoters;
    private Player player;
    private World world;
    private FastForward fastForward;

    Vote(Plugin plugin) {
        timer = Timer.COMPLETE;
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogoff(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (timer != Timer.COMPLETE) // vote is running
            if (player.hasPermission("skipnight.vote")) { // player has permission
                Voter voter = new Voter(player.getUniqueId());
                if (voters.contains(voter)) { // player is in voter list
                    voter = voters.get(voters.lastIndexOf(voter));
                    if (voter.getVote() == 1) yes--;
                    if (voter.getVote() == -1) no--;
                    voters.remove(voter);
                }
            }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if (timer != Timer.COMPLETE && voteType == VoteType.NIGHT) { // vote is running at night
            if (player.hasPermission("skipnight.vote")) { // player has permission
                Voter voter = new Voter(player.getUniqueId());
                if (!voters.contains(voter)) { // player is not in voter list
                    voters.add(voter);
                    voter.voteYes();
                    yes++;
                    player.spigot().sendMessage(Messages.inBedVotedYes());
                }
            }
        }
        else {
            if (player.hasPermission("skipnight.vote")) { // player has permission
                if (player.getWorld().getPlayerCount() > 1) // if player isn't only one in the world
                    player.spigot().sendMessage(Messages.inBedNoVoteInProg());
            }
        }
    }

    public void run() {
        switch (timer) {
            case INIT:
                doInit();
                break;
            case OPERATION:
                doOperation();
                break;
            case INTERRUPT:
                doInterrupt();
                break;
            case FINAL:
                doFinal();
                break;
            case COMPLETE:
                doComplete();
                break;
        }
    }

    private void doInit() {
        voters = new ArrayList<>();
        awayVoters = new ArrayList<>();
        idleVoters = new ArrayList<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        yes = 1;
        no = 0;
        countDown = 30;
        away = 0;
        idle = 0;

        bar = Bukkit.createBossBar("Current Vote: "
                + ChatColor.GREEN + ChatColor.BOLD + "Yes "
                + ChatColor.RESET + "- " + yes
                + ChatColor.DARK_RED + ChatColor.BOLD +  " No "
                + ChatColor.RESET + "- " + no
                + ChatColor.DARK_AQUA + ChatColor.BOLD + " Idle "
                + ChatColor.RESET + "- " + idle
                + ChatColor.BLUE +  ChatColor.BOLD + " Away "
                + ChatColor.RESET + "- " + away, BarColor.PURPLE, BarStyle.SOLID);

        voters = updateAll(voters, player);

        timer = Timer.OPERATION;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doOperation() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.INTERRUPT;
        bar.setProgress((double) countDown / 30.0);
        bar.setTitle("Current Vote: "
                + ChatColor.GREEN + ChatColor.BOLD + "Yes "
                + ChatColor.RESET + "- " + yes
                + ChatColor.DARK_RED + ChatColor.BOLD +  " No "
                + ChatColor.RESET + "- " + no
                + ChatColor.DARK_AQUA + ChatColor.BOLD + " Idle "
                + ChatColor.RESET + "- " + idle
                + ChatColor.BLUE +  ChatColor.BOLD + " Away "
                + ChatColor.RESET + "- " + away);
        voters = updateAll(voters);
        if (countDown == 10) timer = Timer.FINAL;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doInterrupt() {
        countDown = 0;
        bar.setProgress(1.0);
        bar.setTitle(ChatColor.YELLOW + "All players have voted!");
        bar.setColor(BarColor.YELLOW);

        timer = Timer.COMPLETE;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doFinal() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.INTERRUPT;
        bar.setProgress((double) countDown / 30.0);
        bar.setTitle("Current Vote: "
                + ChatColor.GREEN + ChatColor.BOLD + "Yes "
                + ChatColor.RESET + "- " + yes
                + ChatColor.DARK_RED + ChatColor.BOLD +  " No "
                + ChatColor.RESET + "- " + no
                + ChatColor.DARK_AQUA + ChatColor.BOLD + " Idle "
                + ChatColor.RESET + "- " + idle
                + ChatColor.BLUE +  ChatColor.BOLD + " Away "
                + ChatColor.RESET + "- " + away);
        if (countDown == 9) voters = updateAll(voters, Messages.tenSecondsLeft());
        else voters = updateAll(voters);

        if (countDown % 2 == 1) bar.setColor(BarColor.WHITE);
        else bar.setColor(BarColor.PURPLE);

        if (countDown == 0) timer = Timer.COMPLETE;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doComplete() {
        countDown--;
        if (countDown == -1) {
            bar.setProgress(1.0);
            if (yes > no) {
                bar.setTitle(ChatColor.GREEN + "Vote passed!");
                bar.setColor(BarColor.GREEN);
                updateAll(voters, Messages.votePassed(voteTypeString()));
                fastForward = new FastForward(world, plugin, voteType);
                plugin.getServer().getScheduler().runTaskLater(plugin, fastForward, 10);
                if (world.hasStorm()) world.setStorm(false);
            }
            else {
                bar.setTitle(ChatColor.DARK_RED + "Vote failed!");
                bar.setColor(BarColor.RED);
                updateAll(voters, Messages.voteFailed(voteTypeString()));
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
        }

        if (countDown == -2) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);

        if (countDown == -3) {
            HandlerList.unregisterAll(this);
            bar.removeAll();
            bar = null;
            voters = null;
            fastForward = null;
        }
    }

    public void addYes(UUID uuid) {
        if (timer != Timer.COMPLETE) {
            Voter voter = new Voter(uuid);
            if (voters.contains(voter)) {
                voter = voters.get(voters.lastIndexOf(voter));
                if (voter.getVote() == 0) {
                    if (voteType == VoteType.NIGHT && Bukkit.getPlayer(uuid).getStatistic(Statistic.TIME_SINCE_REST) > 72000)
                        Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.mustSleep());
                    else {
                        yes++;
                        voter.voteYes();
                        Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.youVoteYes());
                    }
                }
                else Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.alreadyVoted());
            }
        }
        else Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.noVoteInProg(voteTypeString()));
    }

    public void addNo(UUID uuid) {
        if (timer != Timer.COMPLETE) {
            Voter voter = new Voter(uuid);
            if (voters.contains(voter)) {
                voter = voters.get(voters.lastIndexOf(voter));
                if (voter.getVote() == 0) {
                    if (voteType == VoteType.NIGHT && Bukkit.getPlayer(uuid).getStatistic(Statistic.TIME_SINCE_REST) > 72000)
                        Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.mustSleep());
                    else {
                        no++;
                        voter.voteNo();
                        Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.youVoteNo());
                    }
                }
                else Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.alreadyVoted());
            }
        }
        else Bukkit.getPlayer(uuid).spigot().sendMessage(Messages.noVoteInProg(voteTypeString()));
    }

    // Attempts to start a vote if all conditions are met, otherwise informs player why vote can't start
    public void start(Player player, VoteType voteType) {
        // Read players tag, null if not there
        String tag;
        try {
            tag = player.getPlayerListName().split("#")[1];
        } catch (IndexOutOfBoundsException e) {
            tag = "Active";
        }

        if (!player.hasPermission("skipnight.vote")) // If player doesn't have permission
            player.sendMessage(ChatColor.RED + "You don't have permission to run this!");
        else if (!isInOverworld(player)) // If player isn't in the overworld
            player.sendMessage(ChatColor.RED + "You must be in the overworld to start a vote!");
        else if (voteType == VoteType.NIGHT && player.getWorld().getTime() < 12516) // If it's day, trying to skip night
            player.sendMessage(ChatColor.RED + "You can only start a vote at night!");
        else if (voteType == VoteType.DAY && player.getWorld().getTime() >= 12516) // If it's night, trying to skip day
            player.sendMessage(ChatColor.RED + "You can only start a vote during the day!");
        else if (tag.equalsIgnoreCase("Idle"))
            player.sendMessage(ChatColor.RED + "You cannot start a vote while idle!");
        else if (tag.equalsIgnoreCase("Away"))
            player.sendMessage(ChatColor.RED + "You cannot start a vote while away!");
        else if (!(timer == Timer.COMPLETE)) // If there's a vote happening
            player.sendMessage(ChatColor.RED + "Vote already in progress!");
        else if (voteType == VoteType.NIGHT && player.getStatistic(Statistic.TIME_SINCE_REST) >= 72000) // If it's night, player hasn't slept in 3 days
            player.sendMessage(ChatColor.RED + "You must sleep in a bed first!");
        else {
            timer = Timer.INIT;
            this.voteType = voteType;
            this.player = player;
            world = player.getWorld();
            run();
        }
    }

    // Checks whether player is in overworld
    private boolean isInOverworld(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NORMAL;
    }

    public String voteTypeString() {
        String voteType = "null";
        switch (this.voteType) {
            case DAY:
                voteType = "day";
                break;
            case NIGHT:
                voteType = "night";
                break;
        }
        return voteType;
    }

    private List<Voter> updateAll(List<Voter> voters) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            // Read players tag, null if not there
            String tag;
            try {
                tag = player.getPlayerListName().split("#")[1];
            } catch (IndexOutOfBoundsException e) {
                tag = "Active";
            }

            if (isInOverworld(player) && player.hasPermission("skipnight.vote")) {
                if (voters.contains(voter)) {
                    voter = voters.get(voters.indexOf(voter));
                    if (tag.equalsIgnoreCase("Idle")) { // in V, idle
                        voters.remove(voter);
                        idleVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        player.spigot().sendMessage(Messages.idle());
                    }
                    if (tag.equalsIgnoreCase("Away")) { // in V, away
                        voters.remove(voter);
                        awayVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        player.spigot().sendMessage(Messages.away());
                    }
                } else if (awayVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Idle")) { // in A, idle
                        awayVoters.remove(voter);
                        idleVoters.add(voter);
                        player.spigot().sendMessage(Messages.idle());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in A, active
                        awayVoters.remove(voter);
                        voters.add(voter);
                        player.spigot().sendMessage(Messages.back());
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                } else if (idleVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Away")) { // in I, away
                        idleVoters.remove(voter);
                        awayVoters.add(voter);
                        player.spigot().sendMessage(Messages.away());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in I, active
                        idleVoters.remove(voter);
                        voters.add(voter);
                        player.spigot().sendMessage(Messages.back());
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                } else {
                    if (tag.equalsIgnoreCase("Away")) { // not in V, A, I, away
                        awayVoters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.away());
                    } else if (tag.equalsIgnoreCase("Idle")) { // not in V, A, I, idle
                        idleVoters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.idle());
                    } else { // not in V, A, I, active
                        voters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.voteStarted(voteTypeString()));
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                }
            } else {
                if (voters.contains(voter)) { // not in world, in V
                    voters.remove(voter);
                    int vote = voter.resetVote();
                    if (vote == 1) yes--;
                    if (vote == -1) no--;
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
                if (idleVoters.contains(voter)) { // not in world, in I
                    idleVoters.remove(voter);
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
                if (awayVoters.contains(voter)) { // not in world, in A
                    awayVoters.remove(voter);
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
            }
        }
        playerCount = voters.size();
        away = awayVoters.size();
        idle = idleVoters.size();
        return voters;
    }

    private List<Voter> updateAll(List<Voter> voters, BaseComponent[] message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            // Read players tag, null if not there
            String tag;
            try {
                tag = player.getPlayerListName().split("#")[1];
            } catch (IndexOutOfBoundsException e) {
                tag = "Active";
            }

            if (isInOverworld(player) && player.hasPermission("skipnight.vote")) {
                if (voters.contains(voter)) {
                    voter = voters.get(voters.indexOf(voter));
                    if (tag.equalsIgnoreCase("Idle")) { // in V, idle
                        voters.remove(voter);
                        idleVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        player.spigot().sendMessage(Messages.idle());
                    }
                    if (tag.equalsIgnoreCase("Away")) { // in V, away
                        voters.remove(voter);
                        awayVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        player.spigot().sendMessage(Messages.away());
                    }
                } else if (awayVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Idle")) { // in A, idle
                        awayVoters.remove(voter);
                        idleVoters.add(voter);
                        player.spigot().sendMessage(Messages.idle());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in A, active
                        awayVoters.remove(voter);
                        voters.add(voter);
                        player.spigot().sendMessage(Messages.back());
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                } else if (idleVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Away")) { // in I, away
                        idleVoters.remove(voter);
                        awayVoters.add(voter);
                        player.spigot().sendMessage(Messages.away());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in I, active
                        idleVoters.remove(voter);
                        voters.add(voter);
                        player.spigot().sendMessage(Messages.back());
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                } else {
                    if (tag.equalsIgnoreCase("Away")) { // not in V, A, I, away
                        awayVoters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.away());
                    } else if (tag.equalsIgnoreCase("Idle")) { // not in V, A, I, idle
                        idleVoters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.idle());
                    } else { // not in V, A, I, active
                        voters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.voteStarted(voteTypeString()));
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                }
                player.spigot().sendMessage(message);
            } else {
                if (voters.contains(voter)) { // not in world, in V
                    voters.remove(voter);
                    int vote = voter.resetVote();
                    if (vote == 1) yes--;
                    if (vote == -1) no--;
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
                if (idleVoters.contains(voter)) { // not in world, in I
                    idleVoters.remove(voter);
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
                if (awayVoters.contains(voter)) { // not in world, in A
                    awayVoters.remove(voter);
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
            }
        }
        playerCount = voters.size();
        away = awayVoters.size();
        idle = idleVoters.size();
        return voters;
    }

    private List<Voter> updateAll(List<Voter> voters, Player sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            // Read players tag, null if not there
            String tag;
            try {
                tag = player.getPlayerListName().split("#")[1];
            } catch (IndexOutOfBoundsException e) {
                tag = "Active";
            }

            if (isInOverworld(player) && player.hasPermission("skipnight.vote")) {
                if (voters.contains(voter)) {
                    voter = voters.get(voters.indexOf(voter));
                    if (tag.equalsIgnoreCase("Idle")) { // in V, idle
                        voters.remove(voter);
                        idleVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        player.spigot().sendMessage(Messages.idle());
                    }
                    if (tag.equalsIgnoreCase("Away")) { // in V, away
                        voters.remove(voter);
                        awayVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        player.spigot().sendMessage(Messages.away());
                    }
                } else if (awayVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Idle")) { // in A, idle
                        awayVoters.remove(voter);
                        idleVoters.add(voter);
                        player.spigot().sendMessage(Messages.idle());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in A, active
                        awayVoters.remove(voter);
                        voters.add(voter);
                        player.spigot().sendMessage(Messages.back());
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                } else if (idleVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Away")) { // in I, away
                        idleVoters.remove(voter);
                        awayVoters.add(voter);
                        player.spigot().sendMessage(Messages.away());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in I, active
                        idleVoters.remove(voter);
                        voters.add(voter);
                        player.spigot().sendMessage(Messages.back());
                        player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                    }
                } else {
                    if (tag.equalsIgnoreCase("Away")) { // not in V, A, I, away
                        awayVoters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.voteStarted(voteTypeString()));
                        player.spigot().sendMessage(Messages.away());
                    } else if (tag.equalsIgnoreCase("Idle")) { // not in V, A, I, idle
                        idleVoters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.voteStarted(voteTypeString()));
                        player.spigot().sendMessage(Messages.idle());
                    } else { // not in V, A, I, active
                        voters.add(voter);
                        bar.addPlayer(player);
                        player.spigot().sendMessage(Messages.voteStarted(voteTypeString()));
                        if (player == sender) {
                            voter.voteYes();
                            player.spigot().sendMessage(Messages.youVoteYes());
                        } else {
                            player.spigot().sendMessage(Messages.voteButtons(voteTypeString()));
                        }
                    }
                }
            } else {
                if (voters.contains(voter)) { // not in world, in V
                    voters.remove(voter);
                    int vote = voter.resetVote();
                    if (vote == 1) yes--;
                    if (vote == -1) no--;
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
                if (idleVoters.contains(voter)) { // not in world, in I
                    idleVoters.remove(voter);
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
                if (awayVoters.contains(voter)) { // not in world, in A
                    awayVoters.remove(voter);
                    bar.removePlayer(player);
                    player.spigot().sendMessage(Messages.leftWorld());
                }
            }
        }
        playerCount = voters.size();
        away = awayVoters.size();
        idle = idleVoters.size();
        return voters;
    }
}
