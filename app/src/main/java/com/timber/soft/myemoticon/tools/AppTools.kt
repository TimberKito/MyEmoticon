package com.timber.soft.myemoticon.tools

import android.content.Context
import com.timber.soft.myemoticon.model.RootDataModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object AppTools {

    fun dpCovertPx(context: Context): Int {
        // 获取当前设备的屏幕密度，并赋值给变量 scale
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun parseJsonGsonTool(context: Context, fileName: String): List<RootDataModel>? {
        var dataItems: List<RootDataModel>? = null
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            inputStream.close()
            reader.close()
            val gson = Gson()
            val dataItemArray = gson.fromJson(stringBuilder.toString(), Array<RootDataModel>::class.java)
            dataItems = dataItemArray.toList()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return dataItems
    }
}