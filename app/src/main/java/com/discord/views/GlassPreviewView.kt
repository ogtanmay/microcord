package com.discord.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import com.discord.utilities.theme.GlassAppearance

/** Small self-contained preview used while tuning the app-wide glass settings. */
class GlassPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val surfacePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val surface = RectF()
    private var state = GlassAppearance.State()

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setState(state: GlassAppearance.State) {
        this.state = state
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        backgroundPaint.shader = LinearGradient(
            0f,
            0f,
            width,
            height,
            intArrayOf(Color.rgb(28, 35, 65), Color.rgb(85, 46, 112), Color.rgb(18, 87, 100)),
            null,
            Shader.TileMode.CLAMP,
        )
        canvas.drawRect(0f, 0f, width, height, backgroundPaint)

        surface.set(width * 0.08f, height * 0.12f, width * 0.92f, height * 0.88f)
        val alpha = if (state.enabled) state.opacity * 255 / 100 else 255
        surfacePaint.color = ColorUtils.setAlphaComponent(Color.WHITE, alpha)
        surfacePaint.setShadowLayer(
            10f + state.vibrancy / 20f,
            0f,
            5f,
            ColorUtils.setAlphaComponent(Color.BLACK, 100),
        )
        canvas.drawRoundRect(surface, 24f, 24f, surfacePaint)
        surfacePaint.clearShadowLayer()

        highlightPaint.style = Paint.Style.STROKE
        highlightPaint.strokeWidth = 2f
        highlightPaint.color = ColorUtils.setAlphaComponent(Color.WHITE, 80 + state.vibrancy / 4)
        canvas.drawRoundRect(surface, 24f, 24f, highlightPaint)

        val pill = RectF(surface.left + 18f, surface.top + 22f, surface.right - 18f, surface.top + 58f)
        surfacePaint.color = ColorUtils.setAlphaComponent(Color.BLACK, 45)
        canvas.drawRoundRect(pill, 18f, 18f, surfacePaint)
        surfacePaint.color = ColorUtils.setAlphaComponent(Color.WHITE, 200)
        canvas.drawCircle(pill.left + 20f, pill.centerY(), 8f, surfacePaint)
        canvas.drawRoundRect(
            RectF(pill.left + 38f, pill.top + 12f, pill.right - 80f, pill.top + 17f),
            3f,
            3f,
            surfacePaint,
        )
        canvas.drawRoundRect(
            RectF(pill.left + 38f, pill.top + 21f, pill.right - 125f, pill.top + 26f),
            3f,
            3f,
            surfacePaint,
        )
    }
}
