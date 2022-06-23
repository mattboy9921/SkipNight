package net.mattlabs.skipnight;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Config {

    @Setting(value = "_mattIsAwesome")
    @Comment("Skipnight Configuration\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/SkipNight")
    private boolean _mattIsAwesome = true;

    @Setting(value = "skipnight")
    @Comment("\nEnables voting to skip the night.")
    private boolean skipNight = true;

    @Setting(value = "skipday")
    @Comment("\nEnables voting to skip the day.")
    private boolean skipDay = false;

    @Setting(value = "phantom-support")
    @Comment("\nPrevents players from voting if they haven't slept in 3 days.\n" +
            "Basically, if a player will be attacked by phantoms, they cannot vote.")
    private boolean phantomSupport = true;

    @Setting(value = "world-blacklist")
    @Comment("\nBlacklist of worlds that votes should not happen in.\n" +
            "Any world listed here will not allow voting to ship the night/day.")
    private ArrayList<String> worldBlacklist = new ArrayList<>(Collections.singletonList("example-world-1"));

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
}
