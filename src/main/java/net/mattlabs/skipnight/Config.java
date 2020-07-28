package net.mattlabs.skipnight;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Config {

    @Setting(comment = "Skipnight Configuration\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/SkipNight")
    private boolean mattIsAwesome = true;

    @Setting(value = "skipnight", comment = "\nEnables voting to skip the night.")
    private boolean skipNight = true;

    @Setting(value = "skipday", comment = "\nEnables voting to skip the day.")
    private boolean skipDay = false;

    public boolean isSkipNight() {
        return skipNight;
    }

    public boolean isSkipDay() {
        return skipDay;
    }
}
