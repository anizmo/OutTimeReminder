package com.anizmo.outtimereminder

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_set_work_hours.*
import android.provider.ContactsContract
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.Editable
import androidx.core.widget.addTextChangedListener
import android.R.string.cancel
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


class SetWorkHoursFragment : Fragment() {

    var listener : SetWorkHoursListener? = null

    val PICK_CONTACT = 123

    var phoneNumber : String ?= null

    var message : String ?= null

    var countryCode : String? = null

    var name : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_work_hours, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var hkgBold = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Bold.ttf")
        var hkgLight = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Light.ttf")
        var hkgMedium = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Medium.ttf")
        var hkgRegular = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-Regular.ttf")
        var hkgSemiBold = Typeface.createFromAsset(context?.assets,  "fonts/HKGrotesk-SemiBold.ttf")

        toolbarText.typeface = hkgSemiBold
        hours.typeface = hkgMedium
        minutes.typeface = hkgMedium
        message_label.typeface = hkgMedium
        message_for_sending.typeface = hkgMedium
        msg_txt_input_layout.typeface = hkgMedium
        working_hrs_label.typeface = hkgMedium
        contact_label.typeface = hkgMedium
        contact.typeface = hkgMedium
        hrs_txt_input_layout.typeface = hkgMedium
        minutes_text_input_layout.typeface = hkgMedium
        count.typeface = hkgMedium
        contact_after_set.typeface = hkgMedium
        message_sub_label.typeface = hkgMedium
        submit_text.typeface = hkgBold


        if (Utility.getBoolFromPreference(context, Utility.HAS_SET_WORK_HOURS, false)) {
            hours.setText(Utility.getIntFromPreference(context,Utility.HOURS,0).toString())
            minutes.setText(Utility.getIntFromPreference(context,Utility.MINUTES,0).toString())
            message_for_sending.setText(Utility.getStringFromPreference(context,Utility.REACHED_MESSAGE,""))

            if(Utility.getBoolFromPreference(context, Utility.HAS_SET_CONTACT, false)){
                contact_after_set.visibility = View.VISIBLE
                name = Utility.getStringFromPreference(context,Utility.CONTACT_NAME,"")
                contact.visibility = View.GONE
                contact_after_set.text = name
            }
        }

        set_work_hours.setOnClickListener {
            if(!TextUtils.isEmpty(hours.text) && !TextUtils.isEmpty(minutes.text)){
                if(validateHours( hours.text.toString().toInt(),minutes.text.toString().toInt())){
                    Utility.addToPreferences(context,Utility.HAS_SET_WORK_HOURS,true)
                    message = message_for_sending?.text?.toString()
                    Utility.addToPreferences(context,Utility.REACHED_MESSAGE,message)
                    //countryCode = country_code.text.toString()
                    if(!(phoneNumber?.contains("+")==true)){
                        if(countryCode != null && !TextUtils.isEmpty(countryCode)){
                            if(countryCode?.contains("+")== true){
                                phoneNumber = countryCode + phoneNumber
                            }else{
                                phoneNumber = "+$countryCode$phoneNumber"
                            }
                        }
                    }
                    if(phoneNumber!=null){
                        Utility.addToPreferences(context,Utility.PHONE_NUMBER,phoneNumber)
                        Utility.addToPreferences(context,Utility.CONTACT_NAME,name)
                        Utility.addToPreferences(context,Utility.HAS_SET_CONTACT,true)
                    }
                    Utility.addToPreferences(context,Utility.HOURS,hours.text.toString().toInt())
                    Utility.addToPreferences(context,Utility.MINUTES,minutes.text.toString().toInt())
                    listener?.navigateToSetInTimeFragment()
                }else{

                }
            }


        }

        message_for_sending.addTextChangedListener { text: Editable? ->
            var charCount = text?.length
            count.text = charCount.toString()+"/120"
        }

        hours.addTextChangedListener {
            if(minutes.text?.length==1 || minutes.text?.length==2){
                if(hours.text?.length==1 || hours.text?.length==2){
                    set_work_hours.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }else{
                    set_work_hours.setBackgroundColor(resources.getColor(R.color.grey_background))
                }
            }else{
                set_work_hours.setBackgroundColor(resources.getColor(R.color.grey_background))
            }
        }

        minutes.addTextChangedListener {
            if(hours.text?.length==1 || hours.text?.length==2){
                if(minutes.text?.length==1 || minutes.text?.length==2){
                    set_work_hours.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }else{
                    set_work_hours.setBackgroundColor(resources.getColor(R.color.grey_background))
                }
            }else{
                set_work_hours.setBackgroundColor(resources.getColor(R.color.grey_background))
            }
        }

        contact_pick_input_layout.setOnClickListener {
            listener?.askPermission()
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT)
        }

        if(minutes.text?.length==1 || minutes.text?.length==2){
            if(hours.text?.length==1 || hours.text?.length==2){
                set_work_hours.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }else{
                set_work_hours.setBackgroundColor(resources.getColor(R.color.grey_background))
            }
        }else{
            set_work_hours.setBackgroundColor(resources.getColor(R.color.grey_background))
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        when (reqCode) {
            PICK_CONTACT -> if (resultCode == Activity.RESULT_OK) {

                val contactData = data!!.data
                val c = activity?.managedQuery(contactData, null, null, null, null)
                if (c?.moveToFirst() == true) {


                    val id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

                    val hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                    if (hasPhone.equals("1", ignoreCase = true)) {
                        val phones = activity?.getContentResolver()?.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null
                        )
                        phones?.moveToFirst()
                        phoneNumber = phones?.getString(phones?.getColumnIndex("data1"))
                        if(phoneNumber?.contains("+")==false){
                            showCountryCodeDialog()
                        }
                        contact.visibility = View.GONE
                    }
                    name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    contact_after_set.visibility = View.VISIBLE
                    contact_after_set.text = name
                }
            }
        }
    }

    fun showCountryCodeDialog(){
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Country Code")
        builder.setMessage("Entered contact doesn't have a valid country code. Please enter :")
        builder.setCancelable(false)
        val input = EditText(context!!)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setPositiveButton("OK"
        ) { dialog, which -> countryCode = input.text.toString() }
        builder.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is SetWorkHoursListener){
            listener = context
        }else{

        }
    }

    fun validateHours(hours: Int, minutes: Int): Boolean{
        var errors = 0

        if(hours > 24 || hours < 0){
            errors++
        }

        if(minutes>59 || minutes < 0){
            errors++
        }

        return (errors==0)
    }

    interface SetWorkHoursListener{

        fun navigateToSetInTimeFragment()

        fun navigateToSetWorkHoursFragment()

        fun askPermission()

    }

}
