package net.toughcoder.effectivegles.glkit

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import net.toughcoder.effectivecompose.gles.GLESHelper
import net.toughcoder.effectivecompose.gles.GLESHelper.Companion.BYTES_OF_INTEGER
import net.toughcoder.effectivecompose.gles.GLESHelper.Companion.TEXTURE_UNITS
import net.toughcoder.effectivecompose.gles.GLESHelper.Companion.checkGLError
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.HashSet
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

/**
 * Encapsulation of the GL ES shading program.
 * Fragment shaders and vertex shaders should use different extension:
 * vertex shader: <filename>.vert
 * fragment shader: <filename>.frag
 * Shaders can be placed into different folders, constructors accept full paths only.
 * Helper methods of specifying each shaders are provided also.
 * Usage:
 * 1. instantiate by <code>shader = GLESShader.fromAssets(context, "Basics", "HelloPoints")</code>
 *     full path should be provided here.
 * 2. attach before rendering: <code>shader.attach</code>
 * 3. detach after using: <code>shader.detach</code>
 * That's all.
 */
class GLESShader {
    companion object {
        const val LOG_TAG = "GLESShader"
        private const val DEFAULT_FOLDER = "shaders"
        private const val FOLDER_LIGHT = "lights"
        private const val FOLDER_SHADOW = "shadows"
        private const val FOLDER_PARTICLE = "particles"
        private const val FOLDER_TEXTURE = "textures"
        const val EXT_VERTEX = "vert"
        const val EXT_FRAGMENT = "frag"

        fun fromString(vertex: String, fragment: String): GLESShader {
            return GLESShader(vertex, fragment)
        }

        /**
         * Vertex shader in default assets folder.
         * e.g. BasicColor.vert
         */
        fun vert(name: String) = "$DEFAULT_FOLDER/$name.$EXT_VERTEX"

        /**
         * Fragment shader in default assets folder.
         * e.g. BasicColor.frag
         */
        fun frag(name: String) = "$DEFAULT_FOLDER/$name.$EXT_FRAGMENT"

        /** Vertex shader in lights assets folder */
        fun ltvs(name: String) = "$FOLDER_LIGHT/$name.$EXT_VERTEX"

        /** Fragment shader in lights assets folder */
        fun ltfs(name: String) = "$FOLDER_LIGHT/$name.$EXT_FRAGMENT"

        /** Vertex shader in shadows assets folder */
        fun swvs(name: String) = "$FOLDER_SHADOW/$name.$EXT_VERTEX"

        /** Fragment shader in shadows asset folder */
        fun swfs(name: String) = "$FOLDER_SHADOW/$name.$EXT_FRAGMENT"

        /** Vertex shader in particles assets folder */
        fun pevs(name: String) = "$FOLDER_PARTICLE/$name.$EXT_VERTEX"

        /** Fragment shader in particles assets folder */
        fun pefs(name: String) = "$FOLDER_PARTICLE/$name.$EXT_FRAGMENT"

        /** Vertex shader in textures assets folder */
        fun tevs(name: String) = "$FOLDER_TEXTURE/$name.$EXT_VERTEX"

        /** Fragment shader in textures assets folder */
        fun tefs(name: String) = "$FOLDER_TEXTURE/$name.$EXT_FRAGMENT"

        fun fromAssets(context: Context, vertexPath: String, fragmentPath: String): GLESShader {
            return GLESShader(context, vertexPath, fragmentPath)
        }

        private fun loadShaderSource(context: Context, name: String, type: Int): String {
            val am = context.assets
            val lines = ArrayList<String>()
            try {
                val br = BufferedReader(InputStreamReader(am.open(name)))
                br.use {
                    var ln: String?
                    do {
                        ln = br.readLine()
                        lines.add(ln)
                    } while (ln != null)
                }
            } catch (e: IOException) {
                Log.w(LOG_TAG, "Failed to load source from asset: $name")
            }
            return lines.joinToString("")
        }
    }

    private var vertexShader: String
    private var fragmentShader: String
    private var program: Int
    private val indexMap: HashMap<String, Int>
    private val pointerIndices: MutableSet<String>
    private val bufferObjects: MutableSet<Int>

    private constructor(vertex: String, fragment: String) {
        vertexShader = vertex
        fragmentShader = fragment
        indexMap = HashMap()
        pointerIndices = HashSet()
        bufferObjects = HashSet()
        program = loadProgram()
    }

    private constructor(context: Context, vertex: String, fragment: String) :
            this(loadShaderSource(context, vertex, GLES20.GL_VERTEX_SHADER),
                    loadShaderSource(context, fragment, GLES20.GL_FRAGMENT_SHADER))

    private fun loadProgram(): Int {
        val vsh = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fsh = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        val shaderProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(shaderProgram, vsh)
        GLES20.glAttachShader(shaderProgram, fsh)
        GLES20.glLinkProgram(shaderProgram)
        // Check link status
        val status = IntArray(1)
        GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            Log.e(LOG_TAG, "Link Shader " + GLES20.glGetProgramInfoLog(shaderProgram))
            GLES20.glDeleteProgram(shaderProgram)
            return 0
        }

        GLES20.glValidateProgram(shaderProgram)
        status[0] = GLES20.GL_FALSE
        GLES20.glGetProgramiv(shaderProgram, GLES20.GL_VALIDATE_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            Log.d(LOG_TAG, "Shader program error: " + GLES20.glGetProgramInfoLog(shaderProgram))
        }

        return shaderProgram
    }

    private fun compileShader(type: Int, source: String): Int {
        val sh = GLES20.glCreateShader(type)
        GLES20.glShaderSource(sh, source)
        GLES20.glCompileShader(sh)
        // Check shader compile status
        val status = IntArray(1)
        GLES20.glGetShaderiv(sh, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            Log.e(LOG_TAG, "Shader compile error: " + GLES20.glGetShaderInfoLog(sh))
            GLES20.glDeleteShader(sh)
            return 0
        }

        return sh
    }

    /**
     * Attach the shader to the OpenGL ES renderer context.
     */
    fun attach() {
        GLES20.glUseProgram(program)
    }

    fun detach() {
        GLES20.glDeleteProgram(program)
    }

    fun attribute(name: String): Int {
        if (name in indexMap) {
            return indexMap[name]!!
        }
        val a = GLES20.glGetAttribLocation(program, name)
        if (a < 0) {
            checkGLError(LOG_TAG, "Get attribute $name")
        }
        indexMap[name] = a
        return a
    }

    fun uniform(name: String): Int {
        if (name in indexMap) {
            return indexMap[name]!!
        }
        val u = GLES20.glGetUniformLocation(program, name)
        if (u < 0) {
            checkGLError(LOG_TAG, "Get uniform $name")
        }
        indexMap[name] = u
        return u
    }

    /**
     * Wrapper for glVertexAttrib1f.
     */
    fun attributeScalar(name: String, value: Float = 0f) {
        GLES20.glVertexAttrib1f(attribute(name), value)
    }
    /**
     * Wrapper for glVertexAttribNf API where N = [2, 3, 4]
     */
    fun attributeVector(name: String, x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 1f) {
        GLES20.glVertexAttrib4f(attribute(name), x, y, z, w)
    }

    /**
     * Wrapper for glUniform1f.
     */
    fun uniformScalar(name: String, value: Float = 0f) {
        GLES20.glUniform1f(uniform(name), value)
    }

    fun uniformArray(name: String, array: FloatArray) {
        if (array.size < 3) {
            GLES20.glUniform2f(uniform(name), array[0], array[1])
            return
        }
        val w = if (array.size > 3) array[3] else 1f
        uniformVector(name, array[0], array[1], array[2], w)
    }

    /**
     * Wrapper for glUniformNf API where N = [2, 3, 4]
     */
    fun uniformVector(name: String, x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 1f) {
        GLES20.glUniform4f(uniform(name), x, y, z, w)
    }

    /**
     * Wrapper for glVertexAttribPointer with FloatBuffer.
     * Since will pass FloatBuffer to vertex in most cases, make a wrapper to deal with that.
     * Cache the vertex attribute name, then release them after drawing.
     * @param name
     * @param pointer - the vertex buffer in FloatBuffer
     * @param count - the count of vertex data component must be [1, 4], default is 2(for 2D points)
     */
    fun attributePointer(name: String, pointer: FloatBuffer, count: Int = 2) {
        if (count < 1 || count > 4) {
            throw IllegalArgumentException("count must be [1, 4]: $count")
        }
        pointer.position(0)
        GLES20.glEnableVertexAttribArray(attribute(name))
        GLES20.glVertexAttribPointer(attribute(name), count, GLES20.GL_FLOAT, false, 0, pointer)
        pointerIndices.add(name)
    }

    /**
     * Bind the FloatBuffer to a vertex buffer object.
     */
    fun vertexBuffer(pointer: FloatBuffer) {
        val bufs = IntArray(1)
        pointer.position(0)
        GLES20.glGenBuffers(1, bufs, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufs[0])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, pointer.capacity() * GLESHelper.BYTES_OF_FLOAT, pointer, GLES20.GL_STATIC_DRAW)
        bufferObjects.add(bufs[0])
    }

    /**
     * Bind index buffer to a vertex buffer object.
     */
    fun indexBuffer(pointer: IntBuffer) {
        pointer.position(0)
        val bufs = IntArray(1)
        GLES20.glGenBuffers(1, bufs, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufs[0])
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, pointer.capacity() * BYTES_OF_INTEGER, pointer, GLES20.GL_STATIC_DRAW)
        bufferObjects.add(bufs[0])
    }

    /**
     * Set data to attribute from vertex buffer object bound with vertexBuffer.
     * @param name attribute name
     * @param count vertex component count must be [1, 4]
     * @param stride buffer component stride, in bytes
     * @param offset offset to current data
     */
    fun attributeBuffer(
        name: String,
        count: Int = GLESHelper.VERTEX_COMPONENT,
        stride: Int = GLESHelper.VERTEX_STRIDE,
        offset: Int = 0
    ) {
        if (count < 1 || count > 4) {
            throw IllegalArgumentException("Invalid count must be [1, 4]: $count")
        }
        GLES20.glEnableVertexAttribArray(attribute(name))
        GLES20.glVertexAttribPointer(attribute(name), count, GLES20.GL_FLOAT, false, stride, offset)
    }

    /**
     * Disable vertex attribute pointers that were enabled before.
     */
    fun disablePointers() {
        pointerIndices.forEach {
            GLES20.glDisableVertexAttribArray(attribute(it))
        }
    }

    /**
     * Clean (delete) all vertex buffer objects that were bound with #vertexBuffer.
     * See also {#vertexBuffer}
     */
    fun cleanBufferObjects() {
        val array = bufferObjects.toIntArray()
        GLES20.glDeleteBuffers(array.size, array, 0)
        bufferObjects.clear()
    }

    fun texture(name: String, id: Int, index: Int = 0, fillMode: Int = GLES20.GL_CLAMP_TO_EDGE) {
        GLES20.glActiveTexture(TEXTURE_UNITS[index])
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        GLES20.glUniform1i(uniform(name), index)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, fillMode)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, fillMode)
    }

    fun matrix(name: String, matrix: FloatArray) {
        GLES20.glUniformMatrix4fv(uniform(name), 0, false, matrix, 0)
    }
}