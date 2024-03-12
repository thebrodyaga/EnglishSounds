package com.thebrodyaga.brandbook.compose.component.image

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.component.icon.TwoIconAngle
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageUiModel


@Preview
@Composable
private fun IconWrapperPreviewPlus45() {
    Surface {
        ImageWrapperUiModel.Two(
            first = ImageUiModel(
                IconContainer.Res(R.drawable.app_splash_icon),
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            ),
            second = ImageUiModel(
                IconContainer.Res(R.drawable.ic_google_play),
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            ),
            angleType = TwoIconAngle.Plus45,
        ).Compose(
            modifier = Modifier.size(145.dp)
        )
    }
}

@Preview
@Composable
private fun ImageWrapperPreview() {
    MaterialTheme {
        Surface {
            val list = testList()
            LazyColumn {
                items(list) { item ->
                    item.Compose()
                }
            }

        }
    }
}

@Composable
private fun testList(): List<ImageWrapperUiModel> {
    val size = Modifier.size(48.dp)
    val bigSize = Modifier.size(96.dp)
    val first = ImageUiModel(
        IconContainer.Res(R.drawable.app_splash_icon),
        modifier = Modifier
            .clip(CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
    val second = ImageUiModel(
        IconContainer.Res(R.drawable.ic_google_play),
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
    )
    val gradientBorder = Modifier
        .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
        .clip(CircleShape)
    return listOf(
        ImageWrapperUiModel.Single(first, size),
        ImageWrapperUiModel.Two(first, second, modifier = bigSize),
        ImageWrapperUiModel.Two(first, second, angleType = TwoIconAngle.Zero, modifier = size),
        ImageWrapperUiModel.Two(first, second, angleType = TwoIconAngle.Plus180, modifier = bigSize),
        ImageWrapperUiModel.Two(first, second, modifier = bigSize.then(gradientBorder)),
        ImageWrapperUiModel.Two(
            first,
            second,
            angleType = TwoIconAngle.Zero,
            modifier = size.then(gradientBorder)
        ),
        ImageWrapperUiModel.Two(
            first,
            second,
            angleType = TwoIconAngle.Plus180,
            modifier = bigSize.then(gradientBorder)
        ),
    )
}