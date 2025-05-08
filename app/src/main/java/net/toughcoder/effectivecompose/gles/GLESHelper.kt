package net.toughcoder.effectivecompose.gles

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLU
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import kotlin.math.sqrt

class GLESHelper {
    companion object {
        const val BYTES_OF_FLOAT = 4
        const val BYTES_OF_INTEGER = 4
        const val BYTES_OF_SHORT = 2
        const val INVALID_TEXTURE_ID = -1
        const val OPEN_GLES_2 = 0x00020000
        const val OPEN_GLES_3 = 0x00030000
        const val OPEN_GLES_3_1 = 0x00030001
        const val OPEN_GLES_3_2 = 0x00030002
        const val MATRIX_COMPONENT = 16
        const val MATRIX_STRIDE = MATRIX_COMPONENT * BYTES_OF_FLOAT
        const val TEX2D_COORD_COMPONENT = 2
        const val TEX2D_COORD_STRIDE = 2 * BYTES_OF_FLOAT
        const val VERTEX2D_COMPONENT = 2
        const val VERTEX2D_STRIDE = VERTEX2D_COMPONENT * BYTES_OF_FLOAT
        const val VERTEX_COMPONENT = 3
        const val VERTEX_STRIDE = VERTEX_COMPONENT * BYTES_OF_FLOAT
        const val COLOR_COMPONENT = 3
        const val COLOR_STRIDE = COLOR_COMPONENT * BYTES_OF_FLOAT
        const val FULL_COLOR_COMPONENT = 4
        const val FULL_COLOR_STRIDE = FULL_COLOR_COMPONENT * BYTES_OF_FLOAT

        // Background colors
        private val BLACK = floatArrayOf(0.04f, 0.04f, 0.04f)
        private val WHITE = floatArrayOf(0.96f, 0.96f, 0.96f)
        private val GRAY = floatArrayOf(0.32f, 0.32f, 0.32f)
        private val YELLOW = floatArrayOf(0.97f, 0.86f, 0.42f)
        private val ORANGE = floatArrayOf(0.94f, 0.63f, 0.29f)
        private val BLUE = floatArrayOf(0.05f, 0.64f, 0.58f)
        private val GREEN = floatArrayOf(0.49f, 0.66f, 0.45f)
        private val RED = floatArrayOf(0.84f, 0.07f, 0.33f)
        private val COFFEE = floatArrayOf(0.66f, 0.56f, 0.49f)
        val BG_COLORS = arrayOf(BLACK, WHITE, GRAY, RED, GREEN, BLUE, YELLOW, ORANGE, COFFEE)
        val BG_COLOR_NAMES = arrayOf(
            "Black 黑色", "White 白色", "Gray 灰色",
            "Red 红色", "Green 绿色", "Blue 蓝色",
            "Yellow 黄色", "Orange 橙色", "Coffee 咖啡色"
        )

        val TEXTURE_UNITS = intArrayOf(
                GLES20.GL_TEXTURE0,
                GLES20.GL_TEXTURE1,
                GLES20.GL_TEXTURE2,
                GLES20.GL_TEXTURE3,
                GLES20.GL_TEXTURE4,
                GLES20.GL_TEXTURE5,
                GLES20.GL_TEXTURE6,
                GLES20.GL_TEXTURE7,
                GLES20.GL_TEXTURE8,
                GLES20.GL_TEXTURE9,
                GLES20.GL_TEXTURE10
        )

        fun conformGLES(context: Context, version: Int = OPEN_GLES_2): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val config = am.deviceConfigurationInfo
            return config.reqGlEsVersion >= version
        }

        fun checkGLError(tag: String, msg: String = "Error") {
            val error = GLES20.glGetError()
            if (error != GLES20.GL_NO_ERROR) {
                Log.d(tag, "$msg: 0x" + error.toString(16) + ": " + GLU.gluErrorString(error))
            }
        }

        fun floatBuffer(array: FloatArray): FloatBuffer {
            val buffer = ByteBuffer.allocateDirect(array.size * BYTES_OF_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
            buffer.put(array).position(0)
            return buffer
        }

        fun intBuffer(array: IntArray): IntBuffer {
            val buffer = ByteBuffer.allocateDirect(array.size * BYTES_OF_INTEGER)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer()
            buffer.put(array).position(0)
            return buffer
        }

        fun shortBuffer(array: ShortArray): ShortBuffer {
            val buffer = ByteBuffer.allocateDirect(array.size * BYTES_OF_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
            buffer.put(array).position(0)
            return buffer
        }

        fun matrixBuffer() = ByteBuffer.allocateDirect(MATRIX_STRIDE).order(ByteOrder.nativeOrder()).asFloatBuffer()

        fun matrix(): FloatArray {
            val matrix = FloatArray(16)
            Matrix.setIdentityM(matrix, 0)
            return matrix
        }

        fun textureFrom(bitmap: Bitmap?): Int {
            if (bitmap == null) {
                return INVALID_TEXTURE_ID
            }
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1)
            val tex = IntArray(1)
            GLES20.glGenTextures(1, tex, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0])

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

            return tex[0]
        }

        fun calcNormal(p0: FloatArray, p1: FloatArray, p2: FloatArray): FloatArray {
            // p0 to p1
            val v1 = floatArrayOf(0f, 0f, 0f)
            // p1 to p2
            val v2 = floatArrayOf(0f, 0f, 0f)
            for (i in 0..2) {
                v1[i] = p1[i] - p0[i]
                v2[i] = p2[i] - p1[i]
            }

            return calcNormal(v1, v2)
        }

        private fun calcNormal(v1: FloatArray, v2: FloatArray): FloatArray {
            val cross = floatArrayOf(0f, 0f, 0f)
            cross[0] = v1[1] * v2[2] - v1[2] * v2[1]
            cross[1] = v1[2] * v2[0] - v1[0] * v2[2]
            cross[2] = v1[0] * v2[1] - v1[1] * v2[0]

            return normalize(cross)
        }

        fun normalize(vector: FloatArray): FloatArray {
            val d = sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2])
            return floatArrayOf(
                vector[0] / d, vector[1] / d, vector[2] / d
            )
        }

        fun calcNormalMatrix(matrix: FloatArray): FloatArray {
            val temp = matrix()
            val result = matrix()
            Matrix.invertM(temp, 0, matrix, 0)
            Matrix.transposeM(result, 0, temp, 0)
            return result
        }

        /**
         * Must:
         *   width == height
         *   the size of pixels is 6, each sub array of pixels is the same size
         */
        fun genCubemapTexture(
            width: Int, height: Int, stride: Int,
            fillBuffer: (Int, ByteBuffer)->Unit
        ): Int {
            val tex = intArrayOf(INVALID_TEXTURE_ID)
            GLES20.glGenTextures(1, tex, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, tex[0])

            val format = when (stride) {
                4 -> GLES20.GL_RGBA
                else -> GLES20.GL_RGB
            }

            // Format is RGB, each channel is a byte, so total size is w * h * 3
            val buf = ByteBuffer
                .allocateDirect(width * height * stride)
                .order(ByteOrder.nativeOrder())
            fillBuffer(0, buf)
            buf.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, format,
                width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, buf
            )
            fillBuffer(1, buf)
            buf.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, format,
                width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, buf
            )

            fillBuffer(2, buf)
            buf.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, format,
                width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, buf
            )
            fillBuffer(3, buf)
            buf.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, format,
                width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, buf
            )

            fillBuffer(4, buf)
            buf.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, format,
                width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, buf
            )
            fillBuffer(5, buf)
            buf.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, format,
                width, height, 0, format,
                GLES20.GL_UNSIGNED_BYTE, buf
            )

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
            )
            return tex[0]
        }
    }
}

