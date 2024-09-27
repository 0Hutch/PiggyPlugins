    /*
     * Copyright (c) 2024. By Jrod7938
     *
     */
    package com.wigglydonplugins.autoVorkath

    import lombok.Getter
    import lombok.RequiredArgsConstructor

    @Getter
    @RequiredArgsConstructor
    enum class CROSSBOW(private val crossbowName: String) {
        DRAGON_HUNTER_CROSSBOW("Dragon hunter crossbow"),
        RUNE_CROSSBOW("Rune crossbow"),
        DRAGON_CROSSBOW("Dragon crossbow");
    }
