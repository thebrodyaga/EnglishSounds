package com.thebrodyaga.brandbook.compose.component.image

import androidx.compose.ui.Modifier
import com.thebrodyaga.brandbook.component.icon.TwoIconAngle
import com.thebrodyaga.core.uiUtils.image.ImageUiModel

sealed interface ImageWrapperUiModel {

    data class Single(
        val icon: ImageUiModel,
        val modifier: Modifier = Modifier,
    ) : ImageWrapperUiModel

    // todo
    /*data class SingleWithBadge(
        val icon: ImageViewUiModel,
        val badge: ImageViewUiModel,
    )*/

    data class Two(
        val first: ImageUiModel?,
        val second: ImageUiModel?,
        val angleType: TwoIconAngle = TwoIconAngle.Plus45,
        val modifier: Modifier = Modifier,
    ) : ImageWrapperUiModel
}
