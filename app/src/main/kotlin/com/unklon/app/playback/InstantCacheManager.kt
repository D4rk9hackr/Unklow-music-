package com.unklon.app.playback

import android.content.Context
import androidx.media3.datasource.cache.SimpleCache
import com.music.vivi.db.MusicDatabase
import com.music.vivi.db.entities.SongEntity
import com.music.vivi.utils.YTPlayerUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstantCacheManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    private val playerCache: SimpleCache,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val cachedSongs = mutableSetOf<String>()

    fun onSongStarted(mediaId: String) {
        if (mediaId in cachedSongs) return

        scope.launch {
            try {
                preCacheSong(mediaId)
                cachedSongs.add(mediaId)
                Timber.d("Pre-cached song: $mediaId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to pre-cache song: $mediaId")
            }
        }
    }

    private suspend fun preCacheSong(mediaId: String) {
        // Check if already fully cached
        if (isSongCached(mediaId)) {
            markSongAsDownloaded(mediaId)
            return
        }

        // Resolve stream URL (this also caches the URL for future use)
        try {
            val playbackData = YTPlayerUtils.playerResponseForPlayback(mediaId).getOrThrow()
            val streamUrl = playbackData.streamUrl
            val contentLength = playbackData.format.contentLength ?: 0L

            if (contentLength > 0 && isSongCached(mediaId)) {
                markSongAsDownloaded(mediaId)
            }
        } catch (e: Exception) {
            Timber.d("Could not pre-cache $mediaId: ${e.message}")
        }
    }

    fun isSongCached(mediaId: String): Boolean {
        return try {
            val cachedSpans = playerCache.getCachedSpans(mediaId)
            cachedSpans.isNotEmpty() && cachedSpans.all { it.isCached }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun markSongAsDownloaded(mediaId: String) {
        database.query {
            val existing = getSongByIdBlocking(mediaId)?.song
            if (existing != null && existing.dateDownload == null) {
                upsert(existing.copy(dateDownload = LocalDateTime.now()))
            }
        }
        Timber.d("Marked song as cached: $mediaId")
    }

    fun getCachedCount(): Int = cachedSongs.size

    fun clearCache() {
        cachedSongs.clear()
        Timber.d("Cache tracking cleared")
    }
}
