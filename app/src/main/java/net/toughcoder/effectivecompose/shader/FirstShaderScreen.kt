package net.toughcoder.effectivecompose.shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.toughcoder.effectivecompose.R

const val KEY_SHADERS = "First Shader Demo"

@Serializable
object FirstShader

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FirstShaderScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onBack: () -> Unit
) {
    Box(
        Modifier.fillMaxSize()
            .clickable { onBack() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.dark_forest),
            contentDescription = null
        )
        val runtimeShader = """
            uniform shader image;
            uniform float2 resolution;
            
            uniform float radius;
            uniform float time;
            
            half4 main(float2 fragCoord) {
                vec2 uv = fragCoord / resolution.xy - .5;
                uv.x *= resolution.x / resolution.y;
                float radiusWithTime = (1 + sin(time)) * 0.1 + radius;
                float glowingCircle = smoothstep(radiusWithTime, radiusWithTime - radiusWithTime * 0.8, length(uv));
                return half4(glowingCircle - step(length(uv), radius * 0.7));
                //return half4(step(length(uv), 0.5));
            }
        """.trimIndent()

        val shader = remember { RuntimeShader(runtimeShader) }
        var time by remember { mutableFloatStateOf(0f) }
        shader.setFloatUniform("radius", 0.6f)

        LaunchedEffect(null) {
            while (true) {
                delay(100)
                time += 0.01f
            }
        }

        shader.setFloatUniform("time", time)
        Box(modifier = Modifier
            .size(200.dp)
            .clipToBounds()
            .onSizeChanged { size ->
                shader.setFloatUniform(
                    "resolution", size.width.toFloat(), size.height.toFloat()
                )
            }
            .graphicsLayer {
                this.renderEffect = RenderEffect
                    .createRuntimeShaderEffect(
                        shader, "image"
                    )
                    .asComposeRenderEffect()
            }
            .background(Color.White)
        ) {
        }
    }
}