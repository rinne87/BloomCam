package com.senjy.bloomcam.ui.screens

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.senjy.bloomcam.data.AspectRatioOption
import com.senjy.bloomcam.data.FilterType
import com.senjy.bloomcam.ui.BloomCamViewModel
import com.senjy.bloomcam.ui.components.FilterBar
import com.senjy.bloomcam.ui.components.FocusRing
import com.senjy.bloomcam.ui.theme.BloomGold
import com.senjy.bloomcam.ui.theme.BloomPink
import com.senjy.bloomcam.ui.theme.DarkBackground
import com.senjy.bloomcam.ui.theme.DarkGlass
import com.senjy.bloomcam.ui.theme.PureBlack
import com.senjy.bloomcam.ui.theme.TextPrimary
import com.senjy.bloomcam.ui.theme.White

@Composable
fun CameraScreen(viewModel: BloomCamViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filterIntensity by viewModel.filterIntensity.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    val flashMode by viewModel.flashMode.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    val isCapturing by viewModel.isCapturing.collectAsState()
    val lastSavedUri by viewModel.lastSavedUri.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var focusPoint by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // 1. Camera Viewfinder with Aspect Ratio Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            val ratioModifier = when (aspectRatio) {
                AspectRatioOption.RATIO_4_3 -> Modifier.aspectRatio(3f / 4f)
                AspectRatioOption.RATIO_16_9 -> Modifier.fillMaxSize()
                AspectRatioOption.RATIO_1_1 -> Modifier.aspectRatio(1f)
            }

            Box(
                modifier = ratioModifier
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            previewView?.let { pv ->
                                viewModel.cameraManager.focus(pv, offset.x, offset.y)
                                focusPoint = Pair(offset.x, offset.y)
                            }
                        }
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            viewModel.cameraManager.initialize(lifecycleOwner, this)
                            previewView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Real-time Preview Color/Tint Overlay for 뽀샤시 mood
                if (selectedFilter != FilterType.ORIGINAL && filterIntensity > 0.05f) {
                    val overlayColor = Color(
                        red = selectedFilter.tintR,
                        green = selectedFilter.tintG,
                        blue = selectedFilter.tintB,
                        alpha = (selectedFilter.tintAlpha * 0.45f * filterIntensity).coerceIn(0f, 1f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(overlayColor)
                    )
                }

                // Focus animation ring
                focusPoint?.let { (fx, fy) ->
                    FocusRing(x = fx, y = fy) {
                        focusPoint = null
                    }
                }
            }
        }

        // 2. Top Bar Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash Toggle
            IconButton(
                onClick = { viewModel.toggleFlash() },
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkGlass, CircleShape)
            ) {
                Icon(
                    imageVector = when (flashMode) {
                        ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                        ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                        else -> Icons.Default.FlashOff
                    },
                    contentDescription = "플래시 모드",
                    tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) BloomGold else White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Silent Shutter Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkGlass
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeOff,
                        contentDescription = "무음 셔터",
                        tint = BloomPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "무음 모드",
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Ratio Toggle
            Surface(
                shape = CircleShape,
                color = DarkGlass,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.toggleAspectRatio() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = aspectRatio.label,
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Bottom Controls Area (Filter Bar + Shutter Area)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Filter Bar
            FilterBar(
                selectedFilter = selectedFilter,
                intensity = filterIntensity,
                onFilterSelected = { viewModel.selectFilter(it) },
                onIntensityChanged = { viewModel.setFilterIntensity(it) }
            )

            // Shutter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureBlack)
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .clickable {
                            // Open Gallery
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                type = "image/*"
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (lastSavedUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(lastSavedUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "최근 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "갤러리",
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BloomPink.copy(alpha = 0.2f))
                        .border(4.dp, White, CircleShape)
                        .clickable(enabled = !isCapturing) {
                            viewModel.capturePhoto()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(color = BloomPink, modifier = Modifier.size(36.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(White)
                        )
                    }
                }

                // Switch Camera (Front / Back)
                IconButton(
                    onClick = {
                        previewView?.let { pv ->
                            viewModel.cameraManager.switchCamera(lifecycleOwner, pv)
                            viewModel.onCameraSwitched()
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(DarkGlass, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "카메라 전환",
                        tint = if (isFrontCamera) BloomPink else White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Snackbar Host
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
