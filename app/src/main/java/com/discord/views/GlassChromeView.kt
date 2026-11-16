package com.discord.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.discord.utilities.cache.SharedPreferencesProvider
import com.discord.utilities.theme.GlassAppearance
import com.github.mmin18.widget.RealtimeBlurView

/**
 * Non-interactive glass chrome for the activity's top and bottom navigation areas.
 *
 * It intentionally does not blur the message/content region: blurring every pixel on top of a
 * chat makes text unreadable and is not how the Nerox-style composition is used.
 */
class GlassChromeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    private val topBlur = blurView()
    private val bottomBlur = blurView()
    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val preferences = SharedPreferencesProvider.get()
    private val preferenceListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key?.startsWith("microcord_glass_") == true) post(::applyState)
    }

    init {
        isClickable = false
        isFocusable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        addView(topBlur, LayoutParams(LayoutParams.MATCH_PARENT, dp(76), Gravity.TOP))
        addView(bottomBlur, LayoutParams(LayoutParams.MATCH_PARENT, dp(96), Gravity.BOTTOM))
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
        applyState()
    }

    override fun onDetachedFromWindow() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        super.onDetachedFromWindow()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val state = GlassAppearance.read(preferences)
        if (!state.enabled) return
        edgePaint.color = Color.argb((80 + state.vibrancy / 2).coerceAtMost(180), 255, 255, 255)
        edgePaint.strokeWidth = dp(1).toFloat()
        canvas.drawLine(0f, dp(76).toFloat(), width.toFloat(), dp(76).toFloat(), edgePaint)
        canvas.drawLine(0f, height - dp(96).toFloat(), width.toFloat(), height - dp(96).toFloat(), edgePaint)
    }

    private fun blurView() = RealtimeBlurView(context, null).apply {
        setOverlayColor(Color.argb(48, 255, 255, 255))
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    private fun applyState() {
        val state = GlassAppearance.read(preferences)
        visibility = if (state.enabled) VISIBLE else GONE
        val radius = state.blurRadius.toFloat()
        topBlur.setBlurRadius(radius)
        bottomBlur.setBlurRadius(radius)
        val overlay = Color.argb((state.opacity * 255 / 100).coerceIn(0, 255), 255, 255, 255)
        topBlur.setOverlayColor(overlay)
        bottomBlur.setOverlayColor(overlay)
        invalidate()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
