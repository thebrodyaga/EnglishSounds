package com.thebrodyaga.brandbook.compose.component.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.thebrodyaga.brandbook.component.icon.TwoIconAngle
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageUiModel

@Composable
fun ImageWrapperUiModel.Compose(
    modifier: Modifier = Modifier,
) {
    when (val uiModel = this) {
        is ImageWrapperUiModel.Single -> SingleIcon(
            uiModel = uiModel.icon,
            modifier = modifier.then(uiModel.modifier)
        )

        is ImageWrapperUiModel.Two -> TwoIcon(
            uiModelFirst = uiModel.first,
            uiModelSecond = uiModel.second,
            angleType = uiModel.angleType,
            modifier = modifier.then(uiModel.modifier)
        )
    }
}

@Composable
private fun SingleIcon(
    uiModel: ImageUiModel,
    modifier: Modifier = Modifier,
) {
    val icon = uiModel.icon
    val combinedModifier = modifier.then(uiModel.modifier)

    when (icon) {
        is IconContainer.Draw -> TODO()
        is IconContainer.Res -> Image(
            modifier = combinedModifier,
            painter = painterResource(id = icon.resId),
            contentDescription = null,
        )

        is IconContainer.Url -> TODO("Coil image loading library")
    }
}

@Composable
private fun TwoIcon(
    uiModelFirst: ImageUiModel? = null,
    uiModelSecond: ImageUiModel? = null,
    angleType: TwoIconAngle = TwoIconAngle.Plus45,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        uiModelFirst?.let {
            val firstBias = remember(angleType) {
                when (angleType) {
                    TwoIconAngle.Zero -> BiasAbsoluteAlignment(-1f, 0f)
                    TwoIconAngle.Plus45 -> BiasAbsoluteAlignment(-1f, -1f)
                    TwoIconAngle.Plus180 -> BiasAbsoluteAlignment(1f, 0f)
                }
            }
            SingleIcon(
                uiModel = uiModelFirst,
                modifier = Modifier
                    .align(firstBias)
                    .fillMaxSize(0.625F)
                    .then(uiModelFirst.modifier)
            )
        }
        uiModelSecond?.let {
            val secondBias = remember(angleType) {
                when (angleType) {
                    TwoIconAngle.Zero -> BiasAbsoluteAlignment(1f, 0f)
                    TwoIconAngle.Plus45 -> BiasAbsoluteAlignment(1f, 1f)
                    TwoIconAngle.Plus180 -> BiasAbsoluteAlignment(-1f, 0f)
                }
            }
            SingleIcon(
                uiModel = uiModelSecond,
                modifier = Modifier
                    .align(secondBias)
                    .fillMaxSize(0.625F)
                    .then(uiModelSecond.modifier)
            )
        }
    }
}