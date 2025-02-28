package at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract

/**
 * Encapsulation of calls to [runActivityForResult] that can be reused in code like view models or use cases
 */
fun interface ActivityRunner<I, O> {
    suspend operator fun invoke(input: I): O?
}

/**
 * Factory function for a basic [ActivityRunner]
 */
fun <I, O> ActivityRunner(
    currentActivity: ComponentActivity,
    contract: ActivityResultContract<I, O>,
): ActivityRunner<I, O> = TransformingActivityRunner<I, I, O, O>(
    currentActivity = currentActivity,
    contract = contract,
    transformInput = { it }, // no transformation needed
    transformOutput = { it } // no transformation needed
)

class TransformingActivityRunner<IInner, I, OInner, O>(
    private val currentActivity: ComponentActivity,
    private val contract: ActivityResultContract<IInner, OInner>,
    private val transformInput: (I) -> IInner,
    private val transformOutput: (OInner?) -> O?
) : ActivityRunner<I, O> {
    override suspend operator fun invoke(input: I): O? {
        return runActivityForResult(currentActivity, contract, transformInput(input))
            .let(transformOutput)
    }
}