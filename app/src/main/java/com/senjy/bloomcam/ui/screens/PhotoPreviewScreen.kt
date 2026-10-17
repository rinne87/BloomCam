package com.senjy.bloomcam.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senjy.bloomcam.data.CameraMode
import com.senjy.bloomcam.ui.BloomCamViewModel
import com.senjy.bloomcam.ui.components.FilterBar
import com.senjy.bloomcam.ui.theme.BloomGold
import com.senjy.bloomcam.ui.theme.BloomPink
import com.senjy.bloomcam.ui.theme.DarkGlass
import com.senjy.bloomcam.ui.theme.PureBlack
import com.senjy.bloomcam.ui.theme.TextPrimary
import com.senjy.bloomcam.ui.theme.TextSecondary
import com.senjy.bloomcam.ui.theme.White

enum class PreviewTab {
    FILTERS,
    STUDIO_RETOUCH
}

@Composable
fun PhotoPreviewScreen(viewModel: BloomCamViewModel) {
    val context = LocalContext.current
    val previewBitmap by viewModel.previewFilteredBitmap.collectAsState()
    val cameraMode by viewModel.cameraMode.collectAsState()
    val availableFilters by viewModel.availableFilters.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filterIntensity by viewModel.filterIntensity.collectAsState()
    val lastSavedUri by viewModel.lastSavedUri.collectAsState()

    val skinSmoothing by viewModel.skinSmoothing.collectAsState()
    val edgeSharpness by viewModel.edgeSharpness.collectAsState()
    val studioLighting by viewModel.studioLighting.collectAsState()
    val antiGlareMatte by viewModel.antiGlareMatte.collectAsState()

    var activeTab by remember { mutableStateOf(PreviewTab.FILTERS) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // High resolution photo preview
        previewBitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "촬영된 사진",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp, bottom = 220.dp)
            )
        }

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.closePreview() },
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkGlass, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "다시 촬영",
                    tint = White
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkGlass
            ) {
                val modeTitle = when (cameraMode) {
                    CameraMode.PORTRAIT -> "사진관 프로필 리터칭"
                    CameraMode.OBJECT -> "사물/일상 보정"
                    CameraMode.FIGURE_GUNPLA -> "피규어·건프라 마스터 보정"
                }
                Text(
                    text = modeTitle,
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Share Button
            IconButton(
                onClick = {
                    lastSavedUri?.let { uri ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "사진 공유"))
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkGlass, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "공유하기",
                    tint = White
                )
            }
        }

        // Bottom Controls Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Tab Switcher (필터 프리셋 vs 사진관/건프라 정밀 보정)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarkGlass
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (activeTab == PreviewTab.FILTERS) BloomPink else Color.Transparent)
                                .clickable { activeTab = PreviewTab.FILTERS }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ColorLens,
                                    contentDescription = null,
                                    tint = if (activeTab == PreviewTab.FILTERS) Color.Black else White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "감성 필터",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == PreviewTab.FILTERS) Color.Black else White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (activeTab == PreviewTab.STUDIO_RETOUCH) BloomGold else Color.Transparent)
                                .clickable { activeTab = PreviewTab.STUDIO_RETOUCH }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (activeTab == PreviewTab.STUDIO_RETOUCH) Color.Black else White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "전문 보정",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == PreviewTab.STUDIO_RETOUCH) Color.Black else White
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content
            when (activeTab) {
                PreviewTab.FILTERS -> {
                    FilterBar(
                        filters = availableFilters,
                        selectedFilter = selectedFilter,
                        intensity = filterIntensity,
                        cameraMode = cameraMode,
                        onFilterSelected = { viewModel.selectFilter(it) },
                        onIntensityChanged = { viewModel.setFilterIntensity(it) }
                    )
                }
                PreviewTab.STUDIO_RETOUCH -> {
                    StudioRetouchPanel(
                        cameraMode = cameraMode,
                        skinSmoothing = skinSmoothing ?: (selectedFilter.softBlurRadius / 5.0f),
                        edgeSharpness = edgeSharpness ?: selectedFilter.edgeSharpness,
                        studioLighting = studioLighting ?: selectedFilter.studioLightingGlow,
                        antiGlareMatte = antiGlareMatte ?: selectedFilter.antiGlareMatte,
                        onSkinSmoothingChanged = { viewModel.setSkinSmoothing(it) },
                        onEdgeSharpnessChanged = { viewModel.setEdgeSharpness(it) },
                        onStudioLightingChanged = { viewModel.setStudioLighting(it) },
                        onAntiGlareMatteChanged = { viewModel.setAntiGlareMatte(it) },
                        onReset = { viewModel.resetCustomRetouch() }
                    )
                }
            }

            // Bottom Buttons: Again vs Save
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureBlack)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.closePreview() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "다시 찍기", color = White)
                }

                Button(
                    onClick = { viewModel.saveReappliedPhoto() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BloomPink),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "저장 완료", color = White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Professional Studio & Gunpla Retouching Panel
 */
@Composable
private fun StudioRetouchPanel(
    cameraMode: CameraMode,
    skinSmoothing: Float,
    edgeSharpness: Float,
    studioLighting: Float,
    antiGlareMatte: Float,
    onSkinSmoothingChanged: (Float) -> Unit,
    onEdgeSharpnessChanged: (Float) -> Unit,
    onStudioLightingChanged: (Float) -> Unit,
    onAntiGlareMatteChanged: (Float) -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DarkGlass)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "사진관 & 건프라 전문 세부 조정",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BloomGold
            )

            Row(
                modifier = Modifier.clickable { onReset() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "기본값",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "기본값", fontSize = 11.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slider 1: 피부 도자기 스무딩 (인물)
        RetouchSliderRow(
            title = "피부결 스무딩",
            subtitle = "모공·요철 완화",
            value = skinSmoothing,
            valueRange = 0f..1f,
            displayValue = "${(skinSmoothing * 100).toInt()}%",
            accentColor = BloomPink,
            onValueChange = onSkinSmoothingChanged
        )

        // Slider 2: 먹선 & 몰드 엣지 (건프라/피규어 핵심)
        RetouchSliderRow(
            title = "먹선·몰드 선명도",
            subtitle = "패널라인/디테일 엣지",
            value = edgeSharpness,
            valueRange = 0f..2f,
            displayValue = String.format("%.1fx", edgeSharpness),
            accentColor = Color(0xFF00E5FF),
            onValueChange = onEdgeSharpnessChanged
        )

        // Slider 3: 스튜디오 45도 조명 (입체감 하이라이트)
        RetouchSliderRow(
            title = "스튜디오 조명",
            subtitle = "소프트박스 입체광",
            value = studioLighting,
            valueRange = 0f..1f,
            displayValue = "${(studioLighting * 100).toInt()}%",
            accentColor = Color(0xFFFFD54F),
            onValueChange = onStudioLightingChanged
        )

        // Slider 4: 무광 매트 마감 (플라스틱 번들거림 억제)
        RetouchSliderRow(
            title = "무광 마감 (반사 방지)",
            subtitle = "사출 번들거림 억제",
            value = antiGlareMatte,
            valueRange = 0f..1f,
            displayValue = "${(antiGlareMatte * 100).toInt()}%",
            accentColor = Color(0xFF81D4FA),
            onValueChange = onAntiGlareMatteChanged
        )
    }
}

@Composable
private fun RetouchSliderRow(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.width(96.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextSecondary
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color.DarkGray
            ),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = displayValue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.width(36.dp)
        )
    }
}
