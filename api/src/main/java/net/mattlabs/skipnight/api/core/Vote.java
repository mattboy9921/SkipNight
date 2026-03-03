package net.mattlabs.skipnight.api.core;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.mattlabs.skipnight.api.messaging.Messages;
import net.mattlabs.skipnight.api.messaging.MessagesAdapter;
import net.mattlabs.skipnight.api.player.PlayerAdapter;
import net.mattlabs.skipnight.api.world.WorldAdapter;
import net.mattlabs.skipnight.api.config.Config;
import net.mattlabs.skipnight.api.scheduler.ScheduledRunnable;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.util.FastForward;
import net.mattlabs.skipnight.api.util.VoteType;

import java.util.*;

public class Vote extends ScheduledRunnable {

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
    private Map<UUID, Voter> voters;
    private UUID voteInitiator;
    private UUID world;
    private FastForward fastForward;
    private final Messages messages;
    private final boolean playerActivity;
    private final Config config;
    private final Scheduler scheduler;
    private final PlayerAdapter playerAdapter;
    private final WorldAdapter worldAdapter;
    private final MessagesAdapter messagesAdapter;

    public Vote(Messages messages, boolean playerActivity, Config config, Scheduler scheduler, PlayerAdapter playerAdapter, WorldAdapter worldAdapter, MessagesAdapter messagesAdapter) {
        timer = Timer.OFF;
        this.messages = messages;
        this.playerActivity = playerActivity;
        this.config = config;
        this.scheduler = scheduler;
        this.playerAdapter = playerAdapter;
        this.worldAdapter = worldAdapter;
        this.messagesAdapter = messagesAdapter;
    }

    public void onLogOffEvent(UUID playerUUID) {
        if (timer != Timer.OFF)
            if (playerAdapter.hasPermission(playerUUID, "skipnight.vote." + voteTypeCommandString(voteType))) {
                voters.remove(playerUUID);
            }
    }

    public void onBedEnterEvent(UUID playerUUID) {
        UUID worldUUID = playerAdapter.getWorldUUID(playerUUID);
        // Player has permission, isn't the only one in the world, and it is night (or storming)
        if (playerAdapter.hasPermission(playerUUID, "skipnight.vote.night")
                && worldAdapter.getPlayers(worldUUID).size() > 1
                && timer == Timer.OFF
                && (worldAdapter.getTime(worldUUID) > 12516 || worldAdapter.hasStorm(worldUUID))) {
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().inBedNoVoteInProg());
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
        scheduler.runTaskLater(this, 20);
    }

    /* The main stage of the vote. Checks for a completed vote or waits until the last 10 seconds to move on.
    *  Sets the boss bar each second. */
    private void doOperation() {
        countDown--;
        if (yes + no + idle + away == playerCount) timer = Timer.INTERRUPT;
        if (voteCancel()) timer = Timer.CANCEL;
        bar.progress((float) countDown / countDownInit);
        if (playerActivity)
            bar.name(messages.duringVote().currentVotePA(yes, no, idle, away));
        else
            bar.name(messages.duringVote().currentVote(yes, no));
        updateAll();
        if (countDown <= 10) timer = Timer.FINAL;
        scheduler.runTaskLater(this, 20);
    }

    /* The stage for when everyone has voted. Sets the boss bar and moves onto the next stage. */
    private void doInterrupt() {
        countDown = 0;
        bar.progress(1.0f);
        bar.name(messages.afterVote().allPlayersHaveVoted());
        bar.color(BossBar.Color.YELLOW);

        timer = Timer.COMPLETE;
        scheduler.runTaskLater(this, 20);
    }

    /* The last 10 seconds of the vote. Boss bar alternates white and purple and players receive a message. */
    private void doFinal() {
        countDown--;
        if (yes + no + idle + away == playerCount) timer = Timer.INTERRUPT;
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
        scheduler.runTaskLater(this, 10);
    }

    /* The stage of the vote after the timer has run out. Displays vote passed/failed via boss bar and message.
    *  Initiates a fast-forward to the correct time. */
    private void doComplete() {
        countDown--;
        if (countDown == -1) {
            bar.progress(1.0f);
            if (yes > no) {
                bar.name(messages.afterVote().votePassedBossBar());
                bar.color(BossBar.Color.GREEN);
                updateAll(messages.afterVote().votePassedBossBar(voteTypeString()));
                fastForward = new FastForward(worldAdapter, world, scheduler, voteType);
                scheduler.runTaskLater(fastForward, 10);

                // Set boss bar progress to fast-forward progress
                bar.progress(0.0f);
                scheduler.runTaskTimer(new ScheduledRunnable() {
                    @Override
                    public void run() {
                        float nightEnd = 23900f;
                        float dayEnd = 12516f;
                        float currentTime = worldAdapter.getTime(world);

                        switch (voteType) {
                            case NIGHT:
                                if (currentTime >= dayEnd && currentTime <= nightEnd)
                                    bar.progress(currentTime / nightEnd);
                                else {
                                    bar.progress(1.0f);
                                    this.cancel();
                                }
                                break;
                            case DAY:
                                if (currentTime >= nightEnd || currentTime <= dayEnd)
                                    bar.progress(currentTime / dayEnd);
                                else {
                                    bar.progress(1.0f);
                                    this.cancel();
                                }
                                break;
                        }
                    }
                }, 0, 1);

                //if (world.hasStorm()) world.setStorm(false);
            }
            else {
                bar.name(messages.afterVote().voteFailedBossBar());
                bar.color(BossBar.Color.RED);
                updateAll(messages.afterVote().voteFailedBossBar(voteTypeString()));
            }
            scheduler.runTaskLater(this, 20);
        }

        if (countDown <= -2) scheduler.runTaskLater(this, 20);

        if (countDown <= -9 && bar.progress() == 1.0f) {
            messagesAdapter.hideBossBarAll(bar);
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
        if (countDown >= (config.getCooldown() * -1) - 9) scheduler.runTaskLater(this, 20);
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

        if (countDown > -4) scheduler.runTaskLater(this, 20);

        if (countDown == -4) {
            messagesAdapter.hideBossBarAll(bar);
            bar = null;
            voters = null;
            fastForward = null;
            voteType = null;
            timer = Timer.OFF;
        }
    }

    public void addYes(UUID playerUUID, VoteType voteType) {
        if (timer != Timer.OFF) {
            Voter voter = new Voter(playerUUID);
            if (voters.containsKey(voter.getUuid())) {
                voter = voters.get(voter.getUuid());
                if (voter.getVote() == 0) {
                    if (this.voteType == VoteType.NIGHT && playerAdapter.playerMustSleep(playerUUID) && config.isPhantomSupport()) {
                        messagesAdapter.sendMessage(playerUUID, messages.beforeVote().mustSleep());
                        actionBarMessage(messages.duringVote().playerHasNotSlept(playerAdapter.getName(playerUUID)));
                    }
                    else {
                        voter.voteYes();
                        messagesAdapter.sendMessage(playerUUID, messages.duringVote().youVoteYes());
                        actionBarMessage(messages.duringVote().playerHasVotedYes(playerAdapter.getName(playerUUID)));
                    }
                }
                else messagesAdapter.sendMessage(playerUUID, messages.duringVote().alreadyVoted());
            }
        }
        else messagesAdapter.sendMessage(playerUUID, messages.beforeVote().noVoteInProg(voteTypeCommandString(voteType)));
    }

    public void addNo(UUID playerUUID, VoteType voteType) {
        if (timer != Timer.OFF) {
            Voter voter = new Voter(playerUUID);
            if (voters.containsKey(voter.getUuid())) {
                voter = voters.get(voter.getUuid());
                if (voter.getVote() == 0) {
                    if (this.voteType == VoteType.NIGHT && playerAdapter.playerMustSleep(playerUUID) && config.isPhantomSupport()) {
                        messagesAdapter.sendMessage(playerUUID, messages.beforeVote().mustSleep());
                        actionBarMessage(messages.duringVote().playerHasNotSlept(playerAdapter.getName(playerUUID)));
                    }
                    else {
                        voter.voteNo();
                        messagesAdapter.sendMessage(playerUUID, messages.duringVote().youVoteNo());
                        actionBarMessage(messages.duringVote().playerHasVotedNo(playerAdapter.getName(playerUUID)));
                    }
                }
                else messagesAdapter.sendMessage(playerUUID, messages.duringVote().alreadyVoted());
            }
        }
        else messagesAdapter.sendMessage(playerUUID, messages.beforeVote().noVoteInProg(voteTypeCommandString(voteType)));
    }

    // Attempts to start a vote if all conditions are met, otherwise informs player why vote can't start
    public void start(UUID playerUUID, VoteType voteType) {
        UUID worldUUID = playerAdapter.getWorldUUID(playerUUID);

        if (!playerAdapter.hasPermission(playerUUID,"skipnight.vote." + voteTypeCommandString(voteType))) // If player doesn't have permission
            messagesAdapter.sendMessage(playerUUID, messages.general().noPerm());
        else if (config.getWorldBlacklist().contains(worldAdapter.getName(worldUUID))) // If world is blacklisted
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().worldIsBlacklisted());
        else if (!playerAdapter.isInOverworld(playerUUID)) // If player isn't in the overworld
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().worldNotOverworld());
        else if (voteType == VoteType.NIGHT && worldAdapter.getTime(worldUUID) < 12516 && !worldAdapter.hasStorm(worldUUID)) // If it's day and not raining, trying to skip night
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().canOnlyVoteAtNight());
        else if (voteType == VoteType.DAY && worldAdapter.getTime(worldUUID) >= 12516) // If it's night, trying to skip day
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().canOnlyVoteAtDay());
        else if (playerAdapter.readTag(playerUUID).equalsIgnoreCase("Idle"))
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().noVoteWhileIdle());
        else if (playerAdapter.readTag(playerUUID).equalsIgnoreCase("Away"))
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().noVoteWhileAway());
        else if (timer == Timer.COOLDOWN) // If the vote is in cooldown
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().cooldown());
        else if (!(timer == Timer.OFF)) // If there's a vote happening
            messagesAdapter.sendMessage(playerUUID, messages.duringVote().voteInProg());
        else if (voteType == VoteType.NIGHT && playerAdapter.playerMustSleep(playerUUID) && config.isPhantomSupport()) // If it's night, player hasn't slept in 3 days
            messagesAdapter.sendMessage(playerUUID, messages.beforeVote().mustSleepNewVote());
        else {
            timer = Timer.INIT;
            this.voteType = voteType;
            this.voteInitiator = playerUUID;
            world = playerAdapter.getWorldUUID(playerUUID);
            run();
        }
    }

    private void updateAll() {
        updateAll(null);
    }
    private void updateAll(Component message) {
        for (UUID playerUUID : playerAdapter.getOnlinePlayers()) {
            Voter voter = new Voter(playerUUID);

            List<Component> messageList = new ArrayList<>();

            // Check permission
            if (playerAdapter.hasPermission(playerUUID, "skipnight.vote." + voteTypeCommandString(voteType))) {
                if (playerAdapter.isInOverworld(playerUUID)) {
                    messagesAdapter.showBossBar(playerUUID, bar);

                    // Add voter to list
                    if (!voters.containsKey(voter.getUuid())) {
                        voters.put(voter.getUuid(), voter);
                        // Add message
                        messageList.add(messages.duringVote().voteStarted(playerAdapter.getName(voteInitiator), voteTypeString()));
                    }
                    // Or get from the list
                    else voter = voters.get(voter.getUuid());

                    // Started the vote, automatically vote yes
                    if (playerUUID.equals(voteInitiator) && timer == Timer.INIT) {
                        messageList.add(messages.duringVote().youVoteYes());
                        voter.voteYes();
                    }

                    switch (playerAdapter.readTag(playerUUID)) {
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
                                // In bed, auto vote yes (only if voter hasn't voted)
                                if (voter.getVote() == 0) {
                                    messageList.add(messages.duringVote().inBedVotedYes());
                                    voter.voteYes();
                                }
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
                }
                // Not in Overworld
                else {
                    if (voters.containsKey(voter.getUuid())) {
                        messageList.add(messages.duringVote().leftWorld());
                        voters.remove(voter.getUuid());
                    }
                }
                // Send messages
                for (Component messageToSend : messageList) {
                    messagesAdapter.sendMessage(playerUUID, messageToSend);
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
        for (UUID playerUUID : playerAdapter.getOnlinePlayers()) {
            Voter voter = new Voter(playerUUID);

            if (playerAdapter.isInOverworld(playerUUID) && playerAdapter.hasPermission(playerUUID, "skipnight.vote." + voteTypeCommandString(voteType))) {
                if (voters.containsKey(voter.getUuid())) {
                    messagesAdapter.sendActionBar(playerUUID, message);
                }
            }
        }
    }

    private boolean voteCancel() {
        return (voteType == VoteType.NIGHT && (worldAdapter.getTime(world) > 23900 || worldAdapter.getTime(world) < 12516)) && !worldAdapter.hasStorm(world) ||
                (voteType == VoteType.DAY && worldAdapter.getTime(world) > 12516 && worldAdapter.getTime(world) < 23900);
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
