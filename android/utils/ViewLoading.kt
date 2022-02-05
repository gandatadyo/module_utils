package com.donasi.donasi.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.donasi.donasi.databinding.DialogLoadingBinding

class ViewLoading {
    lateinit var dialogLoading: Dialog
    lateinit var dialogLoadingBinding: DialogLoadingBinding

    fun init(context: Context, layoutInflater: LayoutInflater){
        dialogLoading = Dialog(context)
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoadingBinding = DialogLoadingBinding.inflate(layoutInflater)
        dialogLoading.setContentView(dialogLoadingBinding.root)
        dialogLoading.setCancelable(false)
    }
    fun setTitle(title:String){
        dialogLoadingBinding.lblTitle.text = title
    }
    fun show(){
        dialogLoading.show()
    }
    fun hide(){
        dialogLoading.hide()
    }
}