package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import android.content.Context
import android.util.AtomicFile
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryRules
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class HistoryManager(
    context: Context,
    /**
     * Single-threaded so writes land in the order they were requested: two quick conversions
     * can't race and leave the older list on disk.
     */
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
) : HistoryStore {

    private val appContext = context.applicationContext

    /** AtomicFile, so the app being killed mid-write can't leave a truncated history file. */
    private val file by lazy { AtomicFile(File(appContext.filesDir, "conversion_history.json")) }

    override suspend fun load(): List<HistoryEntry> = withContext(dispatcher) {
        try {
            HistoryRules.normalize(HistoryCodec.decode(String(file.readFully(), Charsets.UTF_8)))
        } catch (_: FileNotFoundException) {
            emptyList()
        } catch (_: Exception) {
            // Corrupt file: start empty; the next save overwrites it.
            emptyList()
        }
    }

    override suspend fun save(entries: List<HistoryEntry>) = withContext(dispatcher) {
        val out = file.startWrite()
        try {
            out.write(HistoryCodec.encode(entries).toByteArray(Charsets.UTF_8))
            file.finishWrite(out)
        } catch (e: IOException) {
            file.failWrite(out)
            throw e
        }
    }

    override suspend fun clear() = withContext(dispatcher) {
        file.delete()
    }
}
