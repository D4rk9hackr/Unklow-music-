package com.unklon.app.playback

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.media3.common.audio.SonicAudioProcessor
import com.music.vivi.extensions.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class PitchSpeedController private constructor(context: Context) {

    private val dataStore: DataStore<Preferences> = context.dataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val sonicProcessor = SonicAudioProcessor()

    private val _speed = MutableStateFlow(1.0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    fun initialize() {
        restoreValues()
    }

    fun setSpeed(newSpeed: Float) {
        val clamped = newSpeed.coerceIn(MIN_VALUE, MAX_VALUE)
        _speed.value = clamped
        sonicProcessor.setSpeed(clamped, _pitch.value)
        persist()
        Timber.d("Speed set to $clamped")
    }

    fun setPitch(newPitch: Float) {
        val clamped = newPitch.coerceIn(MIN_VALUE, MAX_VALUE)
        _pitch.value = clamped
        sonicProcessor.setSpeed(_speed.value, clamped)
        persist()
        Timber.d("Pitch set to $clamped")
    }

    fun reset() {
        _speed.value = 1.0f
        _pitch.value = 1.0f
        sonicProcessor.setSpeed(1.0f, 1.0f)
        persist()
        Timber.d("Pitch/Speed reset to defaults")
    }

    private fun persist() {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[SPEED_KEY] = _speed.value
                prefs[PITCH_KEY] = _pitch.value
            }
        }
    }

    private fun restoreValues() {
        scope.launch {
            val prefs = dataStore.data.first()
            val savedSpeed = prefs[SPEED_KEY] ?: 1.0f
            val savedPitch = prefs[PITCH_KEY] ?: 1.0f
            _speed.value = savedSpeed
            _pitch.value = savedPitch
            sonicProcessor.setSpeed(savedSpeed, savedPitch)
            Timber.d("Restored pitch=$savedPitch speed=$savedSpeed")
        }
    }

    companion object {
        private const val MIN_VALUE = 0.5f
        private const val MAX_VALUE = 2.0f

        private val SPEED_KEY = floatPreferencesKey("unklon_speed")
        private val PITCH_KEY = floatPreferencesKey("unklon_pitch")

        @Volatile
        private var instance: PitchSpeedController? = null

        fun getInstance(context: Context): PitchSpeedController {
            return instance ?: synchronized(this) {
                instance ?: PitchSpeedController(context.applicationContext).also { instance = it }
            }
        }
    }
}
