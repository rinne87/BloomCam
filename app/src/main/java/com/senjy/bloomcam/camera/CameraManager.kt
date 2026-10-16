package com.senjy.bloomcam.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.senjy.bloomcam.data.AspectRatioOption
import com.senjy.bloomcam.data.FilterType
import com.senjy.bloomcam.engine.PhotoFilterEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.min

class CameraManager(private val context: Context) {

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    var isFrontCamera: Boolean = false
        private set

    var flashMode: Int = ImageCapture.FLASH_MODE_OFF
        private set

    fun initialize(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onInitialized: () -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCamera(lifecycleOwner, previewView)
            onInitialized()
        }, ContextCompat.getMainExecutor(context))
    }

    fun switchCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        isFrontCamera = !isFrontCamera
        bindCamera(lifecycleOwner, previewView)
    }

    fun setFlashMode(mode: Int) {
        flashMode = mode
        imageCapture?.flashMode = mode
    }

    fun focus(previewView: PreviewView, x: Float, y: Float) {
        val cam = camera ?: return
        val meteringPointFactory = SurfaceOrientedMeteringPointFactory(
            previewView.width.toFloat(),
            previewView.height.toFloat()
        )
        val point = meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point).build()
        cam.cameraControl.startFocusAndMetering(action)
    }

    private fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val provider = cameraProvider ?: return
        provider.unbindAll()

        val cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()

        try {
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Captures a photo quietly (silent shutter - no sound played).
     * Applies rotation, front-camera mirror, aspect ratio cropping, and the 뽀샤시 filter.
     */
    fun takePhoto(
        filter: FilterType,
        filterIntensity: Float,
        aspectRatioOption: AspectRatioOption,
        onSuccess: (Uri, Bitmap) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val capture = imageCapture ?: run {
            onError(IllegalStateException("카메라가 준비되지 않았습니다."))
            return
        }

        capture.takePicture(
            cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    try {
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val buffer = imageProxy.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        imageProxy.close()

                        var rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            ?: throw IllegalStateException("비트맵 디코딩 실패")

                        // 1. Handle EXIF Rotation & Front Camera Mirroring
                        val matrix = Matrix()
                        matrix.postRotate(rotationDegrees.toFloat())
                        if (isFrontCamera) {
                            matrix.postScale(-1f, 1f) // Mirror horizontally for natural selfie
                        }
                        var orientedBitmap = Bitmap.createBitmap(
                            rawBitmap,
                            0,
                            0,
                            rawBitmap.width,
                            rawBitmap.height,
                            matrix,
                            true
                        )
                        if (orientedBitmap != rawBitmap) {
                            rawBitmap.recycle()
                        }

                        // 2. Crop to aspect ratio if necessary
                        val croppedBitmap = cropToRatio(orientedBitmap, aspectRatioOption)
                        if (croppedBitmap != orientedBitmap) {
                            orientedBitmap.recycle()
                        }

                        // 3. Apply 뽀샤시 Beauty Filter
                        val filteredBitmap = PhotoFilterEngine.applyFilter(
                            croppedBitmap,
                            filter,
                            filterIntensity
                        )
                        if (filteredBitmap != croppedBitmap) {
                            croppedBitmap.recycle()
                        }

                        // 4. Save to MediaStore (Pictures/BloomCam)
                        val savedUri = saveBitmapToGallery(filteredBitmap)

                        ContextCompat.getMainExecutor(context).execute {
                            onSuccess(savedUri, filteredBitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ContextCompat.getMainExecutor(context).execute {
                            onError(e)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    ContextCompat.getMainExecutor(context).execute {
                        onError(exception)
                    }
                }
            }
        )
    }

    private fun cropToRatio(src: Bitmap, option: AspectRatioOption): Bitmap {
        val w = src.width
        val h = src.height

        val targetRatio = option.ratio
        val currentRatio = if (h > w) h.toFloat() / w.toFloat() else w.toFloat() / h.toFloat()

        if (option == AspectRatioOption.RATIO_1_1) {
            val size = min(w, h)
            val startX = (w - size) / 2
            val startY = (h - size) / 2
            return Bitmap.createBitmap(src, startX, startY, size, size)
        }

        // For other ratios, return src if close enough
        return src
    }

    suspend fun saveBitmap(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        saveBitmapToGallery(bitmap)
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): Uri {
        val filename = "Bloom_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BloomCam")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("미디어스토어 URI 생성 실패")

        context.contentResolver.openOutputStream(uri)?.use { out: OutputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 96, out)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
        }

        return uri
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
