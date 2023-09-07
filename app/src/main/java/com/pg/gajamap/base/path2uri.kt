package com.pg.gajamap.base

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

@SuppressLint("Range")
fun path2uri(context: Context, filePath: String): Uri? {
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        null,
        "_data = ?",
        arrayOf(filePath),
        null
    )

    cursor?.use {
        if (it.moveToNext()) {
            val id = it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID))
            return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        }
    }

    return null
}