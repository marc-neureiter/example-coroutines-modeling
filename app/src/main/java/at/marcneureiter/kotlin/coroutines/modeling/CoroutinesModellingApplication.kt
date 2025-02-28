package at.marcneureiter.kotlin.coroutines.modeling

import android.app.Application

class CoroutinesModellingApplication : Application() {

    init {
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: CoroutinesModellingApplication
    }
}