package com.parithidb.zobazeassignment.util

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.google.android.material.transition.MaterialFadeThrough
import com.parithidb.zobazeassignment.R

object ThemeSwitcher {
    private const val PREFS_NAME = "app_theme_prefs"
    private const val KEY_IS_DARK = "is_dark_mode"

    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_DARK, false)
    }

    fun toggleTheme(context: Context, rootView: ViewGroup) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val newDarkMode = !isDarkMode(context)
        prefs.edit { putBoolean(KEY_IS_DARK, newDarkMode) }

        // Fade out
        androidx.transition.TransitionManager.beginDelayedTransition(rootView, MaterialFadeThrough().apply {
            duration = 150
        })

        rootView.alpha = 0f
        rootView.postDelayed({
            AppCompatDelegate.setDefaultNightMode(
                if (newDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }, 150)
    }


    fun applySavedTheme(context: Context) {
        val isDark = isDarkMode(context)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun getThemeIconRes(context: Context): Int {
        return if (isDarkMode(context)) {
            R.drawable.ic_light_mode_24 // show sun in dark mode
        } else {
            R.drawable.ic_dark_mode_24 // show moon in light mode
        }
    }

}
