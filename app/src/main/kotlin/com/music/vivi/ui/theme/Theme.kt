/**
 * vivimusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.music.vivi.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

import androidx.compose.runtime.getValue
import com.music.vivi.constants.SelectedFontKey
import com.music.vivi.constants.AppFont
import com.music.vivi.constants.GoldenHackerKey
import com.music.vivi.utils.rememberPreference
import androidx.compose.ui.text.font.FontFamily

val DefaultThemeColor = Color(0xFFED5564)

val GoldenHackerGold = Color(0xFFD4AF37)
val GoldenHackerObsidianBlack = Color(0xFF0A0A0C)
val GoldenHackerSurfaceDark = Color(0xFF141418)
val GoldenHackerSurfaceLight = Color(0xFFF4F4F6)
val GoldenHackerMuted = Color(0xFF8E8E93)

fun goldenHackerDarkColorScheme() = darkColorScheme(
    primary = GoldenHackerGold,
    onPrimary = GoldenHackerObsidianBlack,
    primaryContainer = Color(0xFF3D3400),
    onPrimaryContainer = Color(0xFFFFDEA1),
    secondary = Color(0xFFE4BF5A),
    onSecondary = Color(0xFF3B2E00),
    secondaryContainer = Color(0xFF544400),
    onSecondaryContainer = Color(0xFFFFDEA1),
    tertiary = GoldenHackerMuted,
    onTertiary = Color(0xFF2E2E30),
    tertiaryContainer = Color(0xFF454547),
    onTertiaryContainer = Color(0xFFCBCBD0),
    background = GoldenHackerObsidianBlack,
    onBackground = Color(0xFFE5E1E6),
    surface = GoldenHackerSurfaceDark,
    onSurface = Color(0xFFE5E1E6),
    surfaceVariant = Color(0xFF1C1C20),
    onSurfaceVariant = Color(0xFFC8C5CA),
    surfaceContainerLow = Color(0xFF111114),
    surfaceContainer = Color(0xFF181820),
    surfaceContainerHigh = Color(0xFF1E1E24),
    surfaceContainerHighest = Color(0xFF262630),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = GoldenHackerMuted,
    outlineVariant = Color(0xFF44474F),
    inverseSurface = Color(0xFFE5E1E6),
    inverseOnSurface = Color(0xFF323033),
    inversePrimary = Color(0xFF5B4400),
)

fun goldenHackerLightColorScheme() = lightColorScheme(
    primary = Color(0xFF7B5700),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDEA1),
    onPrimaryContainer = Color(0xFF271900),
    secondary = Color(0xFF6B5B28),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF4DF99),
    onSecondaryContainer = Color(0xFF221B00),
    tertiary = Color(0xFF5F5E61),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE7E1E6),
    onTertiaryContainer = Color(0xFF1C1C1F),
    background = GoldenHackerSurfaceLight,
    onBackground = Color(0xFF1C1B1E),
    surface = GoldenHackerSurfaceLight,
    onSurface = Color(0xFF1C1B1E),
    surfaceVariant = Color(0xFFE7E0E5),
    onSurfaceVariant = Color(0xFF49474C),
    surfaceContainerLow = Color(0xFFF8F8FA),
    surfaceContainer = Color(0xFFF0F0F2),
    surfaceContainerHigh = Color(0xFFE8E8EA),
    surfaceContainerHighest = Color(0xFFE0E0E2),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    outline = Color(0xFF797780),
    outlineVariant = Color(0xFFC9C5CA),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF3F0F4),
    inversePrimary = Color(0xFFFFDEA1),
)

@Composable
fun vivimusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    goldenHacker: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val selectedFontValue by rememberPreference(SelectedFontKey, AppFont.SYSTEM.value)

    val brandFont = remember(selectedFontValue) {
        when (AppFont.fromValue(selectedFontValue)) {
            AppFont.SYSTEM -> FontFamily.Default
            AppFont.GOOGLE_SANS -> GoogleSansFontFamily
            AppFont.SANS_FLEX -> SansFlexFontFamily
            AppFont.OUTFIT -> OutfitFontFamily
            AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
        }
    }

    val typography = remember(brandFont) {
        getTypography(brandFont = brandFont, plainFont = brandFont)
    }

    val baseColorScheme = if (goldenHacker) {
        if (darkTheme) goldenHackerDarkColorScheme() else goldenHackerLightColorScheme()
    } else {
        val useSystemDynamicColor = (themeColor == DefaultThemeColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        if (useSystemDynamicColor) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            rememberDynamicColorScheme(
                seedColor = themeColor,
                isDark = darkTheme,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
                style = if (themeColor.toArgb() == 0xFF000000.toInt()) PaletteStyle.Monochrome else PaletteStyle.TonalSpot
            )
        }
    }

    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme, goldenHacker) {
        if (darkTheme && pureBlack && !goldenHacker) {
            baseColorScheme.pureBlack(true)
        } else {
            baseColorScheme
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}

fun Bitmap.extractThemeColor(): Color {
    val colorsToPopulation = Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .swatches
        .associate { it.rgb to it.population }
    val rankedColors = Score.score(colorsToPopulation)
    return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
    val extractedColors = Palette.from(this)
        .maximumColorCount(64)
        .generate()
        .swatches
        .associate { it.rgb to it.population }

    val orderedColors = Score.score(extractedColors, 2, 0xff4285f4.toInt(), true)
        .sortedByDescending { Color(it).luminance() }

    return if (orderedColors.size >= 2)
        listOf(Color(orderedColors[0]), Color(orderedColors[1]))
    else
        listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}

@Composable
fun rememberGoldenHacker(): Boolean {
    val (goldenHacker) = rememberPreference(GoldenHackerKey, defaultValue = false)
    return goldenHacker
}

@Composable
fun Modifier.goldenBorder(isActive: Boolean = rememberGoldenHacker()): Modifier {
    if (!isActive) return this
    return this.then(
        Modifier.border(
            border = BorderStroke(1.dp, GoldenHackerGold.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(20.dp)
        )
    )
}
