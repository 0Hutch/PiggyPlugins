package com.polyplugins.Dialogue;


import net.runelite.client.config.*;

@ConfigGroup("DialogueContinuerConfig")
public interface DialogueContinuerConfig extends Config {

    @ConfigItem(
            keyName = "info",
            name = "Information",
            description = "",
            position = 0
    )
    default String info() {
        return "Dialogue continuer works with quest helper.\n\n" +
                "Dismiss random will check for random npcs that are targeting you and reachable every 2 ticks and dismiss them." +
                "\n\nOnly unnoted empty vials and jugs are dropped";
    }

    @ConfigItem(
            name = "Continue Dialogue",
            description = "",
            position = 20,
            keyName = "continueDialogue")
    default boolean continueDialogue() {
        return true;
    }

    @ConfigItem(
            name = "Dismiss Randoms",
            description = "",
            position = 30,
            keyName = "dismissRandoms")
    default boolean dismissRandoms() {
        return true;
    }

    //boolean drop empty vials
    //boolean drop empty wine jugs

    @ConfigItem(
            name = "Drop Empty Vials",
            description = "",
            position = 40,
            keyName = "dropEmptyVials")
    default boolean dropEmptyVials() {
        return true;
    }

    @ConfigItem(
            name = "Drop Empty Wine Jugs",
            description = "",
            position = 50,
            keyName = "dropEmptyWineJugs")
    default boolean dropEmptyWineJugs() {
        return true;
    }

}

