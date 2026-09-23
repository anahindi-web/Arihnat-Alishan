package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.SystemClock

object SoundNotificationHelper {
    var isGlobalSoundEnabled: Boolean = true
    private var lastClickTime: Long = 0

    private var clickToneGenerator: ToneGenerator? = null
    private var notificationToneGenerator: ToneGenerator? = null
    private var alarmToneGenerator: ToneGenerator? = null

    @Synchronized
    private fun getClickTone(): ToneGenerator? {
        if (clickToneGenerator == null) {
            try {
                clickToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
            } catch (_: Exception) {
                clickToneGenerator = null
            }
        }
        return clickToneGenerator
    }

    @Synchronized
    private fun getNotificationTone(): ToneGenerator? {
        if (notificationToneGenerator == null) {
            try {
                notificationToneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
            } catch (_: Exception) {
                notificationToneGenerator = null
            }
        }
        return notificationToneGenerator
    }

    @Synchronized
    private fun getAlarmTone(): ToneGenerator? {
        if (alarmToneGenerator == null) {
            try {
                alarmToneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 90)
            } catch (_: Exception) {
                alarmToneGenerator = null
            }
        }
        return alarmToneGenerator
    }

    /**
     * Plays a crisp, light tactile click sound on button taps.
     */
    fun playClickSound(context: Context, isSoundEnabled: Boolean = true) {
        if (!isGlobalSoundEnabled || !isSoundEnabled) return
        val now = SystemClock.uptimeMillis()
        if (now - lastClickTime < 45) return // debounce rapid clicks
        lastClickTime = now

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.75f)
        } catch (_: Exception) {
            try {
                getClickTone()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 25)
            } catch (_: Exception) {
                // Silent fallback
            }
        }
    }

    /**
     * Plays an audible, pleasant notification chime for announcements, notices, and alerts.
     */
    fun playNotificationSound(context: Context, isSoundEnabled: Boolean = true) {
        if (!isGlobalSoundEnabled || !isSoundEnabled) return
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            if (ringtone != null) {
                ringtone.play()
                return
            }
        } catch (_: Exception) {
            // Fallback
        }

        try {
            getNotificationTone()?.startTone(ToneGenerator.TONE_PROP_ACK, 200)
        } catch (_: Exception) {
            // Silent fallback
        }
    }

    /**
     * Plays task completion celebration sound.
     */
    fun playTaskCompletionSound(context: Context, isSoundEnabled: Boolean = true) {
        if (!isGlobalSoundEnabled || !isSoundEnabled) return
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            if (ringtone != null) {
                ringtone.play()
                return
            }
        } catch (_: Exception) {
            // Fallback
        }

        try {
            getNotificationTone()?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        } catch (_: Exception) {
            // Silent fallback
        }
    }

    /**
     * Plays an urgent alert tone for SOS and high-priority alarms.
     */
    fun playAlertSound(context: Context, isSoundEnabled: Boolean = true) {
        if (!isGlobalSoundEnabled || !isSoundEnabled) return
        try {
            val alarm = getAlarmTone()
            if (alarm != null) {
                alarm.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 350)
            } else {
                playNotificationSound(context, isSoundEnabled)
            }
        } catch (_: Exception) {
            playNotificationSound(context, isSoundEnabled)
        }
    }

    /**
     * Releases any retained audio resources on shutdown.
     */
    @Synchronized
    fun release() {
        try { clickToneGenerator?.release() } catch (_: Exception) {}
        clickToneGenerator = null
        try { notificationToneGenerator?.release() } catch (_: Exception) {}
        notificationToneGenerator = null
        try { alarmToneGenerator?.release() } catch (_: Exception) {}
        alarmToneGenerator = null
    }
}

