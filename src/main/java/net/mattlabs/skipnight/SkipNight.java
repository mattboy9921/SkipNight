package net.mattlabs.skipnight;

import co.aikar.commands.PaperCommandManager;
import net.mattlabs.skipnight.commands.SkipNightCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SkipNight extends JavaPlugin {

    public Vote vote;
    public PaperCommandManager manager;

    public void onEnable() {
        vote = new Vote(this);

        // Register Commands with ACF
        manager = new PaperCommandManager(this);
        manager.registerCommand(new SkipNightCommand(this));

        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz and Aikar)");
    }
}
