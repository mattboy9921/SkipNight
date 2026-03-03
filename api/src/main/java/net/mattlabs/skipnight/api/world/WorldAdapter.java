package net.mattlabs.skipnight.api.world;

import java.util.List;
import java.util.UUID;

public interface WorldAdapter {

    String getName(UUID worldUUID);

    long getTime(UUID worldUUID);

    List<UUID> getPlayers(UUID worldUUID);

    boolean hasStorm(UUID worldUUID);

    void setTime(UUID worldUUID, long time);

    void setStorm(UUID worldUUID, boolean hasStorm);
}
