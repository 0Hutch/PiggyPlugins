/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.wigglydonplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class TELEPORT(private val teleportName: String, private val action: String) {
    CONSTRUCT_CAPE_T("Construct. cape(t)", "Tele to POH"),
    CONSTRUCT_CAPE("Construct. cape", "Tele to POH"),
    HOUSE_TAB("Teleport to house", "Break"),
    HOUSE_RUNES("Rune pouch", "Cast");

    override fun toString(): String = teleportName

    fun action() = action

}
