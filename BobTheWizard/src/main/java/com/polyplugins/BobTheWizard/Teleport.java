package com.polyplugins.BobTheWizard;

public enum Teleport {
    VARROCK,
    LUMBRIDGE,
    FALADOR,
    CAMELOT,
    ARDOUGNE,
    WATCHTOWER,
    TROLLHEIM,
    APE_ATOLL,
    KOUREND;

    private Teleport() {
    }

    static {
        Teleport[] var10000 = new Teleport[9 << 15745 >>> (12417 << 3232)];
        var10000[0] = VARROCK;
        var10000[1] = LUMBRIDGE;
        var10000[(1902991228 ^ -244492420) >>> (17013 ^ 31595)] = FALADOR;
        var10000[805306368 >>> 517 >>> (1273856 >>> 15660)] = CAMELOT;
        var10000[8 >>> 6402 << (11809 << 5536)] = ARDOUGNE;
        var10000[(-2022583835 ^ -1921920539) >>> (8089 << (char)15840)] = WATCHTOWER;
        var10000[912365495 + -899782583 >>> (-28055 ^ -32004)] = TROLLHEIM;
        var10000[-354480830 - -589361854 >>> (53710848 >>> (char)9388)] = APE_ATOLL;
        var10000[330515280 + -330253136 >>> (274595840 >>> 5489)] = KOUREND;
    }
}
