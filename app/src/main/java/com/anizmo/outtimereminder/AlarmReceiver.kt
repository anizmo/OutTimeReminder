package com.anizmo.outtimereminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Utility.getBoolFromPreference(context, Utility.HAS_STARTED_TIMER_FOR_TODAY, false)) {
            val i = Intent()
            i.setClassName("com.anizmo.outtimereminder", "com.anizmo.outtimereminder.AlarmActivity")
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
            Utility.addToPreferences(context,Utility.HAS_STARTED_TIMER_FOR_TODAY,false)
        }
    }

}
