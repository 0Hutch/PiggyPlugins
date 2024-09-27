

package com.polyplugins.BobTheBuilder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("BobTheBuilder")
public interface BobTheBuilderConfig extends Config {
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
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
        return "Lee esto. \n\nEres un idiota si pagaste esto por algun grupo de whatsapp o facebook por este plugin. \n\nEsto esta editado por El Guason user de discord factord.crypto\n\nCanal de Youtube https://www.youtube.com/@El Guason  \n\nCanal de Discord https://discord.com/invite/URXjtjambp";
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
            name = "Opcion",
            keyName = "option",
            description = "",
            position = 4
    )
    default int option() {
        return -1;
    }

    @ConfigItem(
            name = "Items",
            keyName = "items",
            description = "",
            position = 4
    )
    default String items() {
        return "Oak plank";
    }

    @ConfigItem(
            name = "Construir",
            keyName = "build",
            description = "",
            position = 4
    )
    default String build() {
        return "Larder";
    }

    @ConfigItem(
            name = "Remover",
            keyName = "remove",
            description = "",
            position = 4
    )
    default String remove() {
        return "Larder";
    }

    @ConfigItem(
            name = "Tick Delay Min",
            keyName = "tickDelayMin",
            description = "",
            position = 4,
            section = "Tick Delays"
    )
    default int tickdelayMin() {
        return 0;
    }

    @ConfigItem(
            name = "Tick Delay Max",
            keyName = "tickDelayMax",
            description = "",
            position = 5,
            section = "Tick Delays"
    )
    default int tickDelayMax() {
        return 3;
    }
}
