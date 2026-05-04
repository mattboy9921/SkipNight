package net.mattlabs.skipnight.impl.base.events;

import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.api.events.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BaseEventListener implements Listener, EventListener {

    private final Vote vote;

    public BaseEventListener(Vote vote) {
        this.vote = vote;
    }

    @EventHandler
    public void onLogOff(PlayerQuitEvent event) {
        vote.onLogOffEvent(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        vote.onBedEnterEvent(event.getPlayer().getUniqueId());
    }
}
