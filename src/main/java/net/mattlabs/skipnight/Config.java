package net.mattlabs.skipnight;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Collections;

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

    public boolean isSkipNight() {
        return skipNight;
    }

    public boolean isSkipDay() {
        return skipDay;
    }

    public boolean isPhantomSupport() {
        return phantomSupport;
    }

    public ArrayList<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isHeaderDisabled() {
        return disableHeader;
    }

    public int getVoteDuration() {
        return voteDuration;
    }
}
