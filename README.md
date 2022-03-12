SkipNight is a simple plugin that allows players to skip the night via a vote.
Once a vote is started, click the **yes** and **no** interactive text in chat to vote.

SpigotMC link: https://www.spigotmc.org/resources/skipnight.48334/

[![Build Status](https://travis-ci.org/mattboy9921/SkipNight.svg?branch=master)](https://travis-ci.org/mattboy9921/SkipNight)
[![Servers Using SkipNight](https://img.shields.io/bstats/servers/5796?style=flat&label=Servers&logo=bookmeter&logoColor=94A0A5&labelColor=384142&color=00695C)](https://bstats.org/plugin/bukkit/SkipNight/5796)
[![Players Using SkipNight](https://img.shields.io/bstats/players/5796?style=flat&label=Players&logo=bookmeter&logoColor=94A0A5&labelColor=384142&color=00695C)](https://bstats.org/plugin/bukkit/SkipNight/5796)
![SkipNight Downloads](https://img.shields.io/github/downloads/mattboy9921/skipnight/total?label=Downloads&logo=docusign&logoColor=94A0A5&labelColor=384142)
[![SkipNight Latest Release](https://img.shields.io/github/v/release/mattboy9921/skipnight?label=Release&logo=dropbox&logoColor=94A0A5&labelColor=384142)](https://github.com/mattboy9921/CrewChat/releases/latest)
![SkipNight Tested Versions](https://img.shields.io/badge/Tested%20Versions-1.8.0--1.18.0-success?&logo=verizon&logoColor=94A0A5&labelColor=384142)
![SkipNight Made with Love](https://img.shields.io/badge/Made-with%20Love-red?&logo=undertale&logoColor=94A0A5&labelColor=384142)

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
 
Tested working on Paper 1.8.0 - 1.18.0.

Special thanks to RoyCurtis, iamliammckimm, CRX VrynzX, Scarsz, Aikar, mbaxter, zml, Selida and ViMaSter! 