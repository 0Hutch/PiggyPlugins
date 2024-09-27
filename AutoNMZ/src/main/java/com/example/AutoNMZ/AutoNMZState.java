package com.example.AutoNMZ;

public enum AutoNMZState {
    TIMEOUT,
    FIGHT,
    HANDLE_PREP,
    ANIMATING,
    UNHANDLED_STATE,
    BANK_PIN,
    MOVING,
    IDLE;

    private AutoNMZState() {
    }
}
