package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class CivilSemanticColors(
    val gridLine: Color,
    val gridMajorLine: Color,
    val momentCurve: Color,
    val shearCurve: Color,
    val steelAccent: Color,
    val concreteAccent: Color,
    val successAccent: Color,
    val warningAccent: Color
)

val LocalCivilColors = staticCompositionLocalOf {
    CivilSemanticColors(
        gridLine = Color(0x14155A96),
        gridMajorLine = Color(0x28155A96),
        momentCurve = Color(0xFFD96914),
        shearCurve = Color(0xFF155A96),
        steelAccent = Color(0xFF155A96),
        concreteAccent = Color(0xFF64748B),
        successAccent = Color(0xFF187A56),
        warningAccent = Color(0xFFD96914)
    )
}

private val LightColorScheme = lightColorScheme(
    primary = BlueprintLightPrimary,
    onPrimary = BlueprintLightOnPrimary,
    primaryContainer = BlueprintLightPrimaryContainer,
    onPrimaryContainer = BlueprintLightOnPrimaryContainer,
    secondary = BlueprintLightSecondary,
    onSecondary = BlueprintLightOnSecondary,
    secondaryContainer = BlueprintLightSecondaryContainer,
    onSecondaryContainer = BlueprintLightOnSecondaryContainer,
    tertiary = BlueprintLightTertiary,
    tertiaryContainer = BlueprintLightTertiaryContainer,
    background = BlueprintLightBackground,
    onBackground = BlueprintLightOnSurface,
    surface = BlueprintLightSurface,
    onSurface = BlueprintLightOnSurface,
    surfaceVariant = BlueprintLightSurfaceVariant,
    onSurfaceVariant = BlueprintLightOnSurfaceVariant,
    outline = BlueprintLightOutline,
    error = BlueprintLightError
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueprintDarkPrimary,
    onPrimary = BlueprintDarkOnPrimary,
    primaryContainer = BlueprintDarkPrimaryContainer,
    onPrimaryContainer = BlueprintDarkOnPrimaryContainer,
    secondary = BlueprintDarkSecondary,
    onSecondary = BlueprintDarkOnSecondary,
    secondaryContainer = BlueprintDarkSecondaryContainer,
    onSecondaryContainer = BlueprintDarkOnSecondaryContainer,
    tertiary = BlueprintDarkTertiary,
    tertiaryContainer = BlueprintDarkTertiaryContainer,
    background = BlueprintDarkBackground,
    onBackground = BlueprintDarkOnSurface,
    surface = BlueprintDarkSurface,
    onSurface = BlueprintDarkOnSurface,
    surfaceVariant = BlueprintDarkSurfaceVariant,
    onSurfaceVariant = BlueprintDarkOnSurfaceVariant,
    outline = BlueprintDarkOutline,
    error = BlueprintDarkError
)

private val CivilShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun CivilCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val semanticColors = if (darkTheme) {
        CivilSemanticColors(
            gridLine = Color(0x1456B2FF),
            gridMajorLine = Color(0x2856B2FF),
            momentCurve = Color(0xFFFF9642),
            shearCurve = Color(0xFF56B2FF),
            steelAccent = Color(0xFF56B2FF),
            concreteAccent = Color(0xFF8AA0B8),
            successAccent = Color(0xFF4CE0A2),
            warningAccent = Color(0xFFFF9642)
        )
    } else {
        CivilSemanticColors(
            gridLine = Color(0x12155A96),
            gridMajorLine = Color(0x24155A96),
            momentCurve = Color(0xFFD96914),
            shearCurve = Color(0xFF155A96),
            steelAccent = Color(0xFF155A96),
            concreteAccent = Color(0xFF5C7085),
            successAccent = Color(0xFF187A56),
            warningAccent = Color(0xFFD96914)
        )
    }

    CompositionLocalProvider(LocalCivilColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = CivilShapes,
            content = content
        )
    }
}
