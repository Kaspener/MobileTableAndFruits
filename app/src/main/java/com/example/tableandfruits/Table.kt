package com.example.tableandfruits

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Table(private val context: Context) {
    private lateinit var shaderProgram: ShaderProgram
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer

    private var textureId: Int = 0

    // Вершины для столешницы и ножек
    private val vertices = floatArrayOf(
        // Столешница
        -2f, 0.1f, 2f,   // 0: передняя левая верх
        -2f, 0.1f, -2f,  // 1: задняя левая верх
        2f, 0.1f, -2f,   // 2: задняя правая верх
        2f, 0.1f, 2f,    // 3: передняя правая верх
        -2f, 0f, 2f,     // 4: передняя левая низ
        -2f, 0f, -2f,    // 5: задняя левая низ
        2f, 0f, -2f,     // 6: задняя правая низ
        2f, 0f, 2f,      // 7: передняя правая низ

        // Левая передняя ножка
        -1.4f, 0f, 1.4f,     // 8: верхняя передняя левая
        -1.4f, -1f, 1.4f,    // 9: нижняя передняя левая
        -1.2f, -1f, 1.4f,    // 10: нижняя передняя правая
        -1.2f, 0f, 1.4f,     // 11: верхняя передняя правая
        -1.4f, 0f, 1.6f,     // 12: верхняя передняя левая (дополнительная точка для отрисовки куба)
        -1.4f, -1f, 1.6f,    // 13: нижняя передняя левая (дополнительная точка для отрисовки куба)
        -1.2f, -1f, 1.6f,    // 14: нижняя передняя правая (дополнительная точка для отрисовки куба)
        -1.2f, 0f, 1.6f,     // 15: верхняя передняя правая (дополнительная точка для отрисовки куба)

        // Правая передняя ножка
        1.4f, 0f, 1.4f,      // 16: верхняя передняя левая
        1.4f, -1f, 1.4f,     // 17: нижняя передняя левая
        1.2f, -1f, 1.4f,     // 18: нижняя передняя правая
        1.2f, 0f, 1.4f,      // 19: верхняя передняя правая
        1.4f, 0f, 1.6f,      // 20: верхняя передняя левая (дополнительная точка для отрисовки куба)
        1.4f, -1f, 1.6f,     // 21: нижняя передняя левая (дополнительная точка для отрисовки куба)
        1.2f, -1f, 1.6f,     // 22: нижняя передняя правая (дополнительная точка для отрисовки куба)
        1.2f, 0f, 1.6f,      // 23: верхняя передняя правая (дополнительная точка для отрисовки куба)

        // Левая задняя ножка
        -1.4f, 0f, -1.4f,    // 24: верхняя задняя левая
        -1.4f, -1f, -1.4f,   // 25: нижняя задняя левая
        -1.2f, -1f, -1.4f,   // 26: нижняя задняя правая
        -1.2f, 0f, -1.4f,    // 27: верхняя задняя правая
        -1.4f, 0f, -1.6f,    // 28: верхняя задняя левая (дополнительная точка для отрисовки куба)
        -1.4f, -1f, -1.6f,   // 29: нижняя задняя левая (дополнительная точка для отрисовки куба)
        -1.2f, -1f, -1.6f,   // 30: нижняя задняя правая (дополнительная точка для отрисовки куба)
        -1.2f, 0f, -1.6f,    // 31: верхняя задняя правая (дополнительная точка для отрисовки куба)

        // Правая задняя ножка
        1.4f, 0f, -1.4f,     // 32: верхняя задняя левая
        1.4f, -1f, -1.4f,    // 33: нижняя задняя левая
        1.2f, -1f, -1.4f,    // 34: нижняя задняя правая
        1.2f, 0f, -1.4f,     // 35: верхняя задняя правая
        1.4f, 0f, -1.6f,     // 36: верхняя задняя левая (дополнительная точка для отрисовки куба)
        1.4f, -1f, -1.6f,    // 37: нижняя задняя левая (дополнительная точка для отрисовки куба)
        1.2f, -1f, -1.6f,    // 38: нижняя задняя правая (дополнительная точка для отрисовки куба)
        1.2f, 0f, -1.6f      // 39: верхняя задняя правая (дополнительная точка для отрисовки куба)
    )

    // Текстурные координаты
    private val texCoords = floatArrayOf(
        0f, 0f,  // Нижний левый
        0f, 1f,  // Верхний левый
        1f, 1f,  // Верхний правый
        1f, 0f   // Нижний правый
    )

    // Индексы для отрисовки
    private val indices = shortArrayOf(
        // Столешница
        0, 1, 2, 0, 2, 3,   // Верх
        4, 5, 6, 4, 6, 7,   // Низ
        0, 1, 4, 0, 4, 5,   // Левый
        3, 2, 6, 3, 6, 7,   // Правый
        0, 3, 7, 0, 7, 4,   // Передний
        1, 2, 6, 1, 6, 5,   // Задний

        // Ножки
        // Левая передняя ножка
        8, 9, 10, 8, 10, 11,
        12, 13, 14, 12, 14, 15,
        8, 9, 12, 12, 9, 13,
        11, 10, 14, 11, 14, 15,
        8, 11, 12, 11, 13, 12,
        9, 10, 14, 9, 14, 13,
        // Правая передняя ножка
        16, 17, 18, 16, 18, 19,
        20, 21, 22, 20, 22, 23,
        16, 17, 20, 16, 20, 21,
        19, 18, 22, 19, 22, 23,
        16, 19, 23, 16, 23, 20,
        17, 18, 22, 17, 22, 21,

        // Левая задняя ножка
        24, 25, 26, 24, 26, 27,
        28, 29, 30, 28, 30, 31,
        24, 25, 28, 24, 28, 29,
        27, 26, 30, 27, 30, 31,
        24, 27, 31, 24, 31, 28,
        25, 26, 30, 25, 30, 29,
        // Правая задняя ножка
        32, 33, 34, 32, 34, 35,
        36, 37, 38, 36, 38, 39,
        32, 33, 36, 32, 36, 37,
        35, 34, 38, 35, 38, 39,
        32, 35, 39, 32, 39, 36,
        33, 34, 38, 33, 38, 37
    )

    init {
        shaderProgram = ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        // Создаем буфер для вершин
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        // Создаем буфер для текстурных координат
        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4 * 6).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                for (i in 0 until 6) {
                    put(texCoords)
                }
                position(0)
            }
        }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(indices)
                position(0)
            }
        }

        textureId = loadTexture(R.drawable.wood_texture)
    }

    private fun loadTexture(resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        return textureId
    }

    fun draw(mvpMatrix: FloatArray) {
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "aPosition")
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "aTexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uMVPMatrix")
        val textureHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uTexture")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            vertexBuffer
        )

        texCoordBuffer.position(0)
        GLES20.glVertexAttribPointer(
            texCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            2 * 4,
            texCoordBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    companion object {
        private val VERTEX_SHADER_CODE = """
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        uniform mat4 uMVPMatrix;
        varying vec2 vTexCoord;
        void main() {
            vTexCoord = aTexCoord;
            gl_Position = uMVPMatrix * aPosition;
        }
        """.trimIndent()

        private val FRAGMENT_SHADER_CODE = """
        precision mediump float;
        uniform sampler2D uTexture;
        varying vec2 vTexCoord;
        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
        }
        """.trimIndent()
    }
}