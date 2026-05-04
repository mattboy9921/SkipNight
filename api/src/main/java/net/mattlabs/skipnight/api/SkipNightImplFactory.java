package net.mattlabs.skipnight.api;

import net.mattlabs.skipnight.api.commands.CommandAdapter;
import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.api.events.EventListener;
import net.mattlabs.skipnight.api.messaging.MessagesAdapter;
import net.mattlabs.skipnight.api.player.PlayerAdapter;
import net.mattlabs.skipnight.api.scheduler.Scheduler;
import net.mattlabs.skipnight.api.world.WorldAdapter;

public interface SkipNightImplFactory {

    Scheduler scheduler();
    PlayerAdapter playerAdapter();
    WorldAdapter worldAdapter();
    MessagesAdapter messagesAdapter();
    EventListener eventListener(Vote vote);
    CommandAdapter commandAdapter();
}
