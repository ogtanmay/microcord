package com.discord.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.discord.utilities.cache.SharedPreferencesProvider
import com.discord.utilities.theme.GlassAppearance

/** A non-interactive rounded glass rim that can decorate existing bars without changing geometry. */
class GlassPillOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF()
    private val preferences = SharedPreferencesProvider.get()
    private val preferenceListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key?.startsWith("microcord_glass_") == true) postInvalidate()
    }

    init {
        isClickable = false
        isFocusable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onDetachedFromWindow() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        val state = GlassAppearance.read(preferences)
        if (!state.enabled || width <= 0 || height <= 0) return
        val inset = dp(4).toFloat()
        bounds.set(inset, inset, width - inset, height - inset)
        val radius = (height / 2f).coerceAtLeast(dp(16).toFloat())

        paint.style = Paint.Style.FILL
        paint.color = Color.argb((state.opacity * 42 / 100).coerceIn(0, 42), 255, 255, 255)
        canvas.drawRoundRect(bounds, radius, radius, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dp(1).toFloat()
        paint.color = Color.argb((70 + state.vibrancy / 2).coerceAtMost(180), 255, 255, 255)
        canvas.drawRoundRect(bounds, radius, radius, paint)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
