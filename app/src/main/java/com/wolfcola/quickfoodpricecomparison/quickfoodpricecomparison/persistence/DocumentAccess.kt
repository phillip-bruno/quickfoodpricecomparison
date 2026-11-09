package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Reads and writes user-chosen documents (history export/import).
 *
 * URIs are passed as strings rather than android.net.Uri so MainViewModel can be unit tested on
 * the plain JVM, where Uri is an unusable stub.
 */
interface DocumentAccess {
    suspend fun read(uri: String): ByteArray
    suspend fun write(uri: String, bytes: ByteArray)
}

class ContentResolverDocuments(context: Context) : DocumentAccess {

    private val resolver = context.applicationContext.contentResolver

    override suspend fun read(uri: String): ByteArray = withContext(Dispatchers.IO) {
        val stream = resolver.openInputStream(Uri.parse(uri)) ?: throw IOException("Can't open $uri")
        stream.use {
            // Import accepts any file type, so don't let a stray video be read into memory.
            val bytes = it.readNBytes(MAX_IMPORT_BYTES + 1)
            if (bytes.size > MAX_IMPORT_BYTES) throw IOException("File too large")
            bytes
        }
    }

    override suspend fun write(uri: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        val stream = resolver.openOutputStream(Uri.parse(uri), "wt") ?: throw IOException("Can't open $uri")
        stream.use { it.write(bytes) }
    }

    private companion object {
        /** About 10,000 history entries. */
        const val MAX_IMPORT_BYTES = 1024 * 1024
    }
}
