package com.timber.soft.myemoticon.tools

import android.content.Context
import com.google.gson.Gson
import com.timber.soft.myemoticon.model.RootDataModel
import java.io.InputStream
import java.io.InputStreamReader

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

    fun parseJsonFile(jsonInputStream: InputStream): List<RootDataModel> {
        val reader = InputStreamReader(jsonInputStream)
        val jsonString = reader.readText()
        return Gson().fromJson(jsonString, Array<RootDataModel>::class.java).toList()
    }

}