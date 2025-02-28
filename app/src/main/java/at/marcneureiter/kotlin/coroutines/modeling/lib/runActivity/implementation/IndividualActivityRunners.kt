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
fun rememberPickVisualMediaRunner(): PickVisualMediaRunner {
    val currentActivity = LocalContext.current as ComponentActivity
    return remember {
        TransformingActivityRunner(
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
                // This is just to show the possibility of an exception being thrown. In reality a nullable result would be preferable.
                it ?: error("No image was picked!")
            }
        )
    }
}

@Composable
fun rememberRequestPermissionRunner(): ActivityRunner<String, Boolean> {
    val currentActivity = LocalContext.current as ComponentActivity
    return remember { ActivityRunner(currentActivity, ActivityResultContracts.RequestPermission()) }
}

@Composable
fun rememberTakePictureRunner(): TakePictureRunner {
    val currentActivity = LocalContext.current as ComponentActivity
    return TransformingActivityRunner(
        currentActivity,
        ActivityResultContracts.TakePicturePreview(),
        transformInput = { null },
        transformOutput = { it }
    )
}

@Composable
fun rememberAppSettingsRunner(): AppSettingsRunner {
    val currentActivity = LocalContext.current as ComponentActivity
    return TransformingActivityRunner(
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