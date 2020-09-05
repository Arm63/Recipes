package com.example.recipes.ui.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import com.example.recipes.R

object AppUtil {
    fun intToBoolean(b: Int): Boolean {
        return b != 0
    }

    fun booleanToInt(b: Boolean): Int {
        return if (b) 1 else 0
    }


    fun closeKeyboard(activity: Activity?) {
        if (activity != null) {
            if (activity.currentFocus != null) {
                val inputMethodManager =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    fun sendNotification(
        context: Context, cls: Class<*>?,
        title: String?, description: String?, data: String?, type: Int
    ) {
        val intent = Intent(context, cls)
        intent.putExtra(Constant.Extra.EXTRA_NOTIFY_DATA, data)
        intent.putExtra(Constant.Extra.EXTRA_NOTIFY_TYPE, type)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(cls)
        stackBuilder.addNextIntent(intent)
        val notificationPendingIntent =
            stackBuilder.getPendingIntent(type, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder =
            NotificationCompat.Builder(context, Constant.NotifyType.NOTIFICATION_CHANNEL_ID)
        builder.setSmallIcon(android.R.drawable.sym_action_chat)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.img_drawer_android
                )
            )
            .setColor(Color.GRAY)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setAutoCancel(true)
            .setContentIntent(notificationPendingIntent)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constant.NotifyType.NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Configure the notification channel.
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager.notify(type, builder.build())
    }
}