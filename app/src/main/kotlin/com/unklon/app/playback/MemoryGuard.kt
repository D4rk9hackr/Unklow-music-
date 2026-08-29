package com.unklon.app.playback

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import timber.log.Timber
import java.lang.ref.WeakReference

class MemoryGuard private constructor(context: Context) {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val handler = Handler(Looper.getMainLooper())
    private val serviceRef = WeakReference<Any>(null)
    private var monitoring = false
    private var trimLevel = TRIM_MEMORY_RUNNING_LOW

    private val memoryCheckRunnable = object : Runnable {
        override fun run() {
            if (!monitoring) return
            checkAndClean()
            handler.postDelayed(this, CLEAN_INTERVAL_MS)
        }
    }

    fun attachService(service: Any) {
        serviceRef.clear()
        serviceRef = WeakReference(service)
    }

    fun start() {
        if (monitoring) return
        monitoring = true
        Timber.d("MemoryGuard started")
        handler.postDelayed(memoryCheckRunnable, CLEAN_INTERVAL_MS)
    }

    fun stop() {
        monitoring = false
        handler.removeCallbacks(memoryCheckRunnable)
        serviceRef.clear()
        Timber.d("MemoryGuard stopped")
    }

    fun onTrimMemory(level: Int) {
        trimLevel = level
        if (level >= TRIM_MEMORY_RUNNING_CRITICAL) {
            handler.post { performClean-aggressive() }
        }
    }

    private fun checkAndClean() {
        val runtime = Runtime.getRuntime()
        val usedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMB = runtime.maxMemory() / (1024 * 1024)
        val usagePercent = (usedMB * 100) / maxMB

        if (usagePercent > MEMORY_CRITICAL_THRESHOLD) {
            Timber.w("Memory critical: ${usedMB}MB/${maxMB}MB ($usagePercent%)")
            performClean-aggressive()
        } else if (usagePercent > MEMORY_WARNING_THRESHOLD) {
            Timber.d("Memory elevated: ${usedMB}MB/${maxMB}MB ($usagePercent%)")
            performClean-light()
        }
    }

    private fun performClean-light() {
        Runtime.getRuntime().gc()
        Timber.d("Light memory clean performed")
    }

    private fun performClean-aggressive() {
        val runtime = Runtime.getRuntime()
        val before = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

        WeakCacheHolder.clear()
        System.gc()
        System.runFinalization()
        System.gc()

        val after = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        Timber.d("Aggressive clean: ${before}MB -> ${after}MB (freed ${before - after}MB)")
    }

    fun getTrimLevel(): Int = trimLevel

    companion object {
        private const val CLEAN_INTERVAL_MS = 30_000L
        private const val MEMORY_WARNING_THRESHOLD = 70
        private const val MEMORY_CRITICAL_THRESHOLD = 85
        const val TRIM_MEMORY_RUNNING_LOW = 10
        const val TRIM_MEMORY_RUNNING_CRITICAL = 5

        @Volatile
        private var instance: MemoryGuard? = null

        fun getInstance(context: Context): MemoryGuard {
            return instance ?: synchronized(this) {
                instance ?: MemoryGuard(context.applicationContext).also { instance = it }
            }
        }
    }
}

object WeakCacheHolder {
    private val caches = mutableListOf<WeakReference<Any>>()

    fun track(cache: Any) {
        caches.add(WeakReference(cache))
        if (caches.size > 50) {
            caches.removeAll { it.get() == null }
        }
    }

    fun clear() {
        caches.forEach { it.clear() }
        caches.clear()
    }
}
