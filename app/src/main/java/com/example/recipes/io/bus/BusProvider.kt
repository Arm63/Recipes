@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.example.recipes.io.bus

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import java.util.concurrent.Executor

object BusProvider {
    val instance: EventBus = AsyncEventBus(object : Executor {
        private var handler: Handler? = null
        override fun execute(@NonNull command: Runnable) {
            if (handler == null) {
                handler = Handler(Looper.getMainLooper())
            }
            handler!!.post(command)
        }
    })

    fun register(`object`: Any?) {
        instance.register(`object`)
    }

    fun unregister(`object`: Any?) {
        try {
            instance.unregister(`object`)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}