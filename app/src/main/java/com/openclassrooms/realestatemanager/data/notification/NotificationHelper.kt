package com.openclassrooms.realestatemanager.data.notification

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.MainActivity
import com.openclassrooms.realestatemanager.ui.MainActivity.Companion.PERMISSION_REQUEST_CODE

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class NotificationHelper(private val context: Context) {

    private val channelId = "property_inserted_channel"

    // Init the notification channel
    init {
        createNotificationChannel()
    }

    // Create the notification channel with all the parameters needed
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Property Inserted"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.enableVibration(true)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Show the notification when a property is inserted in the database
    fun showPropertyInsertedNotification() {
        val notificationId = 1
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_circle_notifications_24)
            .setContentTitle(context.getString(R.string.property_inserted_notification_title))
            .setContentText(context.getString(R.string.property_inserted_notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context is MainActivity && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                notificationManager.notify(notificationId, notificationBuilder.build())
            }
        } else {
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}



