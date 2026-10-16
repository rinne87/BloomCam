package com.senjy.bloomcam.data

import androidx.compose.ui.graphics.Color

enum class FilterType(
    val id: String,
    val displayName: String,
    val description: String,
    val previewColor: Color,
    val brightness: Float,     // -1.0 .. 1.0 (0.0 = neutral)
    val contrast: Float,       // 0.5 .. 2.0 (1.0 = neutral)
    val saturation: Float,     // 0.0 .. 2.0 (1.0 = neutral)
    val softBlurRadius: Int,   // 0 .. 10
    val tintR: Float,          // 0.0 .. 1.0
    val tintG: Float,
    val tintB: Float,
    val tintAlpha: Float       // 0.0 .. 1.0
) {
    ORIGINAL(
        id = "original",
        displayName = "원본",
        description = "필터 없는 일반 화면",
        previewColor = Color(0xFF8E8E93),
        brightness = 0.0f,
        contrast = 1.0f,
        saturation = 1.0f,
        softBlurRadius = 0,
        tintR = 1.0f,
        tintG = 1.0f,
        tintB = 1.0f,
        tintAlpha = 0.0f
    ),
    PURE_BLOOM(
        id = "pure_bloom",
        displayName = "퓨어 블룸",
        description = "맑고 깨끗한 투명 피부 톤",
        previewColor = Color(0xFFFFB6C1),
        brightness = 0.12f,
        contrast = 0.95f,
        saturation = 1.05f,
        softBlurRadius = 3,
        tintR = 1.0f,
        tintG = 0.95f,
        tintB = 0.98f,
        tintAlpha = 0.15f
    ),
    PEACH_GLOW(
        id = "peach_glow",
        displayName = "피치 글로우",
        description = "생기 있는 화사한 복숭아빛",
        previewColor = Color(0xFFFF9E80),
        brightness = 0.10f,
        contrast = 0.92f,
        saturation = 1.15f,
        softBlurRadius = 4,
        tintR = 1.0f,
        tintG = 0.88f,
        tintB = 0.82f,
        tintAlpha = 0.22f
    ),
    WARM_CREAM(
        id = "warm_cream",
        displayName = "웜 크림",
        description = "따스하고 포근한 우유빛 감성",
        previewColor = Color(0xFFFFE0B2),
        brightness = 0.08f,
        contrast = 0.90f,
        saturation = 0.95f,
        softBlurRadius = 3,
        tintR = 1.0f,
        tintG = 0.94f,
        tintB = 0.85f,
        tintAlpha = 0.18f
    ),
    SOFT_MOOD(
        id = "soft_mood",
        displayName = "소프트 무드",
        description = "잡티를 가려주는 몽환적인 블러",
        previewColor = Color(0xFFE1BEE7),
        brightness = 0.14f,
        contrast = 0.88f,
        saturation = 1.02f,
        softBlurRadius = 5,
        tintR = 0.98f,
        tintG = 0.92f,
        tintB = 1.0f,
        tintAlpha = 0.20f
    ),
    CHERRY_BLOSSOM(
        id = "cherry_blossom",
        displayName = "체리 블라썸",
        description = "꽃잎처럼 사랑스러운 핑크빛",
        previewColor = Color(0xFFFF80AB),
        brightness = 0.10f,
        contrast = 0.96f,
        saturation = 1.20f,
        softBlurRadius = 4,
        tintR = 1.0f,
        tintG = 0.82f,
        tintB = 0.92f,
        tintAlpha = 0.25f
    ),
    VINTAGE_FILM(
        id = "vintage_film",
        displayName = "필름 감성",
        description = "부드럽고 아늑한 필름 카메라",
        previewColor = Color(0xFFD7CCC8),
        brightness = 0.05f,
        contrast = 0.92f,
        saturation = 0.85f,
        softBlurRadius = 2,
        tintR = 0.95f,
        tintG = 0.92f,
        tintB = 0.85f,
        tintAlpha = 0.22f
    )
}

enum class AspectRatioOption(val label: String, val ratio: Float) {
    RATIO_4_3("4:3", 4f / 3f),
    RATIO_16_9("16:9", 16f / 9f),
    RATIO_1_1("1:1", 1f)
}
