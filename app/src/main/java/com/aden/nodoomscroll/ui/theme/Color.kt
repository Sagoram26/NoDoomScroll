package com.aden.nodoomscroll.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette from docs/design/style-system.md (dark = primary target). Where style-system and the
 * .dc.html mockups disagreed, the mockups (newer) won — see design-token extraction notes.
 */
data class NdsColors(
    val bgBase: Color,
    val gradTop: Color,
    val gradMid: Color,
    val gradBottom: Color,
    val glassFill: Color,
    val glassRimTop: Color,
    val glassSheen: Color,
    val glassBorderTop: Color,
    val glassBorderBottom: Color,
    val accent: Color,
    val accent2: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val blocked: Color,
    val warn: Color,
    val warnBg: Color,
    val warnBorder: Color,
    val streakGlow: Color,
    val emphBg: Color,
    val isDark: Boolean,
)

val DarkColors = NdsColors(
    bgBase = Color(0xFF070C09),
    gradTop = Color(0xFF090F0B),
    gradMid = Color(0xFF0C1611),
    gradBottom = Color(0xFF101F17),
    glassFill = Color(0x522E4237),          // rgba(46,66,55,0.32)
    glassRimTop = Color(0x8CCDFFE0),         // rgba(205,255,224,0.55)
    glassSheen = Color(0x2EC8F5DE),          // rgba(200,245,222,0.18)
    glassBorderTop = Color(0xA6C8E6D4),      // rgba(200,230,212,0.65)
    glassBorderBottom = Color(0x4D9CC4AC),   // rgba(156,196,172,0.30)
    accent = Color(0xFF9CC4AC),
    accent2 = Color(0xFFB4D6C2),
    accentSoft = Color(0xFF3A5446),
    onAccent = Color(0xFF0F1712),
    textPrimary = Color(0xFFEAF2ED),
    textSecondary = Color(0xFFA7B8AE),
    textTertiary = Color(0xFF6E7E75),
    blocked = Color(0xFF8A7D72),
    warn = Color(0xFFD4B483),
    warnBg = Color(0x1AD4B483),              // rgba(212,180,131,0.10)
    warnBorder = Color(0x47D4B483),          // rgba(212,180,131,0.28)
    streakGlow = Color(0xE09CC8AE),          // rgba(156,200,174,0.88)
    emphBg = Color(0x663A5446),              // rgba(58,84,70,0.4)
    isDark = true,
)

val LightColors = NdsColors(
    bgBase = Color(0xFFF4FAF6),
    gradTop = Color(0xFFF6FBF8),
    gradMid = Color(0xFFEEF7F1),
    gradBottom = Color(0xFFE3F1E9),
    glassFill = Color(0x47FFFFFF),           // rgba(255,255,255,0.28)
    glassRimTop = Color(0xCCFFFFFF),         // rgba(255,255,255,0.80)
    glassSheen = Color(0x61FFFFFF),          // rgba(255,255,255,0.38)
    glassBorderTop = Color(0xF2FFFFFF),      // rgba(255,255,255,0.95)
    glassBorderBottom = Color(0x80FFFFFF),   // rgba(255,255,255,0.50)
    accent = Color(0xFF5E8C72),
    accent2 = Color(0xFF4A7560),
    accentSoft = Color(0xFFA8C9B5),
    onAccent = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF22302A),
    textSecondary = Color(0xFF5A6B62),
    textTertiary = Color(0xFF8A9890),
    blocked = Color(0xFF9A8A7E),
    warn = Color(0xFFC99A5B),
    warnBg = Color(0x1FC99A5B),              // rgba(201,154,91,0.12)
    warnBorder = Color(0x59C99A5B),          // rgba(201,154,91,0.35)
    streakGlow = Color(0x525E8C72),          // rgba(94,140,114,0.32)
    emphBg = Color(0x38A8C9B5),              // rgba(168,201,181,0.22)
    isDark = false,
)
