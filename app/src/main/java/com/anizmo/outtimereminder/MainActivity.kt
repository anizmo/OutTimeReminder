package com.anizmo.outtimereminder

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder


class MainActivity : AppCompatActivity(), SetWorkHoursFragment.SetWorkHoursListener, SetInTimeFragment.SetInTimeListener {

    var pendingIntent : PendingIntent? = null

    var pendingIntentRequestCode = 191195

    override fun navigateToSetWorkHoursFragment() {
        val manageNudgesFragment = SetWorkHoursFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, manageNudgesFragment, Utility.HAS_SET_WORK_HOURS_FRAGMENT)
        transaction.commit()
    }

    override fun navigateToSetInTimeFragment() {
        val manageNudgesFragment = SetInTimeFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, manageNudgesFragment, Utility.HAS_SET_WORK_HOURS_FRAGMENT)
        transaction.commit()
    }

    override fun askPermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            1)
    }

    override fun setAlarm() {
        val intent = Intent(baseContext, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            baseContext, pendingIntentRequestCode, intent, 0)
        val setTime = System.currentTimeMillis() + (Utility.getIntFromPreference(this,Utility.HOURS,0)*3600*1000) + (Utility.getIntFromPreference(this,Utility.MINUTES,0)*60*1000)
        Utility.addToPreferences(this,Utility.OUT_TIME_FOR_TODAY,setTime)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, setTime, pendingIntent)

        val m = (setTime / 1000) / 60 % 60
        val h = (setTime / 1000) / (60 * 60) % 24
        var hoursDisplay = h.toString()
        var minutesDisplay = m.toString()

        if (h.toString().length < 2) {
            hoursDisplay = "0$hoursDisplay"
        }

        if (m.toString().length < 2) {
            minutesDisplay = "0$minutesDisplay"
        }

        showNotification(this,"Out Time Reminder", "Your out time alarm for today is set!",Intent())
    }

    override fun cancelAlarm() {
        pendingIntent = PendingIntent.getBroadcast(
            baseContext, pendingIntentRequestCode, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent?.cancel()
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(context: Context, title: String, body: String, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(body)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        if (!Utility.getBoolFromPreference(this, Utility.HAS_SET_WORK_HOURS, false)) {
            //navigate to set work time fragment
            navigateToSetWorkHoursFragment()
        }else{
            //navigate to set in time fragment
            navigateToSetInTimeFragment()
        }

    }
}
