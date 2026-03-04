package net.mattlabs.skipnight.api.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents SkipNight's configuration file, saved to the plugin data folder as {@code config.conf} with HOCON
 * formatting.
 *
 * <p>This class is serialized into the config file and deserialized from the config file via Configurate. On plugin
 * load, either the config is created using the default field values of this file, or they are set using the values
 * in the existing config file.</p>
 *
 * <p>The public methods of this class provide the config values once loaded.</p>
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Config {

    @SuppressWarnings("unused")
    @Setting(value = "_mattIsAwesome")
    @Comment("""
            Skipnight Configuration
            By Mattboy9921
            https://github.com/mattboy9921/SkipNight""")
    private boolean _mattIsAwesome = true;

    @Setting(value = "skipnight")
    @Comment("\nEnables voting to skip the night.")
    private boolean skipNight = true;

    @Setting(value = "skipday")
    @Comment("\nEnables voting to skip the day.")
    private boolean skipDay = false;

    @Setting(value = "phantom-support")
    @Comment("""

            Prevents players from voting if they haven't slept in 3 days.
            Basically, if a player will be attacked by phantoms, they cannot vote.""")
    private boolean phantomSupport = true;

    @Setting(value = "world-blacklist")
    @Comment("""

            Blacklist of worlds that votes should not happen in.
            Any world listed here will not allow voting to ship the night/day.""")
    private ArrayList<String> worldBlacklist = new ArrayList<>(Collections.singletonList("example-world-1"));

    @Setting(value = "cooldown")
    @Comment("""

            A cooldown value in seconds to prevent additional votes after a failed vote.
            Set to 0 to disable.""")
    private int cooldown = 30;

    @Setting(value = "disable-header")
    @Comment("""

            Set this to true to have the "[Vote]" header before plugin messages.
            Set to false to disable the header.""")
    private boolean disableHeader = false;

    @Setting(value = "vote-duration")
    @Comment("\nA value for the duration of the vote in seconds.")
    private int voteDuration = 30;

    /**
     * @return whether the function of skipping the night and {@code /skipnight} commands should be enabled
     */
    public boolean isSkipNight() {
        return skipNight;
    }

    /**
     * @return whether the function of skipping the day and {@code /skipday} commands should be enabled
     */
    public boolean isSkipDay() {
        return skipDay;
    }

    /**
     * @return whether voting to skip the night should prevent voting for players who haven't slept in 3 days
     * (if Phantoms will spawn for these players)
     */
    public boolean isPhantomSupport() {
        return phantomSupport;
    }

    /**
     * @return a list of world names where voting is disabled
     */
    public ArrayList<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    /**
     * @return the cooldown, in seconds, before another vote can begin after a failed vote
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * @return whether the "[Vote]" message header is disabled
     */
    public boolean isHeaderDisabled() {
        return disableHeader;
    }

    /**
     * @return the duration of a vote cycle in seconds
     */
    public int getVoteDuration() {
        return voteDuration;
    }
}
