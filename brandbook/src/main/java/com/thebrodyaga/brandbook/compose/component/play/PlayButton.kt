package com.thebrodyaga.brandbook.compose.component.play

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.component.play.PlayButtonBindingState
import com.thebrodyaga.brandbook.component.play.PlayButtonUiModel

//todo vector animation
@Composable
fun PlayButtonUiModel.Compose(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val icon = when (this.state) {
        is PlayButtonBindingState.PauseToPlay -> painterResource(R.drawable.ic_play)
        is PlayButtonBindingState.PlayToPause -> painterResource(R.drawable.ic_pause)
    }

    IconButton(
        onClick = { onClick() },
        modifier = modifier,
    ) {
        Icon(painter = icon, contentDescription = null)
    }
}