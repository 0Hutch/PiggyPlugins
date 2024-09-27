/*
 * Copyright (c) 2024. By Jrod7938
 *
 */

package com.wigglydonplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class ANTIVENOM(private val antiVenomName: String, private val time: Int) {
    ANTI_VENOM_PLUS("Anti-venom+", 3);


    override fun toString(): String = antiVenomName
    fun time(): Int = ((time * .3) * 60 * 1000).toInt() // time to drink ( 30% of the antivenom effect )
}
