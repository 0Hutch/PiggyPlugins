package com.piggyplugins.AutoWarriorsGuild;

public enum Armors {
    BLACK("Black full helm", "Black platebody", "Black platelegs"),
    MITHRIL("Mithril full helm", "Mithril platebody", "Mithril platelegs"),
    ADAMANT("Adamant full helm", "Adamant platebody", "Adamant platelegs"),
    RUNE("Rune full helm", "Rune platebody", "Rune platelegs");

    private final String helm;
    private final String body;
    private final String legs;

    Armors(String helm, String body, String legs) {
        this.helm = helm;
        this.body = body;
        this.legs = legs;
    }

    public String getHelm() {
        return helm;
    }

    public String getBody() {
        return body;
    }

    public String getLegs() {
        return legs;
    }
}