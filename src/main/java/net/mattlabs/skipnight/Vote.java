package net.mattlabs.skipnight;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.mattlabs.skipnight.util.FastForward;
import net.mattlabs.skipnight.util.Versions;
import net.mattlabs.skipnight.util.VoteType;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Vote implements Runnable, Listener {

    enum Timer {
        INIT,
        OPERATION,
        INTERRUPT,
        CANCEL,
        FINAL,
        COMPLETE,
        COOLDOWN,
        OFF
    }

    Timer timer;
    private VoteType voteType;
    private int yes, no, playerCount, countDown, countDownInit, away, idle;
    private BossBar bar;
    private final SkipNight plugin;
    private Map<UUID, Voter> voters;
    private Player voteInitiator;
    private World world;
    private FastForward fastForward;
    private final Messages messages;
    private final BukkitAudiences platform;
    private final String version;
    private final boolean playerActivity;
    private final Config config;

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
            if (player.hasPermission("skipnight.vote." + voteTypeCommandString(voteType))) { // player has permission
                voters.remove(player.getUniqueId());
            }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        // TODO fix so players clicking on bed during day don't get message
        // Player has permission and isn't the only one in the world
        if (player.hasPermission("skipnight.vote.night") && player.getWorld().getPlayers().size() > 1 && timer == Timer.OFF) {
            platform.player(player).sendMessage(messages.beforeVote().inBedNoVoteInProg());
        }
    }

    public void run() {
        switch (timer) {
            case INIT -> doInit();
            case OPERATION -> doOperation();
            case INTERRUPT -> doInterrupt();
            case CANCEL -> doCancel();
            case FINAL -> doFinal();
            case COMPLETE -> doComplete();
            case COOLDOWN -> doCooldown();
            default -> {}
        }
    }

    /* The first stage of a vote. This is where lists, variables and the boss bar are created. The players are updated
    *  about the vote that has just started. */
    private void doInit() {
        voters = new HashMap<>();

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

        updateAll();

        timer = Timer.OPERATION;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    /* The main stage of the vote. Checks for a completed vote or waits until the last 10 seconds to move on.
    *  Sets the boss bar each second. */
    private void doOperation() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.INTERRUPT;
        if (voteCancel()) timer = Timer.CANCEL;
        bar.progress((float) countDown / countDownInit);
        if (playerActivity)
            bar.name(messages.duringVote().currentVotePA(yes, no, idle, away));
        else
            bar.name(messages.duringVote().currentVote(yes, no));
        updateAll();
        if (countDown <= 10) timer = Timer.FINAL;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    /* The stage for when everyone has voted. Sets the boss bar and moves onto the next stage. */
    private void doInterrupt() {
        countDown = 0;
        bar.progress(1.0f);
        bar.name(messages.afterVote().allPlayersHaveVoted());
        bar.color(BossBar.Color.YELLOW);

        timer = Timer.COMPLETE;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    /* The last 10 seconds of the vote. Boss bar alternates white and purple and players receive a message. */
    private void doFinal() {
        countDown--;
        if (yes + no == playerCount) timer = Timer.INTERRUPT;
        if (voteCancel()) timer = Timer.CANCEL;
        bar.progress((float) countDown / countDownInit);
        if (playerActivity)
            bar.name(messages.duringVote().currentVotePA(yes, no, idle, away));
        else
            bar.name(messages.duringVote().currentVote(yes, no));
        if (countDown == 9) updateAll(messages.duringVote().tenSecondsLeft());
        else updateAll();

        if (countDown % 2 == 1) bar.color(BossBar.Color.WHITE);
        else bar.color(BossBar.Color.PURPLE);

        if (countDown == 0) timer = Timer.COMPLETE;
        plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
    }

    /* The stage of the vote after the timer has run out. Displays vote passed/failed via boss bar and message.
    *  Initiates a fast-forward to the correct time. */
    private void doComplete() {
        BukkitRunnable bossBarFastForward = new BukkitRunnable() {

            @Override
            public void run() {
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
                updateAll(messages.afterVote().votePassedBossBar(voteTypeString()));
                fastForward = new FastForward(world, plugin, voteType);
                plugin.getServer().getScheduler().runTaskLater(plugin, fastForward, 10);

                // Set boss bar progress to fast-forward progress
                bar.progress(0.0f);
                bossBarFastForward.runTaskTimer(plugin, 0, 1);

                //if (world.hasStorm()) world.setStorm(false);
            }
            else {
                bar.name(messages.afterVote().voteFailedBossBar());
                bar.color(BossBar.Color.RED);
                updateAll(messages.afterVote().voteFailedBossBar(voteTypeString()));
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
        }

        if (countDown <= -2) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);

        if (countDown <= -9 && bar.progress() == 1.0f) {
            platform.all().hideBossBar(bar);
            bar = null;
            voters = null;
            fastForward = null;
            voteType = null;
            timer = yes > no ? Timer.OFF : Timer.COOLDOWN;
        }
    }

    /* Runs after everything is done to prevent a vote from starting again until after a time. */
    private void doCooldown() {
        countDown--;
        if (countDown >= (config.getCooldown() * -1) - 9) plugin.getServer().getScheduler().runTaskLater(plugin, this, 20);
        else timer = Timer.OFF;
    }

    /* Runs when it becomes the target time during the vote. Switches to blue boss bar and cancels everything. */
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

    public void addYes(Player player, VoteType voteType) {
        if (timer != Timer.OFF) {
            Voter voter = new Voter(player.getUniqueId());
            if (voters.containsKey(voter.getUuid())) {
                voter = voters.get(voter.getUuid());
                if (voter.getVote() == 0) {
                    if (this.voteType == VoteType.NIGHT && playerMustSleep(player) && config.isPhantomSupport()) {
                        platform.player(player).sendMessage(messages.beforeVote().mustSleep());
                        actionBarMessage(messages.duringVote().playerHasNotSlept(player.getName()));
                    }
                    else {
                        voter.voteYes();
                        platform.player(player).sendMessage(messages.duringVote().youVoteYes());
                        actionBarMessage(messages.duringVote().playerHasVotedYes(player.getName()));
                    }
                }
                else platform.player(player).sendMessage(messages.duringVote().alreadyVoted());
            }
        }
        else platform.player(player).sendMessage(messages.beforeVote().noVoteInProg(voteTypeCommandString(voteType)));
    }

    public void addNo(Player player, VoteType voteType) {
        if (timer != Timer.OFF) {
            Voter voter = new Voter(player.getUniqueId());
            if (voters.containsKey(voter.getUuid())) {
                voter = voters.get(voter.getUuid());
                if (voter.getVote() == 0) {
                    if (this.voteType == VoteType.NIGHT && playerMustSleep(player) && config.isPhantomSupport()) {
                        platform.player(player).sendMessage(messages.beforeVote().mustSleep());
                        actionBarMessage(messages.duringVote().playerHasNotSlept(player.getName()));
                    }
                    else {
                        voter.voteNo();
                        platform.player(player).sendMessage(messages.duringVote().youVoteNo());
                        actionBarMessage(messages.duringVote().playerHasVotedNo(player.getName()));
                    }
                }
                else platform.player(player).sendMessage(messages.duringVote().alreadyVoted());
            }
        }
        else platform.player(player).sendMessage(messages.beforeVote().noVoteInProg(voteTypeCommandString(voteType)));
    }

    // Attempts to start a vote if all conditions are met, otherwise informs player why vote can't start
    public void start(Player player, VoteType voteType) {
        if (!player.hasPermission("skipnight.vote." + voteTypeCommandString(voteType))) // If player doesn't have permission
            platform.player(player).sendMessage(messages.general().noPerm());
        else if (config.getWorldBlacklist().contains(player.getWorld().getName())) // If world is blacklisted
            platform.player(player).sendMessage(messages.beforeVote().worldIsBlacklisted());
        else if (!isInOverworld(player)) // If player isn't in the overworld
            platform.player(player).sendMessage(messages.beforeVote().worldNotOverworld());
        else if (voteType == VoteType.NIGHT && player.getWorld().getTime() < 12516 && !player.getWorld().hasStorm()) // If it's day and not raining, trying to skip night
            platform.player(player).sendMessage(messages.beforeVote().canOnlyVoteAtNight());
        else if (voteType == VoteType.DAY && player.getWorld().getTime() >= 12516) // If it's night, trying to skip day
            platform.player(player).sendMessage(messages.beforeVote().canOnlyVoteAtDay());
        else if (readTag(player).equalsIgnoreCase("Idle"))
            platform.player(player).sendMessage(messages.beforeVote().noVoteWhileIdle());
        else if (readTag(player).equalsIgnoreCase("Away"))
            platform.player(player).sendMessage(messages.beforeVote().noVoteWhileAway());
        else if (timer == Timer.COOLDOWN) // If the vote is in cooldown
            platform.player(player).sendMessage(messages.beforeVote().cooldown());
        else if (!(timer == Timer.OFF)) // If there's a vote happening
            platform.player(player).sendMessage(messages.duringVote().voteInProg());
        else if (voteType == VoteType.NIGHT && playerMustSleep(player) && config.isPhantomSupport()) // If it's night, player hasn't slept in 3 days
            platform.player(player).sendMessage(messages.beforeVote().mustSleepNewVote());
        else {
            timer = Timer.INIT;
            this.voteType = voteType;
            this.voteInitiator = player;
            world = player.getWorld();
            run();
        }
    }

    private void updateAll() {
        updateAll(null);
    }
    private void updateAll(Component message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            List<Component> messageList = new ArrayList<>();

            // Check permission
            if (player.hasPermission("skipnight.vote." + voteTypeCommandString(voteType))) {
                if (isInOverworld(player)) {
                    platform.player(player).showBossBar(bar);

                    // Add voter to list
                    if (!voters.containsKey(voter.getUuid())) {
                        voters.put(voter.getUuid(), voter);
                        // Add message
                        messageList.add(messages.duringVote().voteStarted(voteInitiator.getName(), voteTypeString()));
                    }
                    // Or get from the list
                    else voter = voters.get(voter.getUuid());

                    // Started the vote, automatically vote yes
                    if (player.equals(voteInitiator) && timer == Timer.INIT) {
                        messageList.add(messages.duringVote().youVoteYes());
                        voter.voteYes();
                    }

                    switch (readTag(player)) {
                        case "Active" -> {
                            if (!voter.isActive()) {
                                if (voter.isIdle() || voter.isAway())
                                    messageList.add(messages.duringVote().back()); // Was idle or away, now back
                                if (voter.getVote() == 0)
                                    messageList.add(messages.duringVote().voteButtons(voteTypeString())); // Was not in a bed, needs to vote
                                voter.setActive();
                            }
                        }
                        case "Bed" -> {
                            if (!voter.isBed()) {
                                if (voter.isIdle() || voter.isAway())
                                    messageList.add(messages.duringVote().back()); // Was idle or away, now back
                                // In bed, auto vote yes
                                messageList.add(messages.duringVote().inBedVotedYes());
                                voter.voteYes();
                                voter.setBed();
                            }
                        }
                        case "Idle" -> {
                            if (!voter.isIdle()) {
                                messageList.add(messages.duringVote().idle());
                                voter.resetVote();
                                voter.setIdle();
                            }
                        }
                        case "Away" -> {
                            if (!voter.isAway()) {
                                messageList.add(messages.duringVote().away());
                                voter.resetVote();
                                voter.setAway();
                            }
                        }
                    }

                    if (message != null) messageList.add(message);

                    // TODO move this so not in overworld gets messages too
                    // Send messages
                    for (Component messageToSend : messageList) {
                        SkipNight.getInstance().getPlatform().player(player).sendMessage(messageToSend);
                    }
                }
                // Not in Overworld
                else {
                    if (voters.containsKey(voter.getUuid())) {
                        messageList.add(messages.duringVote().leftWorld());
                        voters.remove(voter.getUuid());
                    }
                }
            }
        }
        playerCount = voters.size();
        away = (int) voters.values().stream().filter(Voter::isAway).count();
        idle = (int) voters.values().stream().filter(Voter::isIdle).count();
        yes = (int) voters.values().stream().filter(voter -> voter.getVote() == 1).count();
        no = (int) voters.values().stream().filter(voter -> voter.getVote() == -1).count();
    }

    private void actionBarMessage(Component message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Voter voter = new Voter(player.getUniqueId());

            if (isInOverworld(player) && player.hasPermission("skipnight.vote." + voteTypeCommandString(voteType))) {
                if (voters.containsKey(voter.getUuid())) {
                    platform.player(player).sendActionBar(message);
                }
            }
        }
    }

    private boolean voteCancel() {
        return (voteType == VoteType.NIGHT && (world.getTime() > 23900 || world.getTime() < 12516)) && !world.hasStorm() ||
                (voteType == VoteType.DAY && world.getTime() > 12516 && world.getTime() < 23900);
    }

    private boolean playerMustSleep(Player player) {
        // Check version for TIME_SINCE_REST added in 1.13
        if (Versions.versionCompare("1.13.0", version) <= 0) {
            return player.getStatistic(Statistic.TIME_SINCE_REST) >= 72000;
        } else return false;
    }

    @SuppressWarnings("deprecation")
    private String readTag(Player player) {
        // Read players tag, null if not there
        try {
            return player.getPlayerListName().split("#")[1];
        } catch (IndexOutOfBoundsException e) {
            return player.isSleeping() ? "Bed" : "Active";
        }
    }

    // Checks whether player is in overworld
    private boolean isInOverworld(Player player) {
        return timer == Timer.OFF ? player.getWorld().getEnvironment() == World.Environment.NORMAL : player.getWorld().equals(voteInitiator.getWorld());
    }

    public String voteTypeString() {
        return voteTypeString(this.voteType);
    }

    public String voteTypeString(VoteType voteType) {
        return switch (voteType) {
            case DAY -> messages.getDayString();
            case NIGHT -> messages.getNightString();
        };
    }

    public String voteTypeCommandString(VoteType voteType) {
        return switch (voteType) {
            case DAY -> "day";
            case NIGHT -> "night";
        };
    }
}
