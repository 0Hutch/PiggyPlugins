package com.polyplugins.BobTheWizard;

public enum BankState {
    TELEPORT,
    DEPOSIT,
    WITHDRAW,
    WITHDRAW_NOTED,
    CHECK,
    DONE;

    private BankState() {
    }

    static {
        BankState[] var10000 = new BankState[(char)3 << 896 << (2933248 >>> 9257)];
        var10000[0] = TELEPORT;
        var10000[1] = DEPOSIT;
        var10000[1 << 16352 << -20938 - -28331] = WITHDRAW;
        var10000[192 << 6603 >>> 16946 + -13185] = WITHDRAW_NOTED;
        var10000[256 >>> 11911 << (1208090624 >>> 6897)] = CHECK;
        var10000[5 << 11936 << -11591 + 24391] = DONE;
    }
}
