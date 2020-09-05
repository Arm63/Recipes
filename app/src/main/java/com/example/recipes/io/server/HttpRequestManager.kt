package com.example.recipes.io.server


import com.example.recipes.ui.util.Constant
import com.example.recipes.ui.util.Constant.Util.UTF_8
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object HttpRequestManager {
    fun executeRequest(
        apiUrl: String?,
        requestMethod: String?,
        postData: String?
    ): HttpURLConnection? {
        var connection: HttpURLConnection? = null
        try {
            val ulr = URL(apiUrl)
            connection = ulr.openConnection() as HttpURLConnection
            connection.requestMethod = requestMethod
            connection.useCaches = false
            when (requestMethod) {
                Constant.RequestMethod.PUT, Constant.RequestMethod.POST -> {
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doInput = true
                    connection.doOutput = true
                    connection.connect()
                    val outputStream = connection.outputStream
                    outputStream.write(
                        postData?.toByteArray(charset(UTF_8))
                            ?: byteArrayOf(
                                0
                            )
                    )
                    outputStream.flush()
                    outputStream.close()
                }
                Constant.RequestMethod.GET -> connection.connect()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return connection
    }
}
