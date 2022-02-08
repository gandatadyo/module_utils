package com.donasi.donasi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.Settings
import android.text.*
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import com.donasi.donasi.R
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ModuleGlobal {
    fun saveSharedPreference(context: Context, nameshared:String, valueshared:String){
        val prefs : SharedPreferences = context.getSharedPreferences("com.donasi.donasi.utils",0)
        val editor = prefs.edit()
        editor?.putString(nameshared,valueshared)
        editor?.apply()
    }
    fun getSharedPreference(context: Context, nameshared:String):String{
        val prefs : SharedPreferences = context.getSharedPreferences("com.donasi.donasi.utils",0)
        val tempname= prefs.getString(nameshared,"")
        return tempname.toString()
    }
    
    fun currencyFormat(amount: Double):String{
        val formatter = DecimalFormat("#,###");
        return "Rp. ${formatter.format(amount)}"
    }
    fun currencySeparatorFormat(amount: Double):String{
        val formatter = DecimalFormat("#,###");
        return formatter.format(amount)
    }
    fun hideKeyBoard(context: Context, currentFocus: View){
        val inputManager: InputMethodManager =context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
    fun getIPServer(context: Context):String {
        val ipserver = if (getSharedPreference(context, "ipserver") != ""){
            getSharedPreference(context, "ipserver")
        }else{
            context.getString(R.string.ipserver)
        }
        return ipserver
    }
    @SuppressLint("HardwareIds")
    fun getHardWareID(context: Context):String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    fun showSnackbar(activity: Activity, message:String){
        Snackbar.make(activity.findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG).show()
    }
    fun showToast(ctx: Context, message:String){
        Toast.makeText(ctx,message, Toast.LENGTH_LONG).show()
    }
    fun showDialog(activity: Activity, title:String, funcOK:()->Unit){
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(title)
        builder.setCancelable(false)
        builder.setPositiveButton("Oke") { dialog, _ ->
            dialog.dismiss()
            funcOK()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    fun showConfirmDialog(activity: Activity, title:String, funcYes:()->Unit, funcNo:()->Unit){
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(title)
        builder.setCancelable(false)
        builder.setPositiveButton("Ya") { dialog, _ ->
            dialog.dismiss()
            funcYes()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
            funcNo()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    fun htmlToText(scontent:String): Spanned {
        return HtmlCompat.fromHtml(scontent, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isValidPhone(phone: String): Boolean {
        var falg = true
        if(!Patterns.PHONE.matcher(phone).matches()){
            falg = false
        }else if(phone.length<10){
            falg = false
        }else if(phone.length>15){
            falg = false
        }
        return falg

    }
    fun textSpannable(context:Context,sTextSpannable:String,sTextActionSpannable:String,clickLister:()->Unit):Spannable{

        val textHeadingSpannable = SpannableString(sTextSpannable)
        val clickSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                clickLister()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(context,R.color.co_yellow)
            }
        }

        val spanStart1 = sTextSpannable.indexOf(sTextActionSpannable)
        val spanEnd1 = spanStart1+sTextActionSpannable.length
        textHeadingSpannable.setSpan(clickSpan,spanStart1,spanEnd1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        return textHeadingSpannable
    }
    fun convertBitmapToBase64(bmp: Bitmap, context:Context):String{
        var encodedImage = ""
        val data = ByteArrayOutputStream()
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, data)
            val imageBytes = data.toByteArray()
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Toast.makeText(context, "Error convert image", Toast.LENGTH_SHORT).show()
        }
        return encodedImage
    }
    fun convertBitmapToUri(context: Context, bitmapData: Bitmap): Uri {
        val file = File(context.cacheDir,"test.png") //Get Access to a local file.
        file.delete() // Delete the File, just in Case, that there was still another File
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmapData.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()
        return file.toUri()
    }
    fun calenderToStringINA(calender: Calendar?):String {
        return if(calender!=null) {
            SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(calender.time)
        }else{
            "-"
        }
    }
    fun calenderToString(calender: Calendar):String {
        return if(calender!=null) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calender.time)
        }else{
            "-"
        }
    }
    fun stringToCalendar(sdate:String): Date? {
        return if(sdate!=""){
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(sdate)
        }else{
            null
        }

    }
    fun getVersionApps(context:Context):String{
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        return info.versionName
    }
    fun openLinktoBrowser(context:Context,url:String){
        val uris = Uri.parse(url)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
}
