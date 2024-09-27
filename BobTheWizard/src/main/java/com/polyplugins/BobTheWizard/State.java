package com.polyplugins.BobTheWizard;

public enum State {
    ANIMATING,
    CAST_HIGH_ALC,
    CAST_TELEPORT,
    HIGH_ALCING,
    TELEPORTING,
    MAKE_PLANK,
    BANK,
    TIMEOUT,
    NONE,
    HANDLE_BREAK;

    private State() {
    }

    static {
        State[] var10000 = new State[5 << 3456 << 31205 - 26628];
        var10000[0] = ANIMATING;
        var10000[1] = CAST_HIGH_ALC;
        var10000[-2025900422 + 2030094726 >>> (8117 << (char)4448)] = CAST_TELEPORT;
        var10000[(8210 ^ 16402) >>> 11495 - 10170] = HIGH_ALCING;
        var10000[2097152 >>> 9748 << (601948160 >>> 13936)] = TELEPORTING;
        var10000[10 << 1345 >>> (-6967 ^ -8917)] = MAKE_PLANK;
        var10000[3 << 14048 << -16019 + 19924] = BANK;
        var10000[(564778750 ^ 543807230) >>> (6971 << (char)15393)] = TIMEOUT;
        var10000[-327062872 - -343840088 >>> (1625948160 >>> 6609)] = NONE;
        var10000[36 >>> 11426 << (3456 >>> 1986)] = HANDLE_BREAK;
    }
}
