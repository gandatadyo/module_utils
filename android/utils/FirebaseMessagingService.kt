package com.donasi.donasi.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.window.SplashScreen
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.donasi.donasi.R
import com.donasi.donasi.ui.home.Home
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            if (/* Check if data needs to be processed by long running job */ true) {
                scheduleJob()
            } else {
                handleNow()
            }
        }

        var message:String = ""
        remoteMessage.notification?.let {
            message = it.body.toString()
        }
        sendNotification(message)
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    override fun onNewToken(token: String) { sendRegistrationToServer(token) }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
    }

    private fun sendRegistrationToServer(token: String?) { Log.d(TAG, "sendRegistrationTokenToServer($token)") }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, Home::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_app)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun sendNotification(typemessage: String,datamessage:String) {
//        val notificationModel = NotificationModel()
//        val responseObject = JSONObject(datamessage)
//        if(responseObject.getString("typenotif")=="sendmessage") {
//            val transid = responseObject.getString("transid")
//            val docnumber = responseObject.getString("docnumber")
//            val title = responseObject.getString("caption")
//            val messages = responseObject.getString("content")
//            notificationModel.NotifTypeSendMessage(this,transid,docnumber,title,messages)
//        }
        val notificationModel = NotificationModel()
        notificationModel.showNotif(this,typemessage,datamessage)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}

class NotificationModel{

    fun showNotif(context: Context, title:String, content:String) {
        val CHANNEL_ID = context.getString(R.string.default_notification_channel_idadmin)
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val resultIntent = Intent(context, Home::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(Home::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(resultPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "\"com.app.reservasi\""
            val descriptionText = content
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(context)) {
            val notificationId = 2412
            notify(notificationId, mBuilder.build())
        }
    }
}