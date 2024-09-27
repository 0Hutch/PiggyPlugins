package com.wigglydonplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class FOOD_TYPE(val foodId: Int) {
    SHARK(385),
    MANTA_RAY(391);
}

