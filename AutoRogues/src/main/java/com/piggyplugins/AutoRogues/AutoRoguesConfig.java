package com.piggyplugins.AutoRogues;


import net.runelite.client.config.*;

@ConfigGroup("AutoRogues")
public interface AutoRoguesConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "Shortcut",
            name = "Shortcut",
            description = "Enable level 80+ shortcut? Will ignore if below level 80 Thieving.",
            position = 1
    )
    default boolean shortcut() { return true; }

    @ConfigItem(
            keyName = "ChillLower",
            name = "Lower Chill Threshold",
            description = "If below this amount of run energy, sit and idle until Upper Chill Threshold is met",
            position = 2
    )
    default int chillLower() { return 15; }

    @ConfigItem(
            keyName = "ChillUpper",
            name = "Upper Chill Threshold",
            description = "If below Lower Chill Threshold, sit and idle until this amount of run energy is met",
            position = 2
    )
    default int chillUpper() { return 25; }

    @ConfigItem(
            keyName = "DiscordAchievements",
            name = "Discord Webhook Enabled",
            description = "Send a message to the webhook",
            position = 3
    )
    default boolean discordAchievements() { return false; }

    @ConfigItem(
            keyName = "DiscordAchievementsName",
            name = "Discord Webhook Name",
            description = "Name to send to the Achievements webhook when we send it",
            position = 4
    )
    default String discordAchievementsName() { return "Anonymous"; }

    @ConfigItem(
            keyName = "DiscordAchievementsWebhook",
            name = "Discord Webhook Link",
            description = "",
            position = 5
    )
    default String discordAchievementsWebhook() { return "Place webhook Here if you want"; }
    @ConfigItem(
            keyName = "DiscordAchievementsScreenshot",
            name = "Discord Webhook Screenshots",
            description = "Include a screenshot with the webhook?",
            position = 6
    )
    default boolean discordAchievementsScreenshot() { return false; }
}
