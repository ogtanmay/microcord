package com.discord.utilities.theme

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.discord.utilities.cache.SharedPreferencesProvider

/**
 * Device-scoped liquid-glass controls shared by glass surfaces and their settings preview.
 *
 * Values are deliberately small and renderer-independent so the same settings can be used by
 * RealtimeBlurView-backed Views and future surfaces.
 */
object GlassAppearance {
    const val DEFAULT_BLUR_RADIUS = 18
    const val DEFAULT_OPACITY = 52
    const val DEFAULT_VIBRANCY = 100

    private const val KEY_ENABLED = "microcord_glass_enabled"
    private const val KEY_BLUR_RADIUS = "microcord_glass_blur_radius"
    private const val KEY_OPACITY = "microcord_glass_opacity"
    private const val KEY_VIBRANCY = "microcord_glass_vibrancy"

    data class State(
        val enabled: Boolean = true,
        val blurRadius: Int = DEFAULT_BLUR_RADIUS,
        val opacity: Int = DEFAULT_OPACITY,
        val vibrancy: Int = DEFAULT_VIBRANCY,
    )

    fun read(preferences: SharedPreferences = SharedPreferencesProvider.get()): State = State(
        enabled = preferences.getBoolean(KEY_ENABLED, true),
        blurRadius = preferences.getInt(KEY_BLUR_RADIUS, DEFAULT_BLUR_RADIUS).coerceIn(0, 40),
        opacity = preferences.getInt(KEY_OPACITY, DEFAULT_OPACITY).coerceIn(0, 100),
        vibrancy = preferences.getInt(KEY_VIBRANCY, DEFAULT_VIBRANCY).coerceIn(0, 200),
    )

    fun write(state: State, preferences: SharedPreferences = SharedPreferencesProvider.get()) {
        preferences.edit()
            .putBoolean(KEY_ENABLED, state.enabled)
            .putInt(KEY_BLUR_RADIUS, state.blurRadius.coerceIn(0, 40))
            .putInt(KEY_OPACITY, state.opacity.coerceIn(0, 100))
            .putInt(KEY_VIBRANCY, state.vibrancy.coerceIn(0, 200))
            .apply()
    }

    @VisibleForTesting
    internal fun keys(): Set<String> = setOf(KEY_ENABLED, KEY_BLUR_RADIUS, KEY_OPACITY, KEY_VIBRANCY)
}
