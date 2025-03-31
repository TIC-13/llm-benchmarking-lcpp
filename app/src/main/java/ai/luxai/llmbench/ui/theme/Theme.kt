package ai.luxai.llmbench.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

object CustomColorScheme {
    private val Primary = Color(0xFF445E91)
    private val OnPrimary = Color(0xFFFFFFFF)
    private val PrimaryContainer = Color(0xFFD8E2FF)
    private val OnPrimaryContainer = Color(0xFF001A41)
    private val InversePrimary = Color(0xFFADC6FF)
    private val Secondary = Color(0xFF575E71)
    private val OnSecondary = Color(0xFFFFFFFF)
    private val SecondaryContainer = Color(0xFFDBE2F9)
    private val OnSecondaryContainer = Color(0xFF141B2C)
    private val Tertiary = Color(0xFF715573)
    private val OnTertiary = Color(0xFFFFFFFF)
    private val TertiaryContainer = Color(0xFFFBD7FC)
    private val OnTertiaryContainer = Color(0xFF29132D)
    private val Error = Color(0xFFB3261E)
    private val OnError = Color(0xFFFFFFFF)
    private val ErrorContainer = Color(0xFFF9DEDC)
    private val OnErrorContainer = Color(0xFF410E0B)
    private val Background = Color(0xFFFAF9FD)
    private val OnBackground = Color(0xFF1B1B1F)
    private val Surface = Color(0xFFFAF9FD)
    private val OnSurface = Color(0xFF1B1B1F)
    private val InverseSurface = Color(0xFF121316)
    private val InverseOnSurface = Color(0xFFE3E2E6)
    private val SurfaceVariant = Color(0xFFE1E2EC)
    private val OnSurfaceVariant = Color(0xFF44474F)
    private val Outline = Color(0xFF72747D)

    val LightColors = lightColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        primaryContainer = PrimaryContainer,
        onPrimaryContainer = OnPrimaryContainer,
        inversePrimary = InversePrimary,
        secondary = Secondary,
        onSecondary = OnSecondary,
        secondaryContainer = SecondaryContainer,
        onSecondaryContainer = OnSecondaryContainer,
        tertiary = Tertiary,
        onTertiary = OnTertiary,
        tertiaryContainer = TertiaryContainer,
        onTertiaryContainer = OnTertiaryContainer,
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,
        background = Background,
        onBackground = OnBackground,
        surface = Surface,
        onSurface = OnSurface,
        inverseSurface = InverseSurface,
        inverseOnSurface = InverseOnSurface,
        surfaceVariant = SurfaceVariant,
        onSurfaceVariant = OnSurfaceVariant,
        outline = Outline
    )
}

@Composable
fun LLMBenchmarkingTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CustomColorScheme.LightColors
        else -> CustomColorScheme.LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}