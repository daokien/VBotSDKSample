package com.vpmedia.vbotsdksample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vpmedia.sdkvbot.en.CallState
import kotlin.random.Random


class FirebaseService : FirebaseMessagingService() {


    //nhận notify
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val hashMap = HashMap<String, String>()
        for (key in remoteMessage.data.keys) {
            hashMap[key] = remoteMessage.data[key].toString()
        }
        val type = hashMap["type"]
        Log.d("logCall", "data=$hashMap")
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val message = remoteMessage.notification!!.body
            Log.d("logCall", "noti thường -- title=$title -- message=$message")
            sendNotification("$title", "$message")
        } else {
            Log.d("logCall", "noti data")
            try {
                when (type) {
                    "3" -> {
                        val offCall = hashMap["offCall"]
                        val transId = hashMap["transId"].toString()
                        val hotlineName = hashMap["hotlineName"].toString()
                        val name = hashMap["name"].toString()
                        if (offCall != null) {
                            if (offCall == "0") {
                                //call incoming
                                MyApplication.state = CallState.Incoming
                                val intent = Intent(this, CallActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.putExtra("transId",transId)
                                intent.putExtra("hotlineName",hotlineName)
                                intent.putExtra("name",name)
                                startActivity(intent)
                            } else if (offCall == "1") {
                                //end call
                                MyApplication.client.endCall()
                            }
                        }
                    }
                }
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    //tạo notify trên điện thoại
    private fun sendNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val channelId = "vbot_sdk"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_va_logo_notification)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_va_logo_notification
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(Random.nextInt(0, 9999), notificationBuilder.build())
    }
}

