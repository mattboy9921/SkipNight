package net.mattlabs.skipnight.impl_current;

import net.mattlabs.skipnight.api.world.WorldAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CurrentWorldAdapter implements WorldAdapter {

    @Override
    public String getName(UUID worldUUID) {
        return Bukkit.getWorld(worldUUID).getName();
    }

    @Override
    public long getTime(UUID worldUUID) {
        return Bukkit.getWorld(worldUUID).getTime();
    }

    @Override
    public List<UUID> getPlayers(UUID worldUUID) {
        return Bukkit.getWorld(worldUUID).getPlayers().stream().map(Player::getUniqueId).toList();
    }

    @Override
    public boolean hasStorm(UUID worldUUID) {
        return Bukkit.getWorld(worldUUID).hasStorm();
    }

    @Override
    public void setTime(UUID worldUUID, long time) {
        Bukkit.getWorld(worldUUID).setTime(time);
    }

    @Override
    public void setStorm(UUID worldUUID, boolean hasStorm) {
        Bukkit.getWorld(worldUUID).setStorm(hasStorm);
    }
}
