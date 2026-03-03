package net.mattlabs.skipnight.impl_current;

import net.mattlabs.skipnight.api.commands.CommandAdapter;
import net.mattlabs.skipnight.api.core.Vote;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class CurrentCommandAdapter implements CommandAdapter {

    private LegacyPaperCommandManager<CommandSender> commandManager;
    private Plugin plugin;

    public CurrentCommandAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerFramework() {
        commandManager = LegacyPaperCommandManager.createNative(
                plugin,
                ExecutionCoordinator.coordinatorFor(ExecutionCoordinator.nonSchedulingExecutor())
        );
        // Register Brigadier, fallback to asynchronous completions
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) commandManager.registerBrigadier();
        else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) commandManager.registerAsynchronousCompletions();
    }

    @Override
    public void registerSkipNightCommand(Vote vote) {
        new CurrentSkipNightCommand(commandManager, vote);
    }

    @Override
    public void registerSkipDayCommand(Vote vote) {
        new CurrentSkipDayCommand(commandManager, vote);
    }
}
