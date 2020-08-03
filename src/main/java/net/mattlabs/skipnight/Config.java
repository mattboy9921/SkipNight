package net.mattlabs.skipnight;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Config {

    @Setting(comment = "Skipnight Configuration\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/SkipNight")
    private boolean _mattIsAwesome = true;

    @Setting(value = "skipnight", comment = "\nEnables voting to skip the night.")
    private boolean skipNight = true;

    @Setting(value = "skipday", comment = "\nEnables voting to skip the day.")
    private boolean skipDay = false;

    @Setting(value = "phantom-support", comment = "\nPrevents players from voting if they haven't slept in 3 days.\n" +
            "Basically, if a player will be attacked by phantoms, they cannot vote.")
    private boolean phantomSupport = true;

    public boolean isSkipNight() {
        return skipNight;
    }

    public boolean isSkipDay() {
        return skipDay;
    }

    public boolean isPhantomSupport() {
        return phantomSupport;
    }
}
