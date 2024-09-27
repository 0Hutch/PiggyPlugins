package com.polyplugins.AutoMLM;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("AutoMLM")
public interface AutoMLMConfiguration extends Config {
    String version = "v1.0";
    @ConfigSection(
            name = "Instrucciones",
            description = "Plugin Instrucciones.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";
    @ConfigSection(
            name = "Configuración",
            description = "Plugin Configuración.",
            position = 5
    )
    String setupConfig = "setupConfig";
    @ConfigSection(
            name = "Game Tick Configuracion",
            description = "Configure cómo el bot maneja los retrasos en los ticks del juego; 1 tick del juego equivale aproximadamente a 600 ms",
            position = 57,
            closedByDefault = true
    )
    String delayTickConfig = "delayTickConfig";
    @ConfigSection(
            name = "Configuración de la interfaz de usuario",
            description = "Configuración de la interfaz de usuario",
            position = 80,
            closedByDefault = true
    )
    String UIConfig = "UIConfig";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "informacion";
    }

    @ConfigItem(
            keyName = "ninstructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String ninstructions() {
        return "Comience en la mina Motherlode. \n\nDebe tener un hammer en el inventario si tiene activada la opción Fix Wheel.\n\nActive la opción Usar especificaciones para usar el pico Dragón especial.\n\nConfigure la tecla de acceso rápido y active el complemento con la tecla de acceso rápido.";
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
            keyName = "mineArea",
            name = "Qué área minar?:",
            position = 7,
            section = "setupConfig",
            description = "Ingrese el área donde desea extraer."
    )
    default MineArea MineArea() {
        return MineArea.ARRIBA_1;
    }

    @ConfigItem(
            keyName = "sackSize",
            name = "Saco:",
            position = 8,
            section = "setupConfig",
            description = "Ingrese el área donde desea extraer."
    )
    default Sack Sack() {
        return Sack.REGULAR;
    }

    @ConfigItem(
            keyName = "useSpec",
            name = "Usar Spec",
            description = "Usar Dragon pickaxe spec?",
            position = 9,
            section = "setupConfig"
    )
    default boolean useSpec() {
        return false;
    }

    @ConfigItem(
            keyName = "fixWheels",
            name = "Fix Wheels",
            description = "Si está activado debes tener un martillo en el inventario",
            position = 10,
            section = "setupConfig"
    )
    default boolean fixWheels() {
        return false;
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
            name = "Habilitar la interfaz de usuario",
            description = "Habilitar para activar en la interfaz de usuario del juego",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }
}
