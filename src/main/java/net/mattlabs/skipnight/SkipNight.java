package net.mattlabs.skipnight;

import co.aikar.commands.PaperCommandManager;
import net.mattlabs.configmanager.ConfigManager;
import net.mattlabs.skipnight.commands.SkipDayCommand;
import net.mattlabs.skipnight.commands.SkipNightCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SkipNight extends JavaPlugin {

    public Vote vote;
    private PaperCommandManager manager;
    private ConfigManager configManager;

    public void onEnable() {
        vote = new Vote(this);

        // Configuration Section
        configManager = new ConfigManager(this);
        configManager.loadConfigFiles(
                new ConfigManager.ConfigPath(
                        "config.yml",
                        "config.yml",
                        "config.yml"));
        configManager.saveAllConfigs(false);
        configManager.updateConfig("config.yml");

        // Register Listeners
        getServer().getPluginManager().registerEvents(vote, this);

        // Register ACF
        manager = new PaperCommandManager(this);

        // Register Commands with ACF
        if (configManager.getFileConfig("config.yml").getBoolean("skipnight"))
            manager.registerCommand(new SkipNightCommand(this));
        if (configManager.getFileConfig("config.yml").getBoolean("skipday"))
            manager.registerCommand(new SkipDayCommand(this));

        getLogger().info("SkipNight loaded - By mattboy9921 (Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz, Aikar and Foodyling)");
    }
}
