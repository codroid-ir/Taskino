package ir.codroid.taskino.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleUtil {

    fun setLocale(context: Context, language: String) =
        updateResource(context = context, language = language)

    private fun updateResource(context: Context, language: String) {
        context.resources.apply {
            val locale = Locale(language)
            val config = Configuration(configuration)
            context.createConfigurationContext(configuration)
            Locale.setDefault(locale)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, displayMetrics)
        }
    }
}