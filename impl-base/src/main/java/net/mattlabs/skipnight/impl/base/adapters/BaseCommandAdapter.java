package net.mattlabs.skipnight.impl.base.adapters;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.commands.CommandAdapter;
import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.impl.base.commands.BaseSkipDayCommand;
import net.mattlabs.skipnight.impl.base.commands.BaseSkipNightCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class BaseCommandAdapter implements CommandAdapter {

    private LegacyPaperCommandManager<CommandSender> commandManager;
    private final Plugin plugin;
    private final BukkitAudiences platform;

    public BaseCommandAdapter(Plugin plugin, BukkitAudiences platform) {
        this.plugin = plugin;
        this.platform = platform;
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

        // Override exception handlers
        MinecraftExceptionHandler.create(platform::sender).defaultHandlers().registerTo(commandManager);
    }

    @Override
    public void registerSkipNightCommand(Vote vote) {
        new BaseSkipNightCommand(commandManager, vote);
    }

    @Override
    public void registerSkipDayCommand(Vote vote) {
        new BaseSkipDayCommand(commandManager, vote);
    }
}
