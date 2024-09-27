package com.example.AutoNMZ;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("AutoNMZ")
public interface AutoNMZConfiguration extends Config {
    String version = "v1.0";
    @ConfigSection(
            name = "Instrucciones",
            description = "Plugin Instrucciones.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";
    @ConfigSection(
            name = "Configuración",
            description = "Plugin configuración.",
            position = 5
    )
    String setupConfig = "setupConfig";
    @ConfigSection(
            name = "Configuración de preparación automatizada",
            description = "Sección de configuración automatizada de preparación de NMZ.",
            position = 13
    )
    String autoNMZPrepConfig = "autoNMZPrepConfig";
    @ConfigSection(
            name = "Power Surge Config",
            description = "Power surge spec cambio de armadura.",
            position = 17,
            closedByDefault = true
    )
    String powerSurgeConfig = "powerSurgeConfig";
    @ConfigSection(
            name = "Game Tick Configuracion",
            description = "Configure cómo el bot maneja los retrasos en los ticks del juego; 1 tick del juego equivale aproximadamente a 600 ms",
            position = 57,
            closedByDefault = true
    )
    String delayTickConfig = "delayTickConfig";
    @ConfigSection(
            name = "Configuración de la interfaz de usuario",
            description = "Configuración de la interfaz de usuario.",
            position = 80,
            closedByDefault = true
    )
    String UIConfig = "UIConfig";

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
            keyName = "dinstructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String dinstructions() {
        return "Comience fuera de NMZ.\n\nFlick Prayers solo hace Quick Prayers Infinito. Establecer ofensiva / defensiva antes de encender.\n\nSi Power Surge está habilitado, asegúrese de que su equipo principal y equipo específico estén configurados. Los nombres de los elementos son obligatorios, separados por una coma, por ejemplo 'Dharok's greataxe, Berserker ring'";
    }

    @ConfigItem(
            keyName = "start/stop hotkey",
            name = "Tecla",
            description = "Alternar para activar y desactivar el complemento.",
            position = 6,
            section = "setupConfig"
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "FlickPrayer",
            name = "Flick Prayers (Infinito)",
            description = "Usara quickprayer y no le bajara tu prayer",
            position = 9,
            section = "setupConfig"
    )
    default boolean FlickPrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "RockCake",
            name = "Rock Cake",
            description = "Usar Dwarven rock cake?",
            position = 10,
            section = "setupConfig"
    )
    default boolean RockCake() {
        return false;
    }

    @Range(
            min = 50,
            max = 999
    )
    @ConfigItem(
            keyName = "AbsorptionLowAmount",
            name = "Mantenga la absorb en:",
            position = 11,
            section = "setupConfig",
            description = "Consumirá absorciones si están disponibles en una cantidad determinada."
    )
    default int AbsorptionLowAmount() {
        return 100;
    }

    @ConfigItem(
            keyName = "AutomatePrep",
            name = "Automatizar dream/prep",
            description = "Marque esto si desea automatizar la compra de pots y comenzar a soñar.",
            position = 14,
            section = "autoNMZPrepConfig"
    )
    default boolean AutomatePrep() {
        return false;
    }

    @Range(
            min = 0,
            max = 28
    )
    @ConfigItem(
            keyName = "AbsorptionPotions",
            name = "# de Absorptions",
            description = "# de (4) pociones de absorptionscompletas para retirar de la reserva.",
            position = 15,
            section = "autoNMZPrepConfig"
    )
    default int AbsorptionPotions() {
        return 10;
    }

    @Range(
            min = 0,
            max = 28
    )
    @ConfigItem(
            keyName = "OverloadPotions",
            name = "# de Overloads",
            description = "# de (4) pociones de overload para retirar de la reserva.",
            position = 16,
            section = "autoNMZPrepConfig"
    )
    default int OverloadPotions() {
        return 10;
    }

    @ConfigItem(
            keyName = "PowerSurge",
            name = "Power Surge",
            description = "Usar Power Surge power-up?",
            position = 18,
            section = "powerSurgeConfig"
    )
    default boolean PowerSurge() {
        return false;
    }

    @ConfigItem(
            keyName = "Main Gear",
            name = "Armadura Principal",
            description = "Nombres de la armadura principales",
            position = 19,
            section = "powerSurgeConfig"
    )
    default String MainWeapons() {
        return "";
    }

    @ConfigItem(
            keyName = "Spec Attack Gear Swap",
            name = "Spec Attack armadura para cambiar",
            description = "Equipo para usar ataques especiales durante power surge.",
            position = 20,
            section = "powerSurgeConfig"
    )
    default String SpecWeapons() {
        return "";
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 58,
            section = "delayTickConfig"
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 59,
            section = "delayTickConfig"
    )
    default int tickDelayMax() {
        return 3;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayTarget",
            name = "Game Tick Target",
            description = "",
            position = 60,
            section = "delayTickConfig"
    )
    default int tickDelayTarget() {
        return 2;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayDeviation",
            name = "Game Tick Deviacion",
            description = "",
            position = 61,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribucion",
            description = "",
            position = 62,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

    @ConfigItem(
            keyName = "UISetting",
            name = "Diseño de interfaz de usuario: ",
            description = "Elija el diseño de interfaz de usuario que desee.",
            position = 81,
            section = "UIConfig",
            hidden = false
    )
    default UISettings UISettings() {
        return UISettings.FULL;
    }

    @ConfigItem(
            keyName = "enableUI",
            name = "Habilitar interfaz de usuario",
            description = "Habilitar para activar en la interfaz de usuario del juego",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }
}
