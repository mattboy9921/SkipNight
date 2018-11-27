package net.mattlabs.skipnight;

import net.mattlabs.skipnight.commands.SkipNightCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SkipNight extends JavaPlugin {

    private static SkipNight instance;
    private SkipNightCommand skipNightCommand;
    private Vote vote;

    public void onEnable() {
        instance = this;
        vote = new Vote(this);

        // Register Command
        skipNightCommand = new SkipNightCommand();
        getCommand("skipnight").setExecutor(skipNightCommand);

        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, and Scarsz)");
    }

    public static SkipNight getInstance() {
        return instance;
    }

    public Vote getVote() {
        return vote;
    }
}
