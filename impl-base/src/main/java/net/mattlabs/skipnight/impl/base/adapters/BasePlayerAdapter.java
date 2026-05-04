package net.mattlabs.skipnight.impl.base.adapters;

import net.mattlabs.skipnight.api.player.PlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasePlayerAdapter implements PlayerAdapter {

    private Plugin plugin;

    public BasePlayerAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName(UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID).getName();
    }

    @Override
    public String readTag(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        // Read players tag, null if not there
        try {
            return player.getPlayerListName().split("#")[1];
        } catch (IndexOutOfBoundsException e) {
            return player.isSleeping() ? "Bed" : "Active";
        }
    }

    @Override
    public UUID getWorldUUID(UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID).getWorld().getUID();
    }

    @Override
    public boolean hasPermission(UUID playerUUID, String permission) {
        return Bukkit.getPlayer(playerUUID).hasPermission(permission);
    }

    @Override
    public boolean isInOverworld(UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID).getWorld().getEnvironment() == World.Environment.NORMAL;
    }

    @Override
    public boolean playerMustSleep(UUID playerUUID) {
        // TIME_SINCE_REST added in 1.13 not available in 1.8, player never needs to sleep
        return false;
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
    }
}
