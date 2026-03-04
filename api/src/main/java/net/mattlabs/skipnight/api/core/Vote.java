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

/**
 * Represents a single active vote to skip the night or day in a specific world.
 *
 * <p>A {@code Vote} instance manages the full lifecycle of a vote, including:</p>
 * <ul>
 *     <li>Initialization and countdown timer stages</li>
 *     <li>Tracking player votes and activity states (active, bed, idle, away)</li>
 *     <li>Displaying progress and results via a {@link BossBar}</li>
 *     <li>Fast-forwarding the world time when a vote passes</li>
 *     <li>Handling interruptions, cancellations, and cooldowns</li>
 * </ul>
 *
 * <p>This class is designed to be scheduled periodically via a {@link Scheduler}
 * using the {@link ScheduledRunnable} abstraction, advancing through internal
 * {@link Timer} stages to perform its logic.</p>
 */
public class Vote extends ScheduledRunnable {

    /**
     * Internal timer state machine representing the lifecycle phases of a vote.
     */
    enum Timer {
        /**
         * Initial setup state for a new vote.
         */
        INIT,
        /**
         * Normal operation state, counting down the vote timer.
         */
        OPERATION,
        /**
         * State reached when all players have voted before the timer ends.
         */
        INTERRUPT,
        /**
         * State used when the vote is cancelled because the target time has already been reached.
         */
        CANCEL,
        /**
         * Final countdown state (last 10 seconds of the vote).
         */
        FINAL,
        /**
         * State where the vote result is processed and displayed, and time is fast-forwarded if it passed.
         */
        COMPLETE,
        /**
         * Cooldown state preventing new votes for a configured duration.
         */
        COOLDOWN,
        /**
         * Idle state indicating no active vote.
         */
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

    /**
     * Creates a new {@code Vote} controller.
     *
     * @param messages       the message templates used during all stages of the vote
     * @param playerActivity {@code true} if player activity (idle/away) should be considered in the vote display
     * @param config         the loaded configuration used for durations, cooldowns, and phantom behavior
     * @param scheduler      the scheduler used to run this vote and related tasks
     * @param playerAdapter  abstraction for player-related queries (permissions, world, tags, etc.)
     * @param worldAdapter   abstraction for world-related queries (time, weather, players, etc.)
     * @param messagesAdapter abstraction for sending messages, action bars, and boss bars to players
     */
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

    /**
     * Handles a player logoff event during an active vote.
     *
     * <p>If a vote is in progress and the player has permission to participate in
     * the current {@link VoteType}, they are removed from the internal voter map.</p>
     *
     * @param playerUUID the UUID of the player who logged off
     */
    public void onLogOffEvent(UUID playerUUID) {
        if (timer != Timer.OFF)
            if (playerAdapter.hasPermission(playerUUID, "skipnight.vote." + voteTypeCommandString(voteType))) {
                voters.remove(playerUUID);
            }
    }

    /**
     * Handles a player entering a bed when no vote is currently active.
     *
     * <p>If the player meets the conditions to start a night vote (has permission,
     * world has more than one player, it is night or storming, and no vote is active),
     * they are informed that there is no vote in progress.</p>
     *
     * @param playerUUID the UUID of the player who entered a bed
     */
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

    /**
     * Advances the vote state machine by one step based on the current {@link Timer} value.
     *
     * <p>This method is designed to be scheduled periodically via the {@link Scheduler}.
     * Each call delegates to a private handler method corresponding to the current timer
     * state and may reschedule itself for future execution.</p>
     */
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

    /**
     * The first stage of a vote. Initializes internal state and the boss bar, and
     * informs all eligible players that a vote has started.
     *
     * <p>After initialization, the timer transitions to {@link Timer#OPERATION}
     * and the next run is scheduled.</p>
     */
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

    /**
     * The main operation stage of the vote.
     *
     * <p>Decrements the countdown, updates the boss bar, checks for early completion
     * or cancellation, and transitions to {@link Timer#FINAL} when the vote enters
     * its last 10 seconds.</p>
     */
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

    /**
     * Stage reached when all players have voted before the timer expires.
     *
     * <p>Sets the boss bar to a final "all players have voted" state and transitions
     * to {@link Timer#COMPLETE}.</p>
     */
    private void doInterrupt() {
        countDown = 0;
        bar.progress(1.0f);
        bar.name(messages.afterVote().allPlayersHaveVoted());
        bar.color(BossBar.Color.YELLOW);

        timer = Timer.COMPLETE;
        scheduler.runTaskLater(this, 20);
    }

    /**
     * The final seconds of the vote.
     *
     * <p>Handles the last 10 seconds of the countdown, alternating the boss bar color and
     * sending a notification when only 10 seconds remain. If all players vote or the
     * vote is cancelled, transitions accordingly. Otherwise, moves to
     * {@link Timer#COMPLETE} when the timer reaches zero.</p>
     */
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

    /**
     * Completion stage that processes the result of the vote, displays the outcome,
     * and (if passed) initiates a fast-forward of the world time.
     *
     * <p>Also manages the boss bar fade-out and eventual transition to either
     * {@link Timer#OFF} or {@link Timer#COOLDOWN} depending on the vote result.</p>
     */
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

    /**
     * Cooldown stage that prevents a new vote from starting for the configured
     * cooldown duration after a failed vote.
     *
     * <p>Once the cooldown has elapsed, the timer transitions back to {@link Timer#OFF}.</p>
     */
    private void doCooldown() {
        countDown--;
        if (countDown >= (config.getCooldown() * -1) - 9) scheduler.runTaskLater(this, 20);
        else timer = Timer.OFF;
    }

    /**
     * Cancellation stage used when the target time is reached during the vote.
     *
     * <p>Switches the boss bar to a blue "already day/night" state, then hides and
     * clears internal state before returning to {@link Timer#OFF}.</p>
     */
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

    /**
     * Registers a "yes" vote for the given player, if a vote is active and they are eligible.
     *
     * <p>If the player is required to sleep (phantom support) and the vote is for night,
     * the vote is blocked and the player is informed. Otherwise, their vote is recorded
     * if they have not already voted.</p>
     *
     * @param playerUUID the UUID of the player voting
     * @param voteType   the type of vote the player attempted to cast (day or night)
     */
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

    /**
     * Registers a "no" vote for the given player, if a vote is active and they are eligible.
     *
     * <p>If the player is required to sleep (phantom support) and the vote is for night,
     * the vote is blocked and the player is informed. Otherwise, their vote is recorded
     * if they have not already voted.</p>
     *
     * @param playerUUID the UUID of the player voting
     * @param voteType   the type of vote the player attempted to cast (day or night)
     */
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

    /**
     * Attempts to start a new vote for the given player and {@link VoteType}.
     *
     * <p>This method checks all preconditions for starting a vote, including permissions,
     * world blacklist, dimension, time of day, phantom sleep requirements, and cooldown
     * state. If any condition fails, the player is informed with an appropriate message.
     * If all conditions are met, the internal state is initialized and the vote is started.</p>
     *
     * @param playerUUID the UUID of the player attempting to start the vote
     * @param voteType   the type of vote to start (skip night or skip day)
     */
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

    /**
     * Updates all eligible players with the current boss bar and any additional message.
     */
    private void updateAll() {
        updateAll(null);
    }

    /**
     * Updates all eligible players with the current boss bar and an optional message.
     *
     * <p>Responsible for:</p>
     * <ul>
     *     <li>Ensuring players with permission and in the Overworld see the boss bar</li>
     *     <li>Tracking and updating each {@link Voter}'s state (active, bed, idle, away)</li>
     *     <li>Auto-voting for players when they start the vote or are in bed</li>
     *     <li>Sending appropriate per-player messages based on their state changes</li>
     *     <li>Recalculating vote counts and totals</li>
     * </ul>
     *
     * @param message an additional message to send to each eligible player, or {@code null} for none
     */
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

    /**
     * Sends an action bar message to all eligible players currently participating in the vote.
     *
     * @param message the action bar message to send
     */
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

    /**
     * Determines whether the current vote should be cancelled because the
     * target time has already been reached in the world.
     *
     * @return {@code true} if the vote should be cancelled, {@code false} otherwise
     */
    private boolean voteCancel() {
        return (voteType == VoteType.NIGHT && (worldAdapter.getTime(world) > 23900 || worldAdapter.getTime(world) < 12516)) && !worldAdapter.hasStorm(world) ||
                (voteType == VoteType.DAY && worldAdapter.getTime(world) > 12516 && worldAdapter.getTime(world) < 23900);
    }

    /**
     * Returns the configured display string for the current {@link VoteType}.
     *
     * @return the display string for the current vote type (e.g. "day" or "night" text from {@link Messages})
     */
    public String voteTypeString() {
        return voteTypeString(this.voteType);
    }

    /**
     * Returns the configured display string for a given {@link VoteType}.
     *
     * @param voteType the vote type to convert to a display string
     * @return the display string for the given vote type
     */
    public String voteTypeString(VoteType voteType) {
        return switch (voteType) {
            case DAY -> messages.getDayString();
            case NIGHT -> messages.getNightString();
        };
    }

    /**
     * Returns the command keyword string for the given {@link VoteType}.
     *
     * <p>This is used in permission and command names (e.g. {@code "day"} or {@code "night"}).</p>
     *
     * @param voteType the vote type to convert to a command string
     * @return the command string for the given vote type, such as {@code "day"} or {@code "night"}
     */
    public String voteTypeCommandString(VoteType voteType) {
        return switch (voteType) {
            case DAY -> "day";
            case NIGHT -> "night";
        };
    }
}
