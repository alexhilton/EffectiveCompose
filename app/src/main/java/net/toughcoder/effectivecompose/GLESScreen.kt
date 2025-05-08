package net.toughcoder.effectivecompose

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.serialization.Serializable
import net.toughcoder.effectivecompose.gles.GLESRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

const val KEY_GLES = "OpenGL ES Demo"

@Serializable
object OpenGLES

@Composable
fun GLESScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onBack: () -> Unit
) {
    Box(modifier = modifier.clickable { onBack() }) {
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(50.dp),
            factory = { context ->
                val vertex = "attribute vec4 aPosition;\n" +
                        "attribute float aPointSize;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    gl_Position = aPosition;\n" +
                        "    gl_PointSize = aPointSize;\n" +
                        "}"
                val fragment = "precision mediump float;\n" +
                        "\n" +
                        "uniform vec4 uColor;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    gl_FragColor = uColor;\n" +
                        "}"
                val renderer = object : GLESRenderer(context, vertex, fragment) {
                    override fun onDrawFrame() {
                        super.onDrawFrame()

                        // Set point
                        shader.attributeVector("aPosition")
                        // Set point size
                        shader.attributeScalar("aPointSize", 30.0f)
                        // Set color
                        shader.uniformVector("uColor", 1.0f)

                        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1)
                    }
                }

                val view = GLSurfaceView(context)
                view.setEGLContextClientVersion(2)
                view.setRenderer(object : Renderer {
                    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                        renderer.onSurfaceCreated()
                    }

                    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                        renderer.onSurfaceChanged(width, height)
                    }

                    override fun onDrawFrame(gl: GL10?) {
                        renderer.onDrawFrame()
                    }
                })
                view.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                return@AndroidView view
            },
            update = { view -> view.requestRender() }
        )
    }
}