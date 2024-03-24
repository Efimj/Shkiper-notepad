package com.jobik.shkiper.screens.AppLayout

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jobik.shkiper.ui.theme.CustomTheme

object AppNavigationBarState {
    private var _visible: MutableState<Boolean> = mutableStateOf(false)
    private var _locked: MutableState<Boolean> = mutableStateOf(false)

    val isVisible: State<Boolean> = _visible
    val isLocked: State<Boolean> = _locked

    fun show() {
        if (isLocked.value.not())
            _visible.value = true
    }

    fun showWithUnlock() {
        unlock()
        show()
    }

    fun hide() {
        if (isLocked.value.not())
            _visible.value = false
    }

    fun hideWithLock() {
        hide()
        lock()
    }

    fun lock() {
        _locked.value = true
    }

    fun unlock() {
        _locked.value = false
    }
}

data class CustomBottomNavigationItem(
    val icon: ImageVector,
    @StringRes
    val description: Int,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

data class DefaultNavigationValues(
    val containerHeight: Dp = 64.dp
)

@Composable
fun CustomBottomNavigationItem(properties: CustomBottomNavigationItem) {
    val contentColorValue =
        if (properties.isSelected) CustomTheme.colors.textOnActive else CustomTheme.colors.textSecondary
    val contentColor = animateColorAsState(targetValue = contentColorValue, label = "backgroundColor")

    val backgroundColorValue =
        if (properties.isSelected) CustomTheme.colors.active.copy(alpha = .5f) else Color.Transparent
    val backgroundColor = animateColorAsState(targetValue = backgroundColorValue, label = "backgroundColor")

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(shape = MaterialTheme.shapes.small)
            .background(backgroundColor.value)
            .clickable {
                properties.onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = properties.icon,
            contentDescription = stringResource(properties.description),
            tint = contentColor.value,
        )
    }
}

@Composable
fun CustomBottomNavigation(items: List<CustomBottomNavigationItem>) {
    Row(
        modifier = Modifier
            .height(DefaultNavigationValues().containerHeight)
            .clip(shape = MaterialTheme.shapes.small)
            .border(width = 1.dp, color = CustomTheme.colors.mainBackground, shape = MaterialTheme.shapes.small)
            .background(CustomTheme.colors.secondaryBackground)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach {
            CustomBottomNavigationItem(properties = it)
        }
    }
}