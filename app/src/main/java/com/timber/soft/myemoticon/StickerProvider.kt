package com.timber.soft.myemoticon

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.timber.soft.myemoticon.model.ContentModel
import com.timber.soft.myemoticon.model.Pack
import com.timber.soft.myemoticon.tools.AppTools
import com.timber.soft.myemoticon.tools.AppVal
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class StickerProvider : ContentProvider() {

    private lateinit var context: Context
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {

        context = getContext()!!
        if (!AppVal.AUTHOR.startsWith(context.packageName)) {
            return false
        }

        uriMatcher.addURI(
            AppVal.AUTHOR, AppVal.METADATA + "/*", AppVal.METADATA_CODE_FOR_SINGLE_PACK
        )
        uriMatcher.addURI(
            AppVal.AUTHOR, AppVal.STICKERS + "/*", AppVal.STICKERS_CODE
        )

        return true
    }


    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val match = uriMatcher.match(uri)
        val contentModel = uri.lastPathSegment?.let { getPack(it) } ?: return null

        if (match == AppVal.STICKERS_CODE) {
            val cursor = getCursorThree(contentModel)
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else if (match == AppVal.METADATA_CODE_FOR_SINGLE_PACK) {
            val matrixCursor = getCursorTwo(contentModel)
            if (matrixCursor != null) {
                matrixCursor.setNotificationUri(context.contentResolver, uri)
                return matrixCursor
            }
        }

        return null
    }

    private fun getCursorTwo(contentModel: ContentModel): MatrixCursor? {

        val a = arrayOf<String>(
            AppVal.ANDROID_LINK,
            AppVal.IOS_LINK,
            AppVal.STICKER_TRAY,
            AppVal.STICKER_IDENTIFIER,
            AppVal.STICKER_NAME,
            AppVal.STICKER_PUBLISHER,
            AppVal.EMAIL,
            AppVal.WEBSITE_PUBLISH,
            AppVal.WEBSITE_LICENSE,
            AppVal.WEBSITE_POLICY,
            AppVal.ANIMATED
        )
        val matrixCursor = MatrixCursor(a)
        val rowBuilder = matrixCursor.newRow()
        val pack: Pack = contentModel.packs[0]
        rowBuilder.add(contentModel.storeLink)
        rowBuilder.add(contentModel.iosLink)
        rowBuilder.add(pack.trayName)
        rowBuilder.add(pack.identifier)
        rowBuilder.add(pack.name)
        rowBuilder.add(pack.publisherFull)
        rowBuilder.add(pack.email)
        rowBuilder.add(pack.websitePublisher)
        rowBuilder.add(pack.websiteLicense)
        rowBuilder.add(pack.websitePrivacy)
        if (pack.animated) {
            rowBuilder.add(1)
        } else {
            rowBuilder.add(0)
        }
        return matrixCursor
        return null
    }

    private fun getCursorThree(contentModel: ContentModel): MatrixCursor {
        val a = arrayOf<String>(AppVal.STICKER_FILE_NAME, AppVal.STICKER_EMOJI)
        val matrixCursor = MatrixCursor(a)
        for (sticker in contentModel.packs[0].stickers) {
            val values =
                arrayOf<String>(sticker.imFileName, TextUtils.join(",", sticker.emojis))
            matrixCursor.addRow(values)
        }
        return matrixCursor
    }

    private fun getPack(lastPathSegment: String): ContentModel? {
        val filePath = this.getContext()?.getSharedPreferences("", Context.MODE_PRIVATE)
            ?.getString(lastPathSegment, "")
        val file = filePath?.let { File(it) }
        if (file != null) {
            return if (file.exists()) {
                try {
                    val fileInputStream = FileInputStream(file)
                    val s = AppTools.fileToString(fileInputStream)
                    Gson().fromJson(s,ContentModel::class.java)
                } catch (e: FileNotFoundException) {
                    Log.e("FileNotFoundException", e.toString())
                    null
                }
            } else {
                null
            }
        }
        return null
    }

    /**
     * TODO no impl
     */
    override fun getType(uri: Uri): String? {
        if (uriMatcher.match(uri) == AppVal.STICKERS_CODE) {
            return String.format(
                context.getString(R.string.type_sticker),
                AppVal.AUTHOR,
                AppVal.STICKERS
            )
        }
        return null
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {

        val pathSegments = uri.pathSegments
        if (pathSegments.size < 3) return null
        val fileName = pathSegments[pathSegments.size - 1]
        val identifierName = pathSegments[pathSegments.size - 2]
        if (fileName.isEmpty() || identifierName.isEmpty()) return null

        val fileDir: String = getFileDir(identifierName)

        val path = fileDir + fileName

        val file = File(path)
        return if (file.exists()) {
            AssetFileDescriptor(
                ParcelFileDescriptor.open(
                    file,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ), 0L, -1L
            )
        } else {
            null
        }
//        return super.openAssetFile(uri, mode)
    }

    private fun getFileDir(identifierName: String?): String {
        val filePath = this.getContext()?.getSharedPreferences("", Context.MODE_PRIVATE)
            ?.getString(identifierName, "")
        return filePath!!.substring(0, filePath.lastIndexOf("contents.json"))
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}