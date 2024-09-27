package com.polyplugins.AutoMLM;

public enum AutoMLMState {
    TIMEOUT,
    FIND_BANK,
    DEPOSIT_HOPPER,
    DEPOSIT_BANK,
    HANDLE_BREAK,
    ANIMATING,
    MINE,
    UNHANDLED_STATE,
    BANK_PIN,
    MOVING,
    IDLE;

    private AutoMLMState() {
    }
}
