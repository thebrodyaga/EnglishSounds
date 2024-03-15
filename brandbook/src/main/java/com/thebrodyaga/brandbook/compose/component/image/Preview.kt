package com.thebrodyaga.brandbook.compose.component.image

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.intermediateLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.component.icon.TwoIconAngle
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageUiModel

@Preview
@Composable
private fun ImageWrapperPreview() {
    MaterialTheme {
        Surface {
            var selectedItem by remember { mutableIntStateOf(0) }
            Scaffold(
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier,
                        shape = FloatingActionButtonDefaults.shape,
                        onClick = { /* do something */ },
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                },
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier.clip(ShapeDefaults.ExtraLarge)
                            .wrapContentWidth()
                    )
                    {
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Check, contentDescription = null) },
                            label = { Text("item") },
                            selected = true,
                            onClick = { selectedItem = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                            label = { Text("item") },
                            selected = true,
                            onClick = { selectedItem = 1 }
                        )

                        Spacer(modifier = Modifier.size(40.dp))

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Call, contentDescription = null) },
                            label = { Text("item") },
                            selected = true,
                            onClick = { selectedItem = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Face, contentDescription = null) },
                            label = { Text("item") },
                            selected = true,
                            onClick = { selectedItem = 1 }
                        )
                    }
                },
            ) { _ ->

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