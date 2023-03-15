package com.thebrodyaga.brandbook.component.icon

import com.thebrodyaga.brandbook.recycler.model.UiModel
import com.thebrodyaga.core.uiUtils.image.ImageViewUiModel

sealed interface IconWrapperUiModel : UiModel {

    data class SingleIcon(
        val icon: ImageViewUiModel,
    ) : IconWrapperUiModel

    // todo
    /*data class SingleWithBadge(
        val icon: ImageViewUiModel,
        val badge: ImageViewUiModel,
    )*/

    data class TwoIcon(
        val first: ImageViewUiModel?,
        val second: ImageViewUiModel?,
        val angleType: TwoIconAngle = TwoIconAngle.Plus45,
    ) : IconWrapperUiModel
}

sealed interface TwoIconAngle {
    //horizontal line, first icon on the center of left side, second icon on the center of right side
    object Zero : TwoIconAngle

    //first icon on the left top corner, second icon on the right bottom corner
    object Plus45 : TwoIconAngle

    //horizontal line, first icon on the center of right side, second icon on the center of left side
    object Plus180 : TwoIconAngle
}
