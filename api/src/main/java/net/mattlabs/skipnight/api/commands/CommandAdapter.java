package net.mattlabs.skipnight.api.commands;

import net.mattlabs.skipnight.api.core.Vote;

public interface CommandAdapter {

    void registerFramework();

    void registerSkipNightCommand(Vote vote);

    void registerSkipDayCommand(Vote vote);
}
