package com.senjy.bloomcam.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senjy.bloomcam.ui.BloomCamViewModel
import com.senjy.bloomcam.ui.components.FilterBar
import com.senjy.bloomcam.ui.theme.BloomPink
import com.senjy.bloomcam.ui.theme.DarkBackground
import com.senjy.bloomcam.ui.theme.DarkGlass
import com.senjy.bloomcam.ui.theme.PureBlack
import com.senjy.bloomcam.ui.theme.TextPrimary
import com.senjy.bloomcam.ui.theme.White

@Composable
fun PhotoPreviewScreen(viewModel: BloomCamViewModel) {
    val context = LocalContext.current
    val previewBitmap by viewModel.previewFilteredBitmap.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filterIntensity by viewModel.filterIntensity.collectAsState()
    val lastSavedUri by viewModel.lastSavedUri.collectAsState()

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
                    .padding(top = 70.dp, bottom = 180.dp)
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
                Text(
                    text = "뽀샤시 필터 조정",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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

        // Bottom Controls: Filter adjustment + Done button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            FilterBar(
                selectedFilter = selectedFilter,
                intensity = filterIntensity,
                onFilterSelected = { viewModel.selectFilter(it) },
                onIntensityChanged = { viewModel.setFilterIntensity(it) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureBlack)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
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
