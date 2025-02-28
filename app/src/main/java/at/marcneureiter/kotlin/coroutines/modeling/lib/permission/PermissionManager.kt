package at.marcneureiter.kotlin.coroutines.modeling.lib.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.runActivityForResult

@Composable
fun rememberPermissionManager(): PermissionManager {
    return PermissionManager(LocalContext.current as ComponentActivity)
}

class PermissionManager(private val activity: ComponentActivity) {
    fun check(permission: String) = activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    suspend fun request(permission: String): Boolean? =
        runActivityForResult(activity, ActivityResultContracts.RequestPermission(), permission)
}