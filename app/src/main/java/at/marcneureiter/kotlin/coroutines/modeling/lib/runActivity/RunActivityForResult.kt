package at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Starts an activity for result with a specific [contract] and custom [input], suspending the coroutine meanwhile
 * (cancellable)
 *
 * @return a result object [O] or null if the [currentActivity] has finished in the meantime, in which case we can't
 * receive any result anymore (but note: an eventually started Activity will still be intact!)
 */
suspend fun <I, O> runActivityForResult(
    currentActivity: ComponentActivity,
    contract: ActivityResultContract<I, O>,
    input: I
): O? {
    var activityResultLauncher: ActivityResultLauncher<I>? = null
    var observer: LifecycleEventObserver? = null
    val key = UUID.randomUUID().toString()
    return try {
        suspendCoroutine { continuation ->
            // If and when the current activity is destroyed, it can't receive any result anymore, thus this suspending function has to be resumed ASAP
            if (currentActivity.isDestroyed) {
                continuation.resume(null)
                return@suspendCoroutine
            } else {
                observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_DESTROY)
                        continuation.resume(null)
                }.also { currentActivity.lifecycle.addObserver(it) }
            }

            activityResultLauncher =
                currentActivity.activityResultRegistry.register(key, contract, continuation::resume)
                    .also { it.launch(input) }
        }
    } finally {
        activityResultLauncher?.unregister()
        observer?.let { currentActivity.lifecycle.removeObserver(it) }
    }
}

