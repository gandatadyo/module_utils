package com.donasi.donasi.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class ViewInputDate() {
    private lateinit var context:Context
    fun init(context: Context){
        this.context = context
    }
    fun show(calender:Calendar?,clickListener:(Calendar)->Unit){
        var year = 0
        var month = 0
        var day = 0
        if(calender==null){
            val calenderTemp = Calendar.getInstance()
            year = calenderTemp.get(Calendar.YEAR)
            month = calenderTemp.get(Calendar.MONTH)
            day = calenderTemp.get(Calendar.DAY_OF_MONTH)
        }else{
            year = calender.get(Calendar.YEAR)
            month = calender.get(Calendar.MONTH)
            day = calender.get(Calendar.DAY_OF_MONTH)
        }

        val dpd = DatePickerDialog(this.context, { _, year, monthOfYear, dayOfMonth ->
            val calendarTemp = Calendar.getInstance()
            calendarTemp.set(Calendar.YEAR, year)
            calendarTemp.set(Calendar.MONTH, monthOfYear)
            calendarTemp.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                clickListener(calendarTemp)
        }, year, month, day)
        dpd.show()
    }
}