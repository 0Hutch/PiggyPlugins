/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.wigglydonplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class PRAYER_POTION(private val potionName: String) {
    PRAYER("Prayer potion"),
    SUPER_RESTORE("Super restore");

    override fun toString(): String {
        return potionName
    }
}
