import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme

// Define Colors
val Purple80 = Color(0xFF6200EE)
val PurpleGrey80 = Color(0xFF3700B3)
val Pink80 = Color(0xFFFF4081)

val Purple40 = Color(0xFF03DAC5)
val PurpleGrey40 = Color(0xFF018786)
val Pink40 = Color(0xFF03DAC5)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    surface = Color(0xFF121212),
    onSurface = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000)
)

@Composable
fun QrCodeReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // Ensure Typography is defined elsewhere
        content = content
    )
}

fun MaterialTheme(colorScheme: ColorScheme, typography: Any, content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}


