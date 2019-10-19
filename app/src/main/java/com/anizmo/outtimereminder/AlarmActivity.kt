package com.anizmo.outtimereminder

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.os.VibrationEffect
import android.os.Build
import android.os.Vibrator
import android.view.View
import kotlinx.android.synthetic.main.activity_alarm.*

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        var hkgBold = Typeface.createFromAsset(assets,  "fonts/HKGrotesk-Bold.ttf")
        var hkgLight = Typeface.createFromAsset(assets,  "fonts/HKGrotesk-Light.ttf")
        var hkgMedium = Typeface.createFromAsset(assets,  "fonts/HKGrotesk-Medium.ttf")
        var hkgRegular = Typeface.createFromAsset(assets,  "fonts/HKGrotesk-Regular.ttf")
        var hkgSemiBold = Typeface.createFromAsset(assets,  "fonts/HKGrotesk-SemiBold.ttf")

        happy_txt.typeface = hkgBold
        done_text.typeface = hkgBold
        toolbarText.typeface = hkgSemiBold
        credits.typeface = hkgMedium

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(10000)
        }

        done.setOnClickListener {
            v.cancel()
            finish()
        }
    }
}
