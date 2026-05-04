package net.mattlabs.skipnight.impl.paper_1_20_6.adapters;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.skipnight.api.core.Vote;
import net.mattlabs.skipnight.impl.base.adapters.BaseCommandAdapter;
import net.mattlabs.skipnight.impl.paper_1_20_6.commands.Paper1206SkipDayCommand;
import net.mattlabs.skipnight.impl.paper_1_20_6.commands.Paper1206SkipNightCommand;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;

public class Paper1206CommandAdapter extends BaseCommandAdapter {

    private PaperCommandManager<Source> commandManager;
    private final Plugin plugin;

    public Paper1206CommandAdapter(Plugin plugin, BukkitAudiences platform) {
        super(plugin, platform);
        this.plugin = plugin;
    }

    @Override
    public void registerFramework() {
        commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(plugin);

        // Override exception handlers
        MinecraftExceptionHandler.create(Source::source).defaultHandlers().registerTo(commandManager);
    }

    @Override
    public void registerSkipNightCommand(Vote vote) {
        new Paper1206SkipNightCommand(commandManager, vote);
    }

    @Override
    public void registerSkipDayCommand(Vote vote) {
        new Paper1206SkipDayCommand(commandManager, vote);
    }
}
