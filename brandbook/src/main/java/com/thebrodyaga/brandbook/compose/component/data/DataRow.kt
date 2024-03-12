package com.thebrodyaga.brandbook.compose.component.data

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thebrodyaga.brandbook.compose.component.image.Compose
import com.thebrodyaga.brandbook.compose.component.play.Compose
import com.thebrodyaga.core.uiUtils.text.TextUiModel

@Composable
fun DataRowUiModel.Compose(
    onCLick: () -> Unit = {},
    playIconClick: () -> Unit = { onCLick() },
    rightBtnClick: () -> Unit = { onCLick() },
    modifier: Modifier = Modifier,
) {
    val uiModel = this
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp)
            .clickable { onCLick() }
            .padding(16.dp, 12.dp)
            .then(uiModel.modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier.weight(1f)
        ) {
            when (val leftSide = uiModel.left) {
                is DataRowLeftUiModel.IconWithTwoLineText -> DataRowIconWithTwoLine(leftSide)
                is DataRowLeftUiModel.TwoLineText -> DataRowTwoLine(
                    firstLine = leftSide.firstLineText,
                    secondLine = leftSide.secondLineText,
                )

                null -> {}
            }
        }
        if (left != null && right != null) {
            Spacer(modifier = Modifier.size(8.dp))
        }

        when (right) {
            is DataRowRightUiModel.TwoLineText -> DataRowTwoLine(
                firstLine = right.firstLineText,
                secondLine = right.secondLineText,
            )

            is DataRowRightUiModel.PlayIcon -> right.playIcon.Compose(playIconClick)

            is DataRowRightUiModel.Button.Text -> TextButton(rightBtnClick) {
                Text(text = right.text?.annotated ?: AnnotatedString(""))
            }

            null -> {}
        }
    }
}

@Composable
private fun DataRowIconWithTwoLine(leftSide: DataRowLeftUiModel.IconWithTwoLineText) {
    val icon = leftSide.icon
    val firstLine = leftSide.firstLineText
    val secondLine = leftSide.secondLineText
    icon?.let {
        icon.Compose(
            modifier = Modifier.size(48.dp)
        )
    }
    if (firstLine != null || secondLine != null) {
        Spacer(modifier = Modifier.size(12.dp))
    }
    DataRowTwoLine(
        firstLine = firstLine,
        secondLine = secondLine,
    )
}

@Composable
private fun DataRowTwoLine(
    firstLine: TextUiModel? = null,
    secondLine: TextUiModel? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        if (firstLine is TextUiModel.Raw) {
            Text(
                text = firstLine.text.annotated,
                modifier = firstLine.modifier,
                overflow = TextOverflow.Ellipsis,
                maxLines = firstLine.maxLines ?: 1,
                style = firstLine.style ?: MaterialTheme.typography.titleMedium,
            )
        }
        if (firstLine != null && secondLine != null) {
            Spacer(modifier = Modifier.size(8.dp))
        }
        if (secondLine is TextUiModel.Raw) {
            Text(
                text = secondLine.text.annotated,
                modifier = secondLine.modifier,
                maxLines = secondLine.maxLines ?: 1,
                overflow = TextOverflow.Ellipsis,
                style = secondLine.style ?: MaterialTheme.typography.labelMedium,
            )
        }

    }
}