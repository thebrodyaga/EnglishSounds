package com.thebrodyaga.brandbook.compose.component.data

import androidx.compose.ui.Modifier
import com.thebrodyaga.brandbook.model.UiModelPayload

data class DataRowUiModel(
    val left: DataRowLeftUiModel? = null,
    val right: DataRowRightUiModel? = null,
    override val payload: Any? = null,
    val modifier: Modifier = Modifier,
) : UiModelPayload