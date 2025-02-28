package at.marcneureiter.kotlin.coroutines.modeling

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.marcneureiter.kotlin.coroutines.modeling.lib.permission.PermissionManager
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.AppSettingsRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.PickVisualMediaRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.PickVisualMediaType
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.RequestPermissionRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.TakePictureRunner
import at.marcneureiter.kotlin.coroutines.modeling.util.readBitmapFromUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(State(message = "Hello!"))
    val state = _state.asStateFlow()

    private lateinit var pickVisualMediaRunner: PickVisualMediaRunner
    private lateinit var requestPermissionRunner: RequestPermissionRunner
    private lateinit var takePictureRunner: TakePictureRunner
    private lateinit var permissionManager: PermissionManager
    private lateinit var appSettingsRunner: AppSettingsRunner

    fun init(
        pickVisualMediaRunner: PickVisualMediaRunner,
        requestPermissionRunner: RequestPermissionRunner,
        takePictureRunner: TakePictureRunner,
        permissionManager: PermissionManager,
        appSettingsRunner: AppSettingsRunner
    ) {
        this.pickVisualMediaRunner = pickVisualMediaRunner
        this.requestPermissionRunner = requestPermissionRunner
        this.takePictureRunner = takePictureRunner
        this.permissionManager = permissionManager
        this.appSettingsRunner = appSettingsRunner
    }

    fun takePicture() {
        viewModelScope.launch {
            if (!permissionManager.check(Manifest.permission.CAMERA)) {
                if (permissionManager.request(Manifest.permission.CAMERA) != true) {
                    appSettingsRunner(Unit)
                }
            }

            if (!permissionManager.check(Manifest.permission.CAMERA)) {
                setMessage("Couldn't obtain camera permissions!")
                return@launch
            }

            // In reality, this doesn't need the camera permission at all and you would rather use Jetpack's CameraX instead, but it's only here for the demonstration of the fluent code you can create with suspending functions.
            takePictureRunner(Unit)
                .let(::setPicture)

            setMessage("Picture was taken!")
        }
    }

    fun pickMedia() {
        viewModelScope.launch {
            try {
                val uri = pickVisualMediaRunner(PickVisualMediaType.IMAGE)
                if (uri == null) {
                    setMessage("No image picked!")
                } else {
                    setMessage("Image was picked!")
                    readBitmapFromUri(uri)
                        .let(::setPicture)
                }
            } catch (exception: Exception) {
                setMessage("No image picked!")
            }
        }
    }

    private fun setMessage(value: String) {
        _state.update { it.copy(message = value) }
    }

    private fun setPicture(value: Bitmap?) {
        _state.update { it.copy(picture = value) }
    }

    data class State(
        val message: String,
        @Stable val picture: Bitmap? = null
    )
}