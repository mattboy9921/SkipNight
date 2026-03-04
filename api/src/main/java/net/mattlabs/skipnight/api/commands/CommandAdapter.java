package net.mattlabs.skipnight.api.commands;

import net.mattlabs.skipnight.api.core.Vote;

/**
 * Platform abstraction for registering SkipNight's commands and command framework.
 */
public interface CommandAdapter {

    /**
     * Registers and initializes the platform's command framework.
     *
     * <p>This is where the implementation will create a command manager that hooks into the plugin and registers extra
     * features like Brigadier or async command completions.</p>
     */
    void registerFramework();

    /**
     * Registers the {@code skipnight} command with the command framework.
     *
     * <p>This should include the commands:
     * <ul>
     *     <li>Skip night base command - {@code /skipnight} (alias {@code /sn}) - "Starts a vote to skip the night."</li>
     *     <li>Skip night yes command - {@code /skipnight yes} - "Votes yes for the current vote."</li>
     *     <li>Skip night no command - {@code /skipnight no} - "Votes no for the current vote."</li>
     * </ul></p>
     *
     * @param vote the instance of the vote used by SkipNight
     */
    void registerSkipNightCommand(Vote vote);

    /**
     * Registers the {@code skipday} command with the command framework.
     *
     * <p>This should include the commands:
     * <ul>
     *     <li>Skip day base command - {@code /skipday} (alias {@code /sd}) - "Starts a vote to skip the day."</li>
     *     <li>Skip day yes command - {@code /skipday yes} - "Votes yes for the current vote."</li>
     *     <li>Skip day no command - {@code /skipday no} - "Votes no for the current vote."</li>
     * </ul></p>
     *
     * @param vote the instance of the vote used by SkipNight
     */
    void registerSkipDayCommand(Vote vote);
}
