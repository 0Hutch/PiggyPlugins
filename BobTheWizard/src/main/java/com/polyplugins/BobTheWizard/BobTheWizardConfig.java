package com.polyplugins.BobTheWizard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("BobTheWizard")
public interface BobTheWizardConfig extends Config {
    /*@ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )*/
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Tick Delays",
            description = "",
            position = 3
    )
    String tickDelaySection = "Tick Delays";

    /*@ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Lee esto. \n\nEres un idiota si pagaste esto por algun grupo de whatsapp o facebook por este plugin. \n\nEsto esta editado por ElKondo user de discord factord.crypto\n\nCanal de Youtube https://www.youtube.com/@ElKondo  \n\nCanal de Discord https://discord.com/invite/URXjtjambp";
    }*/

    @ConfigItem(
            keyName = "toggle",
            name = "Tecla",
            description = "",
            position = -2
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "teleport",
            name = "Teleport",
            description = "",
            position = -2
    )
    default Teleport teleport() {
        return Teleport.CAMELOT;
    }

    @ConfigItem(
            keyName = "trainingMethod",
            name = "Entrenar",
            description = "",
            position = -2
    )
    default TrainingMethod trainingMethod() {
        return TrainingMethod.TeleAlch;
    }

    @ConfigItem(
            keyName = "plank",
            name = "Plank",
            description = "",
            position = -2
    )
    default String plank() {
        return "";
    }

    @ConfigItem(
            keyName = "alc",
            name = "High Alc",
            description = "",
            position = -2
    )
    default String alc() {
        return "";
    }

    @ConfigItem(
            name = "Tick Delay Min",
            keyName = "tickDelayMin",
            description = "Lower bound of tick delay, can set both to 0 to remove delay",
            position = 4,
            section = "Tick Delays"
    )
    default int tickdelayMin() {
        return 0;
    }

    @ConfigItem(
            name = "Tick Delay Max",
            keyName = "tickDelayMax",
            description = "Upper bound of tick delay, can set both to 0 to remove delay",
            position = 5,
            section = "Tick Delays"
    )
    default int tickDelayMax() {
        return 1572864 >>> 12179 << (12451840 >>> 7563);
    }

    @ConfigItem(
            name = "Teleport Tick Delay Min",
            keyName = "teleportTickDelayMin",
            description = "Lower bound of tick delay, can set both to 0 to remove delay",
            position = 6,
            section = "Tick Delays"
    )
    default int teleportTickDelayMin() {
        return 0;
    }

    @ConfigItem(
            name = "Teleport Tick Delay Max",
            keyName = "teleportTickDelayMax",
            description = "Upper bound of tick delay, can set both to 0 to remove delay",
            position = 7,
            section = "Tick Delays"
    )
    default int teleportTickDelayMax() {
        return 1;
    }
}
