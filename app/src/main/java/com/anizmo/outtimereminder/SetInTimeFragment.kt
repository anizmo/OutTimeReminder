package com.anizmo.outtimereminder


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_set_in_time.*
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import java.util.*

class SetInTimeFragment : Fragment() {

    private var listener : SetInTimeListener? = null
    var hours = "0"
    var minutes = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_in_time, container, false)
    }

    fun startTimer(){
        val thread = object : Thread() {

            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        activity?.runOnUiThread {
                            updateDisplayedClock()
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }

        thread.start()
    }

    private fun updateDisplayedClock() {
        var timeRemaining =
            Utility.getLongFromPreference(context, Utility.OUT_TIME_FOR_TODAY, 0) - System.currentTimeMillis()
        val m = (timeRemaining / 1000) / 60 % 60
        val h = (timeRemaining / 1000) / (60 * 60) % 24
        var hoursDisplay = h.toString()
        var minutesDisplay = m.toString()
        if (h.toString().length < 2) {
            hoursDisplay = "0$hoursDisplay"
        }

        if (m.toString().length < 2) {
            minutesDisplay = "0$minutesDisplay"
        }

        time_remaining.text = "$hoursDisplay:$minutesDisplay"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var hkgBold = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Bold.ttf")
        var hkgLight = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Light.ttf")
        var hkgMedium = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Medium.ttf")
        var hkgRegular = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Regular.ttf")
        var hkgSemiBold = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-SemiBold.ttf")

        toolbarText.typeface = hkgSemiBold
        start_your_day_txt.typeface = hkgBold
        end_your_day_text.typeface = hkgBold
        time_remaining.typeface = hkgMedium
        today_date.typeface = hkgMedium
        label.typeface = hkgMedium

        settings.setOnClickListener { listener?.navigateToSetWorkHoursFragment() }
        set_in_time.setOnClickListener {
            listener?.setAlarm()
            if (Utility.getBoolFromPreference(context, Utility.HAS_SET_CONTACT, false)) {
                sendWhatsAppMessage()
            }
            Utility.addToPreferences(context,Utility.HAS_STARTED_TIMER_FOR_TODAY,true)

            toggleButtonLayout()

            startTimer()
        }

        end_the_day.setOnClickListener {

            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(context!!)
            dialogBuilder.setMessage("Are you sure you want to end your day?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    listener?.cancelAlarm()
                    Utility.addToPreferences(context,Utility.HAS_STARTED_TIMER_FOR_TODAY,false)
                    set_in_time.visibility = View.VISIBLE
                    end_the_day.visibility = View.GONE
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel()
                }

            val alert = dialogBuilder.create()
//            alert.setTitle("AlertDialogExample")
            alert.show()

        }

        hours = Utility.getIntFromPreference(context,Utility.HOURS,0).toString()
        minutes = Utility.getIntFromPreference(context,Utility.MINUTES,0).toString()

        if(hours.length<2){
            hours = "0$hours"
        }

        if(minutes.length<2){
            minutes = "0$minutes"
        }

        time_remaining.text = "$hours:$minutes"

        var calender = Calendar.getInstance()
        var month = calender.get(Calendar.MONTH)
        var year = calender.get(Calendar.YEAR)
        var day = calender.get(Calendar.DAY_OF_MONTH)
        var dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)

        today_date.text = getDay(dayOfWeek)+", " + day.toString()+" " + getMonth(month + 1)+", " + (year).toString()

        if (Utility.getBoolFromPreference(context, Utility.HAS_STARTED_TIMER_FOR_TODAY, false)){
            toggleButtonLayout()
            updateDisplayedClock()
            startTimer()
        }
    }

    private fun toggleButtonLayout(){
        set_in_time.visibility = View.INVISIBLE
        end_the_day.visibility = View.VISIBLE
    }

    private fun getDay(monthNumber : Int) : String{
        when(monthNumber){
            1 -> return "Sunday"
            2 -> return "Monday"
            3 -> return "Tuesday"
            4 -> return "Wednesday"
            5 -> return "Thursday"
            6 -> return "Friday"
            7 -> return "Saturday"
            else -> return ""
        }
    }

    private fun getMonth(monthNumber : Int) : String{
        when(monthNumber){
            1 -> return "January"
            2 -> return "February"
            3 -> return "March"
            4 -> return "April"
            5 -> return "May"
            6 -> return "June"
            7 -> return "July"
            8 -> return "August"
            9 -> return "September"
            10 -> return "October"
            11 -> return "November"
            12 -> return "December"
            else -> return ""
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is SetInTimeListener){
            listener = context
        }else{

        }
    }

    private fun sendWhatsAppMessage() {
        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.action = Intent.ACTION_VIEW
        sendIntent.setPackage("com.whatsapp")
        val url = "https://api.whatsapp.com/send?phone=" + Utility.getStringFromPreference(context,Utility.PHONE_NUMBER,"not set") + "&text=" + Utility.getStringFromPreference(context,Utility.REACHED_MESSAGE,"I reached work")
        sendIntent.data = Uri.parse(url)
        if (sendIntent.resolveActivity(context?.packageManager) != null) {
            startActivity(sendIntent)
        }
    }

    interface SetInTimeListener{

        fun navigateToSetWorkHoursFragment()

        fun setAlarm()

        fun cancelAlarm()

    }

}
