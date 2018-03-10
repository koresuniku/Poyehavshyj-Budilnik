package com.koresuniku.poyehavshyjbudilnik

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.support.v4.app.NotificationCompat

/**
 * Created by koresuniku on 3/10/18.
 */

class PoehavshyjReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)

        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.pahom)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.narabotu))

        val defaultSoundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.narabotu}")

        mBuilder.setSound(defaultSoundUri)
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mBuilder.build())

        val prefs = context.getSharedPreferences(context.getString(R.string.narabotu), Context.MODE_PRIVATE)
        prefs.edit().putBoolean(context.getString(R.string.is_alarm_set_key), false).apply()
        prefs.edit().putString(context.getString(R.string.alarm_string_key), "").apply()
    }
}