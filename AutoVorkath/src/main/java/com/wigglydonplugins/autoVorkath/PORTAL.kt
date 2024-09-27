/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.wigglydonplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class PORTAL(private val portalName: String, private val portalAction: String) {
    PORTAL_NEXUS("Portal Nexus", "Lunar Isle"),
    LUNAR_ISLE_PORTAL("Lunar Isle Portal", "Enter");

    override fun toString(): String = portalName
    fun action(): String = portalAction

}
