package com.polyplugins.AutoMLM;

public enum Sack {
    REGULAR(81),
    UPGRADED(162);

    private final int size;

    private Sack(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }
}
