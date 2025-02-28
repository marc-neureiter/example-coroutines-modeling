package at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation

import android.graphics.Bitmap
import android.net.Uri
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.ActivityRunner

typealias PickVisualMediaRunner = ActivityRunner<PickVisualMediaType, Uri>

enum class PickVisualMediaType {
    ALL,
    IMAGE,
    VIDEO,
}

typealias RequestPermissionRunner = ActivityRunner<String, Boolean>

typealias TakePictureRunner = ActivityRunner<Unit, Bitmap?>

typealias AppSettingsRunner = ActivityRunner<Unit, Unit>