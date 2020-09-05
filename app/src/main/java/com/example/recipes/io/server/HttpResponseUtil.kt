package com.example.recipes.io.server

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection

object HttpResponseUtil {
    private val LOG_TAG = HttpResponseUtil::class.java.simpleName

    @JvmStatic
    fun parseResponse(connection: HttpURLConnection?): String? {
        var result: String? = null
        var streamReader: InputStreamReader? = null
        var reader: BufferedReader? = null
        try {
            streamReader = InputStreamReader(connection!!.inputStream)
            reader = BufferedReader(streamReader)
            val stringBuilder = StringBuilder()
            var inputLine: String?
            while (reader.readLine().also { inputLine = it } != null) {
                stringBuilder.append(inputLine)
            }
            reader.close()
            streamReader.close()
            connection.disconnect()
            result = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                connection?.disconnect()
                reader?.close()
                streamReader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }
}