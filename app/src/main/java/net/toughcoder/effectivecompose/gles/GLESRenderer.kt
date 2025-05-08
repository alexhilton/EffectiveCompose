package net.toughcoder.effectivecompose.gles

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import net.toughcoder.effectivegles.glkit.GLESShader

open class GLESRenderer(
    protected val context: Context,
    private val vertexShaderPath: String,
    private val fragmentShaderPath: String
) {
    companion object {
        const val LOG_TAG = "GLESRenderer"
    }

    protected lateinit var shader: GLESShader

    protected var ratio = 1f
    protected var width: Int = -1
    protected var height: Int = -1

    open fun onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        shader.attach()
    }

    open fun onSurfaceChanged(width: Int, height: Int) {
        Log.d(LOG_TAG, "onSurfaceChanged width $width, height $height")
        GLES20.glViewport(0, 0, width, height)
        this.width = width
        this.height = height
        ratio = width.toFloat() / height.toFloat()
    }

    open fun onSurfaceCreated() {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        shader = GLESShader.fromString(vertexShaderPath, fragmentShaderPath)
        val vendor = GLES20.glGetString(GLES20.GL_VENDOR)
        val renderer = GLES20.glGetString(GLES20.GL_RENDERER)
        val glslVersion = GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)
        val glVersion = GLES20.glGetString(GLES20.GL_VERSION)
//        val extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS)
        Log.d(LOG_TAG, "onSurfaceCreated: \nVendor: '$vendor'" +
                ",\nRenderer: '$renderer'" +
                ",\nGL version: '$glVersion'" +
                ",\nGLSL version: '$glslVersion'")
    }
}