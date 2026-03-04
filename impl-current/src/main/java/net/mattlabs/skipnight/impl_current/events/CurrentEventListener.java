package net.mattlabs.skipnight.impl_current.events;

import net.mattlabs.skipnight.api.core.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CurrentEventListener implements Listener {

    private final Vote vote;

    public CurrentEventListener(Vote vote) {
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
