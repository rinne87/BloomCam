package com.senjy.bloomcam.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.senjy.bloomcam.camera.CameraManager
import com.senjy.bloomcam.data.AspectRatioOption
import com.senjy.bloomcam.data.FilterType
import com.senjy.bloomcam.engine.PhotoFilterEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BloomCamViewModel(application: Application) : AndroidViewModel(application) {

    val cameraManager = CameraManager(application.applicationContext)

    private val _selectedFilter = MutableStateFlow(FilterType.PURE_BLOOM)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter.asStateFlow()

    private val _filterIntensity = MutableStateFlow(0.75f)
    val filterIntensity: StateFlow<Float> = _filterIntensity.asStateFlow()

    private val _aspectRatio = MutableStateFlow(AspectRatioOption.RATIO_4_3)
    val aspectRatio: StateFlow<AspectRatioOption> = _aspectRatio.asStateFlow()

    private val _flashMode = MutableStateFlow(ImageCapture.FLASH_MODE_OFF)
    val flashMode: StateFlow<Int> = _flashMode.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()

    private val _lastSavedUri = MutableStateFlow<Uri?>(null)
    val lastSavedUri: StateFlow<Uri?> = _lastSavedUri.asStateFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    private val _previewFilteredBitmap = MutableStateFlow<Bitmap?>(null)
    val previewFilteredBitmap: StateFlow<Bitmap?> = _previewFilteredBitmap.asStateFlow()

    private val _isShowingPreview = MutableStateFlow(false)
    val isShowingPreview: StateFlow<Boolean> = _isShowingPreview.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun selectFilter(filter: FilterType) {
        _selectedFilter.value = filter
        updatePreviewFilter()
    }

    fun setFilterIntensity(intensity: Float) {
        _filterIntensity.value = intensity.coerceIn(0.0f, 1.0f)
        updatePreviewFilter()
    }

    fun toggleAspectRatio() {
        val next = when (_aspectRatio.value) {
            AspectRatioOption.RATIO_4_3 -> AspectRatioOption.RATIO_16_9
            AspectRatioOption.RATIO_16_9 -> AspectRatioOption.RATIO_1_1
            AspectRatioOption.RATIO_1_1 -> AspectRatioOption.RATIO_4_3
        }
        _aspectRatio.value = next
    }

    fun toggleFlash() {
        val next = when (_flashMode.value) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_AUTO
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
            else -> ImageCapture.FLASH_MODE_OFF
        }
        _flashMode.value = next
        cameraManager.setFlashMode(next)
    }

    fun onCameraSwitched() {
        _isFrontCamera.value = cameraManager.isFrontCamera
    }

    fun capturePhoto() {
        if (_isCapturing.value) return
        _isCapturing.value = true

        cameraManager.takePhoto(
            filter = _selectedFilter.value,
            filterIntensity = _filterIntensity.value,
            aspectRatioOption = _aspectRatio.value,
            onSuccess = { uri, bitmap ->
                _isCapturing.value = false
                _lastSavedUri.value = uri
                _capturedBitmap.value = bitmap
                _previewFilteredBitmap.value = bitmap
                _isShowingPreview.value = true
                _userMessage.value = "무음으로 뽀샤시하게 저장되었습니다 ✨"
            },
            onError = { exc ->
                _isCapturing.value = false
                _userMessage.value = "촬영 실패: ${exc.localizedMessage}"
            }
        )
    }

    fun closePreview() {
        _isShowingPreview.value = false
        _capturedBitmap.value = null
        _previewFilteredBitmap.value = null
    }

    fun reapplyFilterToPreview() {
        val raw = _capturedBitmap.value ?: return
        viewModelScope.launch {
            val processed = PhotoFilterEngine.applyFilter(
                raw,
                _selectedFilter.value,
                _filterIntensity.value
            )
            _previewFilteredBitmap.value = processed
        }
    }

    fun saveReappliedPhoto() {
        val bitmap = _previewFilteredBitmap.value ?: return
        viewModelScope.launch {
            try {
                val uri = cameraManager.saveBitmap(bitmap)
                _lastSavedUri.value = uri
                _userMessage.value = "갤러리에 새 사진으로 저장되었습니다 🌸"
                closePreview()
            } catch (e: Exception) {
                _userMessage.value = "저장 실패: ${e.localizedMessage}"
            }
        }
    }

    private fun updatePreviewFilter() {
        if (_isShowingPreview.value && _capturedBitmap.value != null) {
            reapplyFilterToPreview()
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.shutdown()
    }
}
