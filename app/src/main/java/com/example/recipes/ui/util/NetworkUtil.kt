@file:Suppress("DEPRECATION")

package com.example.recipes.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

@Suppress("DEPRECATION")
class NetworkUtil private constructor() {

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    companion object {
        private val LOG_TAG = NetworkUtil::class.java.simpleName
        private var sInstance: NetworkUtil? = null
        val instance: NetworkUtil?
            get() {
                if (sInstance == null) {
                    sInstance = NetworkUtil()
                }
                return sInstance
            }
    }
}
