package net.mattlabs.skipnight;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.mattlabs.skipnight.util.FastForward;
import net.mattlabs.skipnight.util.Versions;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vote implements Runnable, Listener {

    private enum Timer {
        INIT,
        OPERATION,
        INTERRUPT,
        CANCEL,
        FINAL,
        COMPLETE,
        COOLDOWN,
        OFF
    }

    private Timer timer;
    private VoteType voteType;
    private int yes, no, playerCount, countDown, countDownInit, away, idle;
    private BossBar bar;
    private SkipNight plugin;
    private List<Voter> voters;
    private List<Voter> awayVoters;
    private List<Voter> idleVoters;
    private Player player;
    private World world;
    private FastForward fastForward;
    private Messages messages;
    private BukkitAudiences platform;
    private String version;
    private boolean playerActivity;
    private Config config;

    Vote(SkipNight plugin) {
        timer = Timer.OFF;
        this.plugin = plugin;
        messages = SkipNight.getInstance().getMessages();
        platform = SkipNight.getInstance().getPlatform();
        version = SkipNight.getInstance().getVersion();
        playerActivity = SkipNight.getInstance().hasPlayerActivity();
        config = SkipNight.getInstance().getConfiguration();
    }

    @EventHandler
    public void onLogoff(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (timer != Timer.OFF) // vote is running
            if (player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) { // player has permission
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

        if (timer != Timer.OFF && voteType == VoteType.NIGHT) { // vote is running at night
            if (player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) { // player has permission
                Voter voter = new Voter(player.getUniqueId());
                if (voters.contains(voter)) { // Voter exists but hasn't voted
                    voter = voters.get(voters.indexOf(voter));
                    if (voter.getVote() == 0) {
                        voter.voteYes();
                        yes++;
                        platform.player(player).sendMessage(messages.duringVote().inBedVotedYes());
                        plugin.getLogger().info("Voter exists but hasn't voted");
                    }
                }
                else { // Voter doesn't exist but hasn't voted
                    voters.add(voter);
                    voter.voteYes();
                    yes++;
                    platform.player(player).sendMessage(messages.duringVote().inBedVotedYes());
                    plugin.getLogger().info("Voter doesn't exist but hasn't voted");
                }
            }
        }
        else {
            if (player.hasPermission("skipnight.vote.night") && player.getWorld().getTime() >= 12516) { // player has permission
                if (player.getWorld().getPlayers().size() > 1) // if player isn't only one in the world
                    platform.player(player).sendMessage(messages.beforeVote().inBedNoVoteInProg());
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
            case CANCEL:
                doCancel();
                break;
            case FINAL:
                doFinal();
                break;
            case COMPLETE:
                doComplete();
                break;
            case COOLDOWN:
                doCooldown();
                break;
            default:
                break;
        }
    }

    private void doInit() {
        voters = new ArrayList<>();
        awayVoters = new ArrayList<>();
        idleVoters = new ArrayList<>();

        bar = BossBar.bossBar(Component.text(), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

        yes = 1;
        no = 0;
        countDown = config.getVoteDuration();
        countDownInit = config.getVoteDuration();
        away = 0;
        idle = 0;

        if (playerActivity)
            bar.name(messages.duringVote().currentVotePA(yes, no, idle, away)).color(BossBar.Color.PURPLE);
        else
            bar.name(messages.duringVote().currentVote(yes, no)).color(BossBar.Color.PURPLE);

        voters = updateAll(voters, player);

        timer = Timer.OPERATION;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doOperation() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.INTERRUPT;
        if (voteCancel()) timer = Timer.CANCEL;
        bar.progress((float) countDown / countDownInit);
        if (playerActivity)
            bar.name(messages.duringVote().currentVotePA(yes, no, idle, away));
        else
            bar.name(messages.duringVote().currentVote(yes, no));
        voters = updateAll(voters);
        if (countDown <= 10) timer = Timer.FINAL;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doInterrupt() {
        countDown = 0;
        bar.progress(1.0f);
        bar.name(messages.afterVote().allPlayersHaveVoted());
        bar.color(BossBar.Color.YELLOW);

        timer = Timer.COMPLETE;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doFinal() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.INTERRUPT;
        if (voteCancel()) timer = Timer.CANCEL;
        bar.progress((float) countDown / countDownInit);
        if (playerActivity)
            bar.name(messages.duringVote().currentVotePA(yes, no, idle, away));
        else
            bar.name(messages.duringVote().currentVote(yes, no));
        if (countDown == 9) voters = updateAll(voters, messages.duringVote().tenSecondsLeft());
        else voters = updateAll(voters);

        if (countDown % 2 == 1) bar.color(BossBar.Color.WHITE);
        else bar.color(BossBar.Color.PURPLE);

        if (countDown == 0) timer = Timer.COMPLETE;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    private void doComplete() {
        BukkitRunnable bossBarFastForward = new BukkitRunnable() {

            @Override
            public void run() {
                countDown = -7;
                float totalTime;
                if (voteType == VoteType.NIGHT) totalTime = 23900f;
                else totalTime = 12516f;
                float progress = world.getTime() / totalTime;
                if (progress > 1.0f) {
                    bar.progress(1.0f);
                    this.cancel();
                }
                else bar.progress(progress);
            }
        };

        countDown--;
        if (countDown == -1) {
            bar.progress(1.0f);
            if (yes > no) {
                bar.name(messages.afterVote().votePassedBossBar());
                bar.color(BossBar.Color.GREEN);
                updateAll(voters, messages.afterVote().votePassedBossBar(voteTypeString()));
                fastForward = new FastForward(world, plugin, voteType);
                plugin.getServer().getScheduler().runTaskLater(plugin, fastForward, 10);

                // Set boss bar progress to fast forward progress
                bar.progress(0.0f);
                bossBarFastForward.runTaskTimer(plugin, 0, 1);

                if (world.hasStorm()) world.setStorm(false);
            }
            else {
                bar.name(messages.afterVote().voteFailedBossBar());
                bar.color(BossBar.Color.RED);
                updateAll(voters, messages.afterVote().voteFailedBossBar(voteTypeString()));
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
        }

        if (countDown <= -2) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);

        if (countDown == -9) {
            platform.all().hideBossBar(bar);
            bar = null;
            voters = null;
            fastForward = null;
            voteType = null;
            timer = yes > no ? Timer.OFF : Timer.COOLDOWN;
        }
    }

    private void doCooldown() {
        if (countDown >= (config.getCooldown() * -1) - 9) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
        else timer = Timer.OFF;
    }

    private void doCancel() {
        if (countDown > 0) countDown = 0;
        if (countDown == 0) {
            bar.progress(1.0f);
            bar.color(BossBar.Color.BLUE);
            if (voteType == VoteType.NIGHT) bar.name(messages.afterVote().itIsAlreadyDay());
            else bar.name(messages.afterVote().itIsAlreadyNight());
        }

        countDown--;

        if (countDown > -4) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);

        if (countDown == -4) {
            platform.all().hideBossBar(bar);
            bar = null;
            voters = null;
            fastForward = null;
            voteType = null;
            timer = Timer.OFF;
        }
    }

    public void addYes(UUID uuid, VoteType voteType) {
        if (timer != Timer.OFF) {
            Voter voter = new Voter(uuid);
            if (voters.contains(voter)) {
                voter = voters.get(voters.lastIndexOf(voter));
                if (voter.getVote() == 0) {
                    boolean playerMustSleep;
                    // Check version for TIME_SINCE_REST added in 1.13
                    if (Versions.versionCompare("1.13.0", version) <= 0) {
                        playerMustSleep = Bukkit.getPlayer(uuid).getStatistic(Statistic.TIME_SINCE_REST) >= 72000;
                    } else playerMustSleep = false;
                    
                    if (this.voteType == VoteType.NIGHT && playerMustSleep && config.isPhantomSupport()) {
                        platform.player(uuid).sendMessage(messages.beforeVote().mustSleep());
                        actionBarMessage(messages.duringVote().playerHasNotSlept(Bukkit.getPlayer(uuid).getName()));

                    }
                    else {
                        yes++;
                        voter.voteYes();
                        platform.player(uuid).sendMessage(messages.duringVote().youVoteYes());
                        actionBarMessage(messages.duringVote().playerHasVotedYes(Bukkit.getPlayer(uuid).getName()));
                    }
                }
                else platform.player(uuid).sendMessage(messages.duringVote().alreadyVoted());
            }
        }
        else platform.player(uuid).sendMessage(messages.beforeVote().noVoteInProg(voteTypeString(voteType)));
    }

    public void addNo(UUID uuid, VoteType voteType) {
        if (timer != Timer.OFF) {
            Voter voter = new Voter(uuid);
            if (voters.contains(voter)) {
                voter = voters.get(voters.lastIndexOf(voter));
                if (voter.getVote() == 0) {
                    boolean playerMustSleep;
                    // Check version for TIME_SINCE_REST added in 1.13
                    if (Versions.versionCompare("1.13.0", version) <= 0) {
                        playerMustSleep = Bukkit.getPlayer(uuid).getStatistic(Statistic.TIME_SINCE_REST) >= 72000;
                    } else playerMustSleep = false;
                    
                    if (this.voteType == VoteType.NIGHT && playerMustSleep && config.isPhantomSupport()) {
                        platform.player(uuid).sendMessage(messages.beforeVote().mustSleep());
                        actionBarMessage(messages.duringVote().playerHasNotSlept(Bukkit.getPlayer(uuid).getName()));
                    }
                    else {
                        no++;
                        voter.voteNo();
                        platform.player(uuid).sendMessage(messages.duringVote().youVoteNo());
                        actionBarMessage(messages.duringVote().playerHasVotedNo(Bukkit.getPlayer(uuid).getName()));
                    }
                }
                else platform.player(uuid).sendMessage(messages.duringVote().alreadyVoted());
            }
        }
        else platform.player(uuid).sendMessage(messages.beforeVote().noVoteInProg(voteTypeString(voteType)));
    }

    // Attempts to start a vote if all conditions are met, otherwise informs player why vote can't start
    public void start(Player player, VoteType voteType) {
        // Read players tag, null if not there
        String tag;
        boolean playerMustSleep;
        try {
            tag = player.getPlayerListName().split("#")[1];
        } catch (IndexOutOfBoundsException e) {
            tag = "Active";
        }

        // Check version for TIME_SINCE_REST added in 1.13
        if (Versions.versionCompare("1.13.0", version) <= 0) {
            playerMustSleep = player.getStatistic(Statistic.TIME_SINCE_REST) >= 72000;
        } else playerMustSleep = false;

        if (!player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) // If player doesn't have permission
            platform.player(player).sendMessage(messages.general().noPerm());
        else if (config.getWorldBlacklist().contains(player.getWorld().getName())) // If world is blacklisted
            platform.player(player).sendMessage(messages.beforeVote().worldIsBlacklisted());
        else if (!isInOverworld(player)) // If player isn't in the overworld
            platform.player(player).sendMessage(messages.beforeVote().worldNotOverworld());
        else if (voteType == VoteType.NIGHT && player.getWorld().getTime() < 12516 && !player.getWorld().hasStorm()) // If it's day and not raining, trying to skip night
            platform.player(player).sendMessage(messages.beforeVote().canOnlyVoteAtNight());
        else if (voteType == VoteType.DAY && player.getWorld().getTime() >= 12516) // If it's night, trying to skip day
            platform.player(player).sendMessage(messages.beforeVote().canOnlyVoteAtDay());
        else if (tag.equalsIgnoreCase("Idle"))
            platform.player(player).sendMessage(messages.beforeVote().noVoteWhileIdle());
        else if (tag.equalsIgnoreCase("Away"))
            platform.player(player).sendMessage(messages.beforeVote().noVoteWhileAway());
        else if (timer == Timer.COOLDOWN) // If the vote is in cooldown
            platform.player(player).sendMessage(messages.beforeVote().cooldown());
        else if (!(timer == Timer.OFF)) // If there's a vote happening
            platform.player(player).sendMessage(messages.duringVote().voteInProg());
        else if (voteType == VoteType.NIGHT && playerMustSleep && config.isPhantomSupport()) // If it's night, player hasn't slept in 3 days
            platform.player(player).sendMessage(messages.beforeVote().mustSleepNewVote());
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
        return voteTypeString(this.voteType);
    }

    public String voteTypeString(VoteType voteType) {
        String voteTypeString = "null";
        switch (voteType) {
            case DAY:
                voteTypeString = messages.getDayString();
                break;
            case NIGHT:
                voteTypeString = messages.getNightString();
                break;
        }
        return voteTypeString;
    }

    private String voteTypeCommandString() {
        String voteTypeString = "null";
        switch (voteType) {
            case DAY:
                voteTypeString = "day";
                break;
            case NIGHT:
                voteTypeString = "night";
                break;
        }
        return voteTypeString;
    }
    
    public String voteTypeStringCommand(VoteType voteType) {
        String voteTypeString = "null";
        switch (voteType) {
            case DAY:
                voteTypeString = "day";
                break;
            case NIGHT:
                voteTypeString = "night";
                break;
        }
        return voteTypeString;
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

            if (isInOverworld(player) && player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) {
                if (voters.contains(voter)) {
                    voter = voters.get(voters.indexOf(voter));
                    if (tag.equalsIgnoreCase("Idle")) { // in V, idle
                        voters.remove(voter);
                        idleVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    }
                    if (tag.equalsIgnoreCase("Away")) { // in V, away
                        voters.remove(voter);
                        awayVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        platform.player(player).sendMessage(messages.duringVote().away());
                    }
                } else if (awayVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Idle")) { // in A, idle
                        awayVoters.remove(voter);
                        idleVoters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in A, active
                        awayVoters.remove(voter);
                        voters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().back());
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                } else if (idleVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Away")) { // in I, away
                        idleVoters.remove(voter);
                        awayVoters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().away());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in I, active
                        idleVoters.remove(voter);
                        voters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().back());
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                } else {
                    if (tag.equalsIgnoreCase("Away")) { // not in V, A, I, away
                        awayVoters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().away());
                    } else if (tag.equalsIgnoreCase("Idle")) { // not in V, A, I, idle
                        idleVoters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    } else { // not in V, A, I, active
                        voters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().voteStarted(this.player.getName(), voteTypeString()));
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                }
            } else {
                if (voters.contains(voter)) { // not in world, in V
                    voters.remove(voter);
                    int vote = voter.resetVote();
                    if (vote == 1) yes--;
                    if (vote == -1) no--;
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
                if (idleVoters.contains(voter)) { // not in world, in I
                    idleVoters.remove(voter);
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
                if (awayVoters.contains(voter)) { // not in world, in A
                    awayVoters.remove(voter);
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
            }
        }
        playerCount = voters.size();
        away = awayVoters.size();
        idle = idleVoters.size();
        return voters;
    }

    private List<Voter> updateAll(List<Voter> voters, Component message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            // Read players tag, null if not there
            String tag;
            try {
                tag = player.getPlayerListName().split("#")[1];
            } catch (IndexOutOfBoundsException e) {
                tag = "Active";
            }

            if (isInOverworld(player) && player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) {
                if (voters.contains(voter)) {
                    voter = voters.get(voters.indexOf(voter));
                    if (tag.equalsIgnoreCase("Idle")) { // in V, idle
                        voters.remove(voter);
                        idleVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    }
                    if (tag.equalsIgnoreCase("Away")) { // in V, away
                        voters.remove(voter);
                        awayVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        platform.player(player).sendMessage(messages.duringVote().away());
                    }
                } else if (awayVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Idle")) { // in A, idle
                        awayVoters.remove(voter);
                        idleVoters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in A, active
                        awayVoters.remove(voter);
                        voters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().back());
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                } else if (idleVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Away")) { // in I, away
                        idleVoters.remove(voter);
                        awayVoters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().away());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in I, active
                        idleVoters.remove(voter);
                        voters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().back());
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                } else {
                    if (tag.equalsIgnoreCase("Away")) { // not in V, A, I, away
                        awayVoters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().away());
                    } else if (tag.equalsIgnoreCase("Idle")) { // not in V, A, I, idle
                        idleVoters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    } else { // not in V, A, I, active
                        voters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().voteStarted(this.player.getName(), voteTypeString()));
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                }
                platform.player(player).sendMessage(message);
            } else {
                if (voters.contains(voter)) { // not in world, in V
                    voters.remove(voter);
                    int vote = voter.resetVote();
                    if (vote == 1) yes--;
                    if (vote == -1) no--;
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
                if (idleVoters.contains(voter)) { // not in world, in I
                    idleVoters.remove(voter);
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
                if (awayVoters.contains(voter)) { // not in world, in A
                    awayVoters.remove(voter);
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
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

            if (isInOverworld(player) && player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) {
                if (voters.contains(voter)) {
                    voter = voters.get(voters.indexOf(voter));
                    if (tag.equalsIgnoreCase("Idle")) { // in V, idle
                        voters.remove(voter);
                        idleVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    }
                    if (tag.equalsIgnoreCase("Away")) { // in V, away
                        voters.remove(voter);
                        awayVoters.add(voter);
                        int vote = voter.resetVote();
                        if (vote == 1) yes--;
                        if (vote == -1) no--;
                        platform.player(player).sendMessage(messages.duringVote().away());
                    }
                } else if (awayVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Idle")) { // in A, idle
                        awayVoters.remove(voter);
                        idleVoters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in A, active
                        awayVoters.remove(voter);
                        voters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().back());
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                } else if (idleVoters.contains(voter)) {
                    if (tag.equalsIgnoreCase("Away")) { // in I, away
                        idleVoters.remove(voter);
                        awayVoters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().away());
                    }
                    if (!tag.equalsIgnoreCase("Idle")
                            && !tag.equalsIgnoreCase("Away")) { // in I, active
                        idleVoters.remove(voter);
                        voters.add(voter);
                        platform.player(player).sendMessage(messages.duringVote().back());
                        platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                    }
                } else {
                    if (tag.equalsIgnoreCase("Away")) { // not in V, A, I, away
                        awayVoters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().voteStarted(this.player.getName(), voteTypeString()));
                        platform.player(player).sendMessage(messages.duringVote().away());
                    } else if (tag.equalsIgnoreCase("Idle")) { // not in V, A, I, idle
                        idleVoters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().voteStarted(this.player.getName(), voteTypeString()));
                        platform.player(player).sendMessage(messages.duringVote().idle());
                    } else { // not in V, A, I, active
                        voters.add(voter);
                        platform.player(player).showBossBar(bar);
                        platform.player(player).sendMessage(messages.duringVote().voteStarted(this.player.getName(), voteTypeString()));
                        if (player == sender) {
                            voter.voteYes();
                            platform.player(player).sendMessage(messages.duringVote().youVoteYes());
                        } else if (player.isSleeping()) {
                            voter.voteYes();
                            platform.player(player).sendMessage(messages.duringVote().youVoteYes());
                        } else {
                            platform.player(player).sendMessage(messages.duringVote().voteButtons(voteTypeCommandString()));
                        }
                    }
                }
            } else {
                if (voters.contains(voter)) { // not in world, in V
                    voters.remove(voter);
                    int vote = voter.resetVote();
                    if (vote == 1) yes--;
                    if (vote == -1) no--;
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
                if (idleVoters.contains(voter)) { // not in world, in I
                    idleVoters.remove(voter);
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
                if (awayVoters.contains(voter)) { // not in world, in A
                    awayVoters.remove(voter);
                    platform.player(player).hideBossBar(bar);
                    platform.player(player).sendMessage(messages.duringVote().leftWorld());
                }
            }
        }
        playerCount = voters.size();
        away = awayVoters.size();
        idle = idleVoters.size();
        return voters;
    }

    private void actionBarMessage(Component message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            if (isInOverworld(player) && player.hasPermission("skipnight.vote." + voteTypeStringCommand(voteType))) {
                if (voters.contains(voter)) {
                    platform.player(player).sendActionBar(message);
                }
            }
        }
    }

    private boolean voteCancel() {
        return (voteType == VoteType.NIGHT && (world.getTime() > 23900 || world.getTime() < 12516)) && !world.hasStorm() ||
                (voteType == VoteType.DAY && world.getTime() > 12516 && world.getTime() < 23900);
    }
}
