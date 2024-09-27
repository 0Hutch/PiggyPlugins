/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.wigglydonplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class STAFF(private val staffName: String) {
    SLAYER_STAFF("Slayer's staff"),
    SLAYER_STAFF_E("Slayer's staff (e)"),
    RUNE_POUCH("Rune pouch");

    override fun toString(): String {
        return staffName
    }
}
