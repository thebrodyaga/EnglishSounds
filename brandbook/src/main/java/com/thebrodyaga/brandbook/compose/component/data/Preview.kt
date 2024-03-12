package com.thebrodyaga.brandbook.compose.component.data

import androidx.compose.foundation.border
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
import com.thebrodyaga.brandbook.component.play.PlayButtonBindingState
import com.thebrodyaga.brandbook.component.play.PlayButtonUiModel
import com.thebrodyaga.brandbook.compose.component.image.ImageWrapperUiModel
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageUiModel
import com.thebrodyaga.core.uiUtils.text.TextUiModel
import com.thebrodyaga.core.uiUtils.text.TextWrap


@Preview
@Composable
private fun DataUiModelPreview() {
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
private fun testList(): List<DataRowUiModel> {
    val shortText = TextUiModel.Raw(TextWrap.Raw("firstLineText"))
    val longText = TextUiModel.Raw(
        text = TextWrap.Raw(
            "firstLineText firstLineText firstLineText firstLineText " +
                    "firstLineText firstLineText firstLineText firstLineText " +
                    "firstLineText firstLineText firstLineText firstLineText " +
                    "firstLineText firstLineText firstLineText  firstLineText " +
                    "firstLineText"
        ),
        maxLines = Int.MAX_VALUE,
    )
    val longTextEllipsis = TextUiModel.Raw(
        text = TextWrap.Raw(
            "firstLineText firstLineText firstLineText firstLineText " +
                    "firstLineText firstLineText firstLineText firstLineText " +
                    "firstLineText firstLineText firstLineText firstLineText " +
                    "firstLineText firstLineText firstLineText  firstLineText " +
                    "firstLineText"
        ),
    )
    val testIcon = ImageUiModel(
        IconContainer.Res(R.drawable.ic_google_play),
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
    )

    val singleImage = ImageWrapperUiModel.Single(testIcon)

    return listOf(
        DataRowUiModel(
            left = DataRowLeftUiModel.TwoLineText(shortText, shortText),
            right = DataRowRightUiModel.TwoLineText(shortText, shortText),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(singleImage, shortText, shortText),
            right = DataRowRightUiModel.TwoLineText(shortText, shortText),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.TwoLineText(longTextEllipsis, longTextEllipsis),
            right = DataRowRightUiModel.TwoLineText(longTextEllipsis, longTextEllipsis),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(null, shortText, shortText),
            right = DataRowRightUiModel.TwoLineText(shortText, shortText),
        ),
        DataRowUiModel(
            left = null,
            right = DataRowRightUiModel.TwoLineText(shortText, shortText),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(singleImage, shortText, shortText),
            right = null,
        ),
        DataRowUiModel(
            left = null,
            right = DataRowRightUiModel.TwoLineText(longTextEllipsis, longTextEllipsis),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(singleImage, longTextEllipsis, longTextEllipsis),
            right = null,
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(singleImage, longTextEllipsis, longTextEllipsis),
            right = DataRowRightUiModel.PlayIcon(PlayButtonUiModel(PlayButtonBindingState.PauseToPlay())),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(singleImage, longTextEllipsis, longTextEllipsis),
            right = DataRowRightUiModel.PlayIcon(PlayButtonUiModel(PlayButtonBindingState.PlayToPause())),
        ),
        DataRowUiModel(
            left = DataRowLeftUiModel.IconWithTwoLineText(singleImage, longTextEllipsis, longTextEllipsis),
            right = DataRowRightUiModel.Button.Text(shortText.text),
        ),
        DataRowUiModel(
            left = null,
            right = DataRowRightUiModel.Button.Text(shortText.text),
        ),
    )
}