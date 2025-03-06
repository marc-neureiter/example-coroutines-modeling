package at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.ActivityRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.TransformingActivityRunner

@Composable
fun rememberActivityRunners(): ActivityRunners {
    val currentActivity = LocalContext.current as ComponentActivity
    return remember { ActivityRunners(currentActivity) }
}

class ActivityRunners(private val currentActivity: ComponentActivity) {

    val pickVisualMedia: PickVisualMediaRunner
        get() = TransformingActivityRunner(
            currentActivity = currentActivity,
            contract = ActivityResultContracts.PickVisualMedia(),
            transformInput = {
                val mediaType = when (it) {
                    PickVisualMediaType.ALL -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
                    PickVisualMediaType.IMAGE -> ActivityResultContracts.PickVisualMedia.ImageOnly
                    PickVisualMediaType.VIDEO -> ActivityResultContracts.PickVisualMedia.VideoOnly
                }
                PickVisualMediaRequest.Builder()
                    .setMediaType(mediaType)
                    .build()
            },
            transformOutput = {
                it ?: error("No image was picked!")
            }
        )

    val requestPermissionRunner: RequestPermissionRunner
        get() = ActivityRunner(currentActivity, ActivityResultContracts.RequestPermission())

    val takePictureRunner: TakePictureRunner
        get() = TransformingActivityRunner(
            currentActivity,
            ActivityResultContracts.TakePicturePreview(),
            transformInput = { null },
            transformOutput = { it }
        )

    val appSettingsRunner: AppSettingsRunner
        get() = TransformingActivityRunner(
            currentActivity = currentActivity,
            contract = ActivityResultContracts.StartActivityForResult(),
            transformInput = { _: Unit ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    it.data = Uri.parse("package:${currentActivity.packageName}")
                }
            },
            transformOutput = { }
        )
}


