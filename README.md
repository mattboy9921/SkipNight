SkipNight is a simple plugin that allows players to skip the night via a vote.
Once a vote is started, click the **yes** and **no** interactive text in chat to vote.

SpigotMC link: https://www.spigotmc.org/resources/skipnight.48334/

[![Build Status](https://travis-ci.org/mattboy9921/SkipNight.svg?branch=master)](https://travis-ci.org/mattboy9921/SkipNight)
[![Servers Using SkipNight](https://img.shields.io/bstats/servers/5796)](https://bstats.org/plugin/bukkit/SkipNight/5796)

**Commands**
 - `/skipnight` - Starts vote to skip the night.
 - `/skipday` - Starts a vote to skip the day.
 
**Permissions**
 - `skipnight.vote.day` - Allows player to start vote to skip day, vote and be counted in vote.
 - `skipnight.vote.night` - Allows player to start vote to skip night, vote and be counted in vote.
 
 **Configuration (`config.conf`)**
 - `skipnight` - Set to `true` to allow skipping the night.
 - `skipday` - Set to `true` to allow skipping the day.
 - `phantom-support` - Set to `true` to prevent players from voting if phantoms are attacking them.
 
 **Messages Configuration (`messages.conf`)**
 
 The messages configuration contains every string of text found in the plugin. 
 Follow the instructions in the messages configuration to modify these for your needs.
 
Tested working on Paper 1.9.0 - 1.16.1.

Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz, Aikar, mbaxter, zml and Selida! 