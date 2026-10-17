package com.senjy.bloomcam.data

enum class CameraMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String
) {
    PORTRAIT(
        id = "portrait",
        title = "인물",
        subtitle = "뽀샤시 피부결 & 사진관 스튜디오 조명",
        badge = "인물 모드"
    ),
    OBJECT(
        id = "object",
        title = "사물/일상",
        subtitle = "생생한 컬러 & 선명한 카페/음식 디테일",
        badge = "사물 모드"
    ),
    FIGURE_GUNPLA(
        id = "figure_gunpla",
        title = "피규어/건프라",
        subtitle = "먹선(몰드) 엣지 강화 & 플라스틱 반사 방지",
        badge = "피규어·건프라 전용"
    )
}
