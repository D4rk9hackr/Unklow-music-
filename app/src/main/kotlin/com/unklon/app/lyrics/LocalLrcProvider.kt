package com.unklon.app.lyrics

import android.content.Context
import android.os.Environment
import com.music.vivi.lyrics.LyricsProvider
import timber.log.Timber
import java.io.File

object LocalLrcProvider : LyricsProvider {

    override val name = "LocalLrc"

    override fun isEnabled(context: Context): Boolean = true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> {
        val lrcContent = findLrcFile(title, artist) ?: return Result.failure(
            FileNotFoundException("No local .lrc found for: $title - $artist")
        )
        return Result.success(lrcContent)
    }

    private fun findLrcFile(title: String, artist: String): String? {
        val searchDirs = getSearchDirectories()
        val normalizedTitle = normalizeForSearch(title)
        val normalizedArtist = normalizeForSearch(artist)

        for (dir in searchDirs) {
            if (!dir.exists() || !dir.isDirectory) continue

            val lrcFiles = dir.listFiles { file ->
                file.extension.equals("lrc", ignoreCase = true)
            } ?: continue

            // Exact match: "Artist - Title.lrc"
            for (file in lrcFiles) {
                val name = file.nameWithoutExtension.lowercase()
                if (name == "$normalizedArtist - $normalizedTitle" ||
                    name == "$normalizedTitle - $normalizedArtist" ||
                    name == normalizedTitle
                ) {
                    return readLrcFile(file)
                }
            }

            // Fuzzy match: filename contains title
            for (file in lrcFiles) {
                val name = file.nameWithoutExtension.lowercase()
                if (name.contains(normalizedTitle) || normalizedTitle.contains(name)) {
                    return readLrcFile(file)
                }
            }
        }
        return null
    }

    private fun readLrcFile(file: File): String? {
        return try {
            val content = file.readText(Charsets.UTF_8)
            if (content.isBlank()) null else content
        } catch (e: Exception) {
            Timber.e(e, "Failed to read LRC file: ${file.absolutePath}")
            null
        }
    }

    private fun getSearchDirectories(): List<File> {
        val dirs = mutableListOf<File>()

        // Primary: Music/Unklon/
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        dirs.add(File(musicDir, "Unklon"))

        // Secondary: Music/lrc/
        dirs.add(File(musicDir, "lrc"))

        // Tertiary: Download/
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))

        // Quaternary: App-internal storage
        // Note: context would be needed here, but we use external for now
        return dirs
    }

    private fun normalizeForSearch(input: String): String {
        return input.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    class FileNotFoundException(message: String) : Exception(message)
}
