package com.senjy.bloomcam.data

import androidx.compose.ui.graphics.Color

enum class FilterType(
    val id: String,
    val displayName: String,
    val description: String,
    val previewColor: Color,
    val mode: CameraMode,
    val brightness: Float,       // -1.0 .. 1.0 (0.0 = neutral)
    val contrast: Float,         // 0.5 .. 2.0 (1.0 = neutral)
    val saturation: Float,       // 0.0 .. 2.0 (1.0 = neutral)
    val softBlurRadius: Int,     // 0 .. 10 (skin smoothing)
    val tintR: Float,            // 0.0 .. 1.0
    val tintG: Float,
    val tintB: Float,
    val tintAlpha: Float,        // 0.0 .. 1.0
    val edgeSharpness: Float = 0.0f,     // 0.0 .. 2.0 (panel lines & details)
    val antiGlareMatte: Float = 0.0f,    // 0.0 .. 1.0 (suppress plastic shine / highlights)
    val studioLightingGlow: Float = 0.0f,// 0.0 .. 1.0 (studio softbox highlight)
    val vignette: Float = 0.0f           // 0.0 .. 1.0 (focus attention on subject)
) {
    // -------------------------------------------------------------
    // 1. 공통 원본 (Original)
    // -------------------------------------------------------------
    ORIGINAL(
        id = "original",
        displayName = "원본",
        description = "보정 없는 기본 촬영",
        previewColor = Color(0xFF8E8E93),
        mode = CameraMode.PORTRAIT,
        brightness = 0.0f,
        contrast = 1.0f,
        saturation = 1.0f,
        softBlurRadius = 0,
        tintR = 1.0f,
        tintG = 1.0f,
        tintB = 1.0f,
        tintAlpha = 0.0f
    ),

    // -------------------------------------------------------------
    // 2. 인물 모드 (Portrait Mode - 뽀샤시 & 사진관 스튜디오)
    // -------------------------------------------------------------
    PURE_BLOOM(
        id = "pure_bloom",
        displayName = "퓨어 블룸",
        description = "맑고 깨끗한 투명 피부 톤 (데일리 셀카)",
        previewColor = Color(0xFFFFB6C1),
        mode = CameraMode.PORTRAIT,
        brightness = 0.12f,
        contrast = 0.95f,
        saturation = 1.05f,
        softBlurRadius = 3,
        tintR = 1.0f,
        tintG = 0.95f,
        tintB = 0.98f,
        tintAlpha = 0.15f,
        studioLightingGlow = 0.25f
    ),
    STUDIO_PRO(
        id = "studio_pro",
        displayName = "사진관 프로필",
        description = "도자기 피부결 + 스튜디오 45도 조명 입체감",
        previewColor = Color(0xFFFFD1DC),
        mode = CameraMode.PORTRAIT,
        brightness = 0.14f,
        contrast = 1.02f,
        saturation = 1.08f,
        softBlurRadius = 4,
        tintR = 1.0f,
        tintG = 0.96f,
        tintB = 0.94f,
        tintAlpha = 0.18f,
        studioLightingGlow = 0.45f,
        edgeSharpness = 0.4f,
        vignette = 0.20f
    ),
    PEACH_GLOW(
        id = "peach_glow",
        displayName = "피치 글로우",
        description = "생기 있는 화사한 복숭아빛 과즙 톤",
        previewColor = Color(0xFFFF9E80),
        mode = CameraMode.PORTRAIT,
        brightness = 0.10f,
        contrast = 0.92f,
        saturation = 1.15f,
        softBlurRadius = 4,
        tintR = 1.0f,
        tintG = 0.88f,
        tintB = 0.82f,
        tintAlpha = 0.22f,
        studioLightingGlow = 0.20f
    ),
    WARM_CREAM(
        id = "warm_cream",
        displayName = "웜 크림",
        description = "따스하고 포근한 우유빛 베이지 감성",
        previewColor = Color(0xFFFFE0B2),
        mode = CameraMode.PORTRAIT,
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
        description = "잡티를 가려주는 몽환적인 소프트 블러",
        previewColor = Color(0xFFE1BEE7),
        mode = CameraMode.PORTRAIT,
        brightness = 0.14f,
        contrast = 0.88f,
        saturation = 1.02f,
        softBlurRadius = 5,
        tintR = 0.98f,
        tintG = 0.92f,
        tintB = 1.0f,
        tintAlpha = 0.20f,
        studioLightingGlow = 0.30f
    ),
    CHERRY_BLOSSOM(
        id = "cherry_blossom",
        displayName = "체리 블라썸",
        description = "벚꽃잎처럼 사랑스럽고 화사한 핑크 톤",
        previewColor = Color(0xFFFF80AB),
        mode = CameraMode.PORTRAIT,
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
        description = "부드럽고 아늑한 클래식 필름 카메라",
        previewColor = Color(0xFFD7CCC8),
        mode = CameraMode.PORTRAIT,
        brightness = 0.05f,
        contrast = 0.92f,
        saturation = 0.85f,
        softBlurRadius = 2,
        tintR = 0.95f,
        tintG = 0.92f,
        tintB = 0.85f,
        tintAlpha = 0.22f,
        vignette = 0.25f
    ),

    // -------------------------------------------------------------
    // 3. 사물 / 일상 모드 (Object Mode - 비비드 & 카페)
    // -------------------------------------------------------------
    VIVID_POP(
        id = "vivid_pop",
        displayName = "비비드 팝",
        description = "생생하고 선명한 컬러풀 사물 촬영",
        previewColor = Color(0xFF00E676),
        mode = CameraMode.OBJECT,
        brightness = 0.05f,
        contrast = 1.15f,
        saturation = 1.30f,
        softBlurRadius = 0,
        tintR = 1.0f,
        tintG = 1.0f,
        tintB = 1.0f,
        tintAlpha = 0.0f,
        edgeSharpness = 0.4f
    ),
    CAFE_WARM(
        id = "cafe_warm",
        displayName = "카페 감성",
        description = "아늑한 조명과 원목 느낌의 따뜻한 사물 톤",
        previewColor = Color(0xFFFFB74D),
        mode = CameraMode.OBJECT,
        brightness = 0.06f,
        contrast = 1.05f,
        saturation = 1.05f,
        softBlurRadius = 1,
        tintR = 1.0f,
        tintG = 0.92f,
        tintB = 0.82f,
        tintAlpha = 0.18f,
        vignette = 0.15f
    ),
    FOOD_DELIGHT(
        id = "food_delight",
        displayName = "푸드 딜라이트",
        description = "음식과 디저트의 붉은기/온기를 살려주는 톤",
        previewColor = Color(0xFFFF7043),
        mode = CameraMode.OBJECT,
        brightness = 0.08f,
        contrast = 1.10f,
        saturation = 1.25f,
        softBlurRadius = 0,
        tintR = 1.0f,
        tintG = 0.94f,
        tintB = 0.88f,
        tintAlpha = 0.12f,
        edgeSharpness = 0.3f
    ),
    CLEAN_MINIMAL(
        id = "clean_minimal",
        displayName = "클린 미니멀",
        description = "소품과 인테리어를 정갈하게 정돈해주는 화이트 톤",
        previewColor = Color(0xFF90CAF9),
        mode = CameraMode.OBJECT,
        brightness = 0.10f,
        contrast = 1.08f,
        saturation = 0.95f,
        softBlurRadius = 0,
        tintR = 0.95f,
        tintG = 0.98f,
        tintB = 1.0f,
        tintAlpha = 0.10f,
        edgeSharpness = 0.2f
    ),

    // -------------------------------------------------------------
    // 4. 피규어 / 건프라 전용 모드 (Figure & Gunpla - 엣지&무광)
    // -------------------------------------------------------------
    PANEL_LINE_PRO(
        id = "panel_line_pro",
        displayName = "먹선 & 메카닉",
        description = "건프라 패널라인(먹선) 극대화 & 몰드 음영 강화",
        previewColor = Color(0xFF00E5FF),
        mode = CameraMode.FIGURE_GUNPLA,
        brightness = 0.04f,
        contrast = 1.22f,
        saturation = 1.12f,
        softBlurRadius = 0,
        tintR = 0.95f,
        tintG = 0.98f,
        tintB = 1.0f,
        tintAlpha = 0.08f,
        edgeSharpness = 1.30f,
        antiGlareMatte = 0.40f,
        vignette = 0.18f
    ),
    MATTE_TOPCOAT(
        id = "matte_topcoat",
        displayName = "무광 마감 (매트)",
        description = "플라스틱 사출 번들거림 억제, 도색작 같은 무광 질감",
        previewColor = Color(0xFFB0BEC5),
        mode = CameraMode.FIGURE_GUNPLA,
        brightness = -0.02f,
        contrast = 1.14f,
        saturation = 1.05f,
        softBlurRadius = 0,
        tintR = 1.0f,
        tintG = 1.0f,
        tintB = 1.0f,
        tintAlpha = 0.0f,
        edgeSharpness = 0.70f,
        antiGlareMatte = 0.85f,
        vignette = 0.22f
    ),
    METALLIC_HEAVY(
        id = "metallic_heavy",
        displayName = "메탈릭 헤비",
        description = "프레임과 메탈릭 도색의 중후한 깊이감 & 금속 광택",
        previewColor = Color(0xFFFFD700),
        mode = CameraMode.FIGURE_GUNPLA,
        brightness = -0.04f,
        contrast = 1.30f,
        saturation = 1.18f,
        softBlurRadius = 0,
        tintR = 1.0f,
        tintG = 0.96f,
        tintB = 0.90f,
        tintAlpha = 0.10f,
        edgeSharpness = 1.10f,
        antiGlareMatte = 0.50f,
        vignette = 0.30f
    ),
    BOX_ART_HDR(
        id = "box_art_hdr",
        displayName = "박스아트 화보",
        description = "반다이 공식 박스아트 스타일 드라마틱 하이 콘트라스트",
        previewColor = Color(0xFFFF5252),
        mode = CameraMode.FIGURE_GUNPLA,
        brightness = 0.05f,
        contrast = 1.25f,
        saturation = 1.25f,
        softBlurRadius = 0,
        tintR = 0.98f,
        tintG = 0.95f,
        tintB = 1.0f,
        tintAlpha = 0.12f,
        edgeSharpness = 0.90f,
        antiGlareMatte = 0.35f,
        vignette = 0.35f
    ),
    ANIME_PVC(
        id = "anime_pvc",
        displayName = "미소녀 피규어",
        description = "피부 번들거림은 잡고 눈동자/헤어 디테일은 선명하게",
        previewColor = Color(0xFFF48FB1),
        mode = CameraMode.FIGURE_GUNPLA,
        brightness = 0.08f,
        contrast = 1.08f,
        saturation = 1.15f,
        softBlurRadius = 1,
        tintR = 1.0f,
        tintG = 0.92f,
        tintB = 0.95f,
        tintAlpha = 0.15f,
        edgeSharpness = 0.75f,
        antiGlareMatte = 0.60f,
        studioLightingGlow = 0.25f,
        vignette = 0.15f
    );

    companion object {
        fun getFiltersForMode(mode: CameraMode): List<FilterType> {
            val list = mutableListOf<FilterType>()
            // Always include ORIGINAL as the first option
            list.add(ORIGINAL)
            // Add mode-specific filters
            list.addAll(values().filter { it.mode == mode && it != ORIGINAL })
            return list
        }
    }
}

enum class AspectRatioOption(val label: String, val ratio: Float) {
    RATIO_4_3("4:3", 4f / 3f),
    RATIO_16_9("16:9", 16f / 9f),
    RATIO_1_1("1:1", 1f)
}
