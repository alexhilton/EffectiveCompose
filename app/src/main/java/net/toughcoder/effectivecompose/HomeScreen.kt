package net.toughcoder.effectivecompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import net.toughcoder.effectivecompose.shader.KEY_SHADERS
import net.toughcoder.effectivecompose.shader.FirstShader

@Serializable
object Home

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    goto: (Any)->Unit
) {
    Column(
        modifier = modifier.padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Button(onClick = { goto(Animations) }) {
            BigText(KEY_ANIM)
        }

        Button(onClick = { goto(WavingFlag) }) {
            BigText(KEY_FLAG)
        }

        Button(onClick = { goto(OpenGLES) }) {
            BigText(KEY_GLES)
        }

        Button(onClick = { goto(FirstShader) }) {
            BigText(KEY_SHADERS)
        }
    }
}

@Composable
fun BigText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium
    )
}