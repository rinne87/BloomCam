package com.senjy.bloomcam.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.senjy.bloomcam.camera.CameraManager
import com.senjy.bloomcam.data.AspectRatioOption
import com.senjy.bloomcam.data.CameraMode
import com.senjy.bloomcam.data.FilterType
import com.senjy.bloomcam.engine.PhotoFilterEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BloomCamViewModel(application: Application) : AndroidViewModel(application) {

    val cameraManager = CameraManager(application.applicationContext)

    // Current Camera Mode (Portrait, Object, Figure/Gunpla)
    private val _cameraMode = MutableStateFlow(CameraMode.PORTRAIT)
    val cameraMode: StateFlow<CameraMode> = _cameraMode.asStateFlow()

    // Available filters based on current Camera Mode
    private val _availableFilters = MutableStateFlow(FilterType.getFiltersForMode(CameraMode.PORTRAIT))
    val availableFilters: StateFlow<List<FilterType>> = _availableFilters.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FilterType.PURE_BLOOM)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter.asStateFlow()

    private val _filterIntensity = MutableStateFlow(0.80f)
    val filterIntensity: StateFlow<Float> = _filterIntensity.asStateFlow()

    // Custom Studio / Gunpla Retouch Sliders (in PhotoPreviewScreen)
    private val _skinSmoothing = MutableStateFlow<Float?>(null)
    val skinSmoothing: StateFlow<Float?> = _skinSmoothing.asStateFlow()

    private val _edgeSharpness = MutableStateFlow<Float?>(null)
    val edgeSharpness: StateFlow<Float?> = _edgeSharpness.asStateFlow()

    private val _studioLighting = MutableStateFlow<Float?>(null)
    val studioLighting: StateFlow<Float?> = _studioLighting.asStateFlow()

    private val _antiGlareMatte = MutableStateFlow<Float?>(null)
    val antiGlareMatte: StateFlow<Float?> = _antiGlareMatte.asStateFlow()

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

    private val _capturedRawBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedRawBitmap: StateFlow<Bitmap?> = _capturedRawBitmap.asStateFlow()

    private val _previewFilteredBitmap = MutableStateFlow<Bitmap?>(null)
    val previewFilteredBitmap: StateFlow<Bitmap?> = _previewFilteredBitmap.asStateFlow()

    private val _isShowingPreview = MutableStateFlow(false)
    val isShowingPreview: StateFlow<Boolean> = _isShowingPreview.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun setCameraMode(mode: CameraMode) {
        if (_cameraMode.value == mode) return
        _cameraMode.value = mode
        val filters = FilterType.getFiltersForMode(mode)
        _availableFilters.value = filters
        // Default to the first preset filter (after ORIGINAL) for this mode
        val defaultPreset = filters.getOrNull(1) ?: FilterType.ORIGINAL
        _selectedFilter.value = defaultPreset
        resetCustomRetouch()
        updatePreviewFilter()
    }

    fun selectFilter(filter: FilterType) {
        _selectedFilter.value = filter
        resetCustomRetouch()
        updatePreviewFilter()
    }

    fun setFilterIntensity(intensity: Float) {
        _filterIntensity.value = intensity.coerceIn(0.0f, 1.0f)
        updatePreviewFilter()
    }

    // Studio Retouch Adjustments
    fun setSkinSmoothing(value: Float) {
        _skinSmoothing.value = value.coerceIn(0f, 1f)
        updatePreviewFilter()
    }

    fun setEdgeSharpness(value: Float) {
        _edgeSharpness.value = value.coerceIn(0f, 2f)
        updatePreviewFilter()
    }

    fun setStudioLighting(value: Float) {
        _studioLighting.value = value.coerceIn(0f, 1f)
        updatePreviewFilter()
    }

    fun setAntiGlareMatte(value: Float) {
        _antiGlareMatte.value = value.coerceIn(0f, 1f)
        updatePreviewFilter()
    }

    fun resetCustomRetouch() {
        _skinSmoothing.value = null
        _edgeSharpness.value = null
        _studioLighting.value = null
        _antiGlareMatte.value = null
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
            onSuccess = { uri, filteredBitmap, rawBitmap ->
                _isCapturing.value = false
                _lastSavedUri.value = uri
                _capturedRawBitmap.value = rawBitmap
                _previewFilteredBitmap.value = filteredBitmap
                _isShowingPreview.value = true
                val modeDesc = when (_cameraMode.value) {
                    CameraMode.PORTRAIT -> "사진관 뽀샤시 인물 사진 저장 ✨"
                    CameraMode.OBJECT -> "생생한 사물 사진 저장 ☕"
                    CameraMode.FIGURE_GUNPLA -> "먹선/무광 건프라 사진 저장 🤖"
                }
                _userMessage.value = "무음으로 $modeDesc"
            },
            onError = { exc ->
                _isCapturing.value = false
                _userMessage.value = "촬영 실패: ${exc.localizedMessage}"
            }
        )
    }

    fun closePreview() {
        _isShowingPreview.value = false
        _capturedRawBitmap.value = null
        _previewFilteredBitmap.value = null
        resetCustomRetouch()
    }

    fun reapplyFilterToPreview() {
        val raw = _capturedRawBitmap.value ?: return
        viewModelScope.launch {
            val processed = PhotoFilterEngine.applyFilter(
                src = raw,
                filter = _selectedFilter.value,
                intensity = _filterIntensity.value,
                customSkinSmoothing = _skinSmoothing.value,
                customEdgeSharpness = _edgeSharpness.value,
                customStudioLighting = _studioLighting.value,
                customAntiGlareMatte = _antiGlareMatte.value
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
                _userMessage.value = "보정된 사진이 갤러리에 새로 저장되었습니다 🌸"
                closePreview()
            } catch (e: Exception) {
                _userMessage.value = "저장 실패: ${e.localizedMessage}"
            }
        }
    }

    private fun updatePreviewFilter() {
        if (_isShowingPreview.value && _capturedRawBitmap.value != null) {
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
