package com.timber.soft.myemoticon.tools

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.timber.soft.myemoticon.model.RootDataModel
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.zip.ZipFile

object AppTools {

    fun fileToString(fileInputStream: InputStream): String {
        return try {
            val charArray = CharArray(fileInputStream.available())
            var readCount = 0
            val streamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(streamReader)
            val stringWriter = StringWriter()
            while (bufferedReader.read(charArray).also { readCount = it } != -1) {
                stringWriter.write(charArray, 0, readCount)
            }
            stringWriter.toString()
        } catch (exception: IOException) {
            ""
        }
    }

    fun dpCovertPx(context: Context): Int {
        // 获取当前设备的屏幕密度，并赋值给变量 scale
        var result = 0
        // 获取状态栏高度的资源ID，赋值给变量 resourceId
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        // 如果资源ID大于0，则获取状态栏的高度并赋值给变量 result
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        // 返回结果
        return result
    }

    fun parseJsonFile(jsonInputStream: InputStream): List<RootDataModel> {
        val reader = InputStreamReader(jsonInputStream)
        val jsonString = reader.readText()
        return Gson().fromJson(jsonString, Array<RootDataModel>::class.java).toList()
    }

    fun downLoadFile(
        context: Context,
        zipUrl: String,
        newPath: String,
        listener: DownloadListener
    ) {
        Glide.with(context).downloadOnly().load(zipUrl)
            .addListener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File>?,
                    isFirstResource: Boolean
                ): Boolean {
                    listener.downloadListener(false, false, newPath)
                    return false
                }

                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val resultBoolean = resource?.let { getUnzipFile(it, newPath) }
                    if (resultBoolean != null) {
                        listener.downloadListener(true, resultBoolean, newPath)
                    }
                    return false
                }
            }).preload()
    }

    private fun getUnzipFile(oldFile: File, newPath: String): Boolean {
        return try {
            val newFile = File(newPath)
            if (!newFile.exists()) {
                newFile.mkdir()
            }
            val absolutePath = oldFile.absolutePath
            val zipFile = ZipFile(absolutePath)
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val file = File(newPath, entry.name)
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    val outputStream = FileOutputStream(file)
                    val inputStream = zipFile.getInputStream(entry)
                    val bytes = ByteArray(1024)
                    var length = 0
                    while (inputStream.read(bytes).also { length = it } > 0) {
                        outputStream.write(bytes, 0, length)
                    }
                    outputStream.close()
                    inputStream.close()
                }
            }
            true
        } catch (exception: Exception) {
            false
        }
    }

}

interface DownloadListener {
    fun downloadListener(isDownloadSuccess: Boolean, isUnzipSuccess: Boolean, newPath: String)
}
