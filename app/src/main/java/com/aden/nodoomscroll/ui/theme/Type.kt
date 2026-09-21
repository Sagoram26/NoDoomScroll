package com.aden.nodoomscroll.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ponytail: design calls for Plus Jakarta Sans (display) + Nunito Sans (body). Using the platform
// sans-serif at the specified weights instead of Downloadable Fonts — avoids a Play-Services runtime
// dependency + cert plumbing for a personal app. Bundle the real .ttf in res/font/ if brand-exact
// type matters.
private val Display = FontFamily.SansSerif
private val Body = FontFamily.SansSerif

val NdsType = Typography(
    displayLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, letterSpacing = (-1.0).sp),
    headlineLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-0.8).sp),
    headlineMedium = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.6).sp),
    titleLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = Body, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Body, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontFamily = Body, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp),
    labelSmall = TextStyle(fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp),
)

// Oversized display numerals (streak counter, hero stats) — larger than any Typography slot.
val NdsStreakNumber = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 72.sp, letterSpacing = (-2.0).sp)
val NdsHeroNumber = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, letterSpacing = (-1.5).sp)
