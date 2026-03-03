package net.mattlabs.skipnight.api.player;

import java.util.Collection;
import java.util.UUID;

public interface PlayerAdapter {

    String getName(UUID playerUUID);

    String readTag(UUID playerUUID);

    UUID getWorldUUID(UUID playerUUID);

    boolean hasPermission(UUID playerUUID, String permission);

    boolean isInOverworld(UUID playerUUID);

    boolean playerMustSleep(UUID playerUUID);

    Collection<UUID> getOnlinePlayers();
}
