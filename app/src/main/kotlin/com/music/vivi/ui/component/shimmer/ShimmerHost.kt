/**
 * vivimusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.music.vivi.ui.component.shimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer

/**
 * Staggered shimmer host: children fade in sequentially with a configurable delay
 * between each item, creating a cascading reveal effect instead of a simultaneous flash.
 */
@Composable
fun ShimmerHost(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    staggerDelayMs: Int = 80,
    content: @Composable ColumnScope.() -> Unit,
) {
    val stagger = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        stagger.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 400,
                easing = LinearEasing,
                delayMillis = 150,
            ),
        )
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        modifier =
        modifier
            .graphicsLayer(alpha = stagger.value)
            .shimmer()
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent)),
                    blendMode = BlendMode.DstIn,
                )
            },
        content = content,
    )
}

val ShimmerTheme =
    defaultShimmerTheme.copy(
        animationSpec =
        infiniteRepeatable(
            animation =
            tween(
                durationMillis = 800,
                easing = LinearEasing,
                delayMillis = 250,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        shaderColors =
        listOf(
            Color.Unspecified.copy(alpha = 0.25f),
            Color.Unspecified.copy(alpha = 0.50f),
            Color.Unspecified.copy(alpha = 0.25f),
        ),
    )
