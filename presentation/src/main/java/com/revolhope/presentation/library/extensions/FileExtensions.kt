package com.revolhope.presentation.library.extensions

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

suspend fun ContentResolver.readAsync(uri: Uri?): String? =
    uri?.let {
        withContext(Dispatchers.IO) {
            try {
                openInputStream(uri)?.use {
                    val bytes = it.readBytes()
                    it.close()
                    String(bytes)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
