package net.toughcoder.effectivecompose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AnimateVisibility() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WithAnimatedVisibility()

        WithAlphaAsState()

        LaunchingAnimation()
    }
}

/*
 * This won't work when first compose, i.e. when launching.
 * It only works after views are composed. When you toggle
 * the button after everything shown, for instance.
 * We can see that when animating exit, the animated
 * composables are removed from layouts. This is the biggest
 * difference from animate alpha as state.
 */
@Composable
private fun WithAnimatedVisibility() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var visible by remember { mutableStateOf(true) }

        AnimatedVisibility(visible) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Green)
            )
        }

        Button(
            onClick = { visible = !visible }
        ) {
            Text("(AnimatedVisibility) Show/Hide")
        }
    }
}

@Composable
private fun WithAlphaAsState() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var visible by remember { mutableStateOf(true) }
        val alphaState by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            label = "alpha as state"
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer { alpha = alphaState }
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Green)
        )

        Button(
            onClick = { visible = !visible }
        ) {
            Text("(Alpha as state) Show/Hide")
        }
    }
}

@Composable
private fun LaunchingAnimation() {
    val alphaAnimation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer { alpha = alphaAnimation.value }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Green)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Animation when launching!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}