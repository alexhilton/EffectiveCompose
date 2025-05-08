package net.toughcoder.effectivecompose

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

const val KEY_FLAG = "Waving flag"

@Serializable
object WavingFlag

@Composable
fun FiveStarScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onBack: ()->Unit
) {
    Box(
        modifier = Modifier
            .background(Color.DarkGray)
            .padding(top = 60.dp)
            .clickable { onBack() },
        contentAlignment = Alignment.TopCenter
    ) {
        FiveStarsRedFlag(220.dp)
    }
}

@Composable
fun FiveStarsRedFlag(height: Dp = 200.dp) {
    val flagWidth = height.times(1.5f)
    val stickWidth = 10.dp
    val canvasWidth = flagWidth.plus(stickWidth)

    val flagTransition = rememberInfiniteTransition(label = "infinite flag transition")

    val rotateY by flagTransition.animateFloat(
        initialValue = -3f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "rotateY"
    )

    val amplitude = with(LocalDensity.current) { height.div(8f).toPx() }
    val heightPx = with(LocalDensity.current) { height.toPx() }

    val waveDuration = 2000
    val ya by flagTransition.animateFloat(
        initialValue = amplitude / 2f,
        targetValue = -amplitude / 2f,
        animationSpec = infiniteRepeatable(tween(waveDuration), RepeatMode.Reverse),
        label = "ya"
    )
    val yb by flagTransition.animateFloat(
        initialValue = -amplitude / 2f,
        targetValue = amplitude / 2f,
        animationSpec = infiniteRepeatable(tween(waveDuration), RepeatMode.Reverse),
        label = "yb"
    )
    val yc by flagTransition.animateFloat(
        initialValue = amplitude / 2f,
        targetValue = -amplitude / 2f,
        animationSpec = infiniteRepeatable(tween(waveDuration), RepeatMode.Reverse),
        label = "yc"
    )

    val ye by flagTransition.animateFloat(
        initialValue = heightPx + amplitude / 2f,
        targetValue = heightPx - amplitude / 2f,
        animationSpec = infiniteRepeatable(tween(waveDuration), RepeatMode.Reverse),
        label = "ye"
    )
    val yf by flagTransition.animateFloat(
        initialValue = heightPx - amplitude / 2f,
        targetValue = heightPx + amplitude / 2f,
        animationSpec = infiniteRepeatable(tween(waveDuration), RepeatMode.Reverse),
        label = "yf"
    )
    val yg by flagTransition.animateFloat(
        initialValue = heightPx + amplitude / 2f,
        targetValue = heightPx - amplitude / 2f,
        animationSpec = infiniteRepeatable(tween(waveDuration), RepeatMode.Reverse),
        label = "yg"
    )

    Canvas(
        modifier = Modifier
            .size(canvasWidth, height)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0f)
                rotationZ = 2f
                rotationY = rotateY
            }
    ) {
        // The stick
        drawRect(
            Color.LightGray,
            size = Size(stickWidth.toPx(), size.height * 2f)
        )

        val stickOffset = Offset(stickWidth.toPx(), 0f)

        // The background
        val pathBG = Path().apply {
            moveTo(0f, 0f)
            cubicTo(flagWidth.toPx() / 3f, ya, flagWidth.toPx() * 2f / 3f, yb, flagWidth.toPx(), yc)

            lineTo(flagWidth.toPx(), ye)

            cubicTo(flagWidth.toPx() * 2f / 3f, yf, flagWidth.toPx() / 3f, yg, 0f, size.height)

            lineTo(0f, 0f)

            translate(stickOffset)
        }
        drawPath(path = pathBG, color = Color.Red, style = Fill)

        val radius = size.height * 3f / 20f
        val alphaCenter = Offset(flagWidth.toPx() / 6f, size.height / 4f)
        val smallRadius = size.height / 20f

        //<editor-fold desc="Draw the stars">
        // 大五角星 alpha
        drawStar(
            alphaCenter = alphaCenter,
            center = alphaCenter,
            radius = radius,
            color = Color.Yellow,
            offset = stickOffset
        )

        // 小五星 a
        drawStar(
            alphaCenter = alphaCenter,
            center = Offset(flagWidth.toPx() / 3f, size.height / 10f),
            radius = smallRadius,
            color = Color.Yellow,
            offset = stickOffset
        )

        // 小五星 b
        drawStar(
            alphaCenter = alphaCenter,
            center = Offset(flagWidth.toPx() * 0.4f, size.height / 5f),
            radius = smallRadius,
            color = Color.Yellow,
            offset = stickOffset
        )

        // 小五星 c
        drawStar(
            alphaCenter = alphaCenter,
            center = Offset(flagWidth.toPx() * 0.4f, size.height * 7 / 20f),
            radius = smallRadius,
            color = Color.Yellow,
            offset = stickOffset
        )

        // 小五星 d
        drawStar(
            alphaCenter = alphaCenter,
            center = Offset(flagWidth.toPx() / 3f, size.height * 9 / 20f),
            radius = smallRadius,
            color = Color.Yellow,
            offset = stickOffset
        )
        //</editor-fold>

        //<editor-fold desc="Draw grid for debugging">
        if (DEBUG) {
            val strokeWidth = 0.8.dp.toPx()

            // Slice
            drawLine(
                Color.Black,
                Offset(stickOffset.x, size.height / 2f),
                Offset(size.width, size.height / 2f),
                strokeWidth = strokeWidth * 2f
            )

            drawLine(
                Color.Black,
                Offset(stickOffset.x + flagWidth.toPx() / 2f, 0f),
                Offset(stickOffset.x + flagWidth.toPx() / 2f, size.height),
                strokeWidth = strokeWidth * 2f
            )

            // Grid
            for (i in 1 until 10) {
                drawLine(
                    Color.Black,
                    Offset(stickOffset.x, size.height * i / 20f),
                    Offset(stickOffset.x + flagWidth.toPx() / 2f, size.height * i / 20f),
                    strokeWidth = strokeWidth
                )
            }

            for (i in 1 until 16) {
                drawLine(
                    Color.Black,
                    Offset(flagWidth.toPx() * i / 30f, 0f),
                    Offset(flagWidth.toPx() * i / 30f, size.height / 2f),
                    strokeWidth = strokeWidth
                )
            }

            // Circles
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = Offset(stickOffset.x + alphaCenter.x, alphaCenter.y),
                style = Stroke(width = strokeWidth)
            )

            drawCircle(
                color = Color.Black,
                radius = smallRadius,
                center = Offset(stickOffset.x + flagWidth.toPx() / 3f, size.height / 10f),
                style = Stroke(width = strokeWidth)
            )

            drawCircle(
                color = Color.Black,
                radius = smallRadius,
                center = Offset(stickOffset.x + flagWidth.toPx() * 0.4f, size.height / 5f),
                style = Stroke(width = strokeWidth)
            )

            drawCircle(
                color = Color.Black,
                radius = smallRadius,
                center = Offset(stickOffset.x + flagWidth.toPx() * 0.4f, size.height * 7 / 20f),
                style = Stroke(width = strokeWidth)
            )

            drawCircle(
                color = Color.Black,
                radius = smallRadius,
                center = Offset(stickOffset.x + flagWidth.toPx() / 3f, size.height * 9 / 20f),
                style = Stroke(width = strokeWidth)
            )

            // Line between Alpha center to a, b, c and d's centers.
            drawLine(
                Color.Black,
                Offset(stickOffset.x + alphaCenter.x, alphaCenter.y),
                Offset(stickOffset.x + flagWidth.toPx() / 3f, size.height / 10f),
                strokeWidth = strokeWidth
            )

            drawLine(
                Color.Black,
                Offset(stickOffset.x + alphaCenter.x, alphaCenter.y),
                Offset(stickOffset.x + flagWidth.toPx() * 0.4f, size.height / 5f),
                strokeWidth = strokeWidth
            )

            drawLine(
                Color.Black,
                Offset(stickOffset.x + alphaCenter.x, alphaCenter.y),
                Offset(stickOffset.x + flagWidth.toPx() * 0.4f, size.height * 7f / 20f),
                strokeWidth = strokeWidth
            )

            drawLine(
                Color.Black,
                Offset(stickOffset.x + alphaCenter.x, alphaCenter.y),
                Offset(stickOffset.x + flagWidth.toPx() / 3f, size.height * 9f / 20f),
                strokeWidth = strokeWidth
            )
        }
        //</editor-fold>
    }
}

fun DrawScope.drawStar(
    alphaCenter: Offset, center: Offset, radius: Float, color: Color,
    offset: Offset
) {
    val pointNumber = 5
    val angle = PI.toFloat() / pointNumber
    val innerRadius = radius * cos(angle) / 2f

    val beta = if (alphaCenter == center) {
        0f
    } else {
        PI.toFloat() / 2f - atan((center.y - alphaCenter.y) / (center.x - alphaCenter.x))
    }

    val path = Path().apply {
        for (i in 0 .. pointNumber * 2) {
            val r = if (i % 2 == 1) radius else innerRadius
            val omega = angle * i + beta

            val x = center.x + r * sin(omega)
            val y = center.y + r * cos(omega)
            lineTo(x, y)
        }
        translate(offset)
    }

    drawPath(
        path = path,
        color = color,
        style = Fill
    )
}

const val DEBUG = false