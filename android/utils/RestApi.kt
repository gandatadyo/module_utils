package com.donasi.donasi.utils

import android.app.Activity
import android.content.Context
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.donasi.donasi.R
import com.jacksonandroidnetworking.JacksonParserFactory
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class RestApi {
    fun requestHttp(activity: Activity, urlPath: String, params: HashMap<String, String>, funcResponseSuccess: (String) -> Unit, funcResponseFailed: (String) -> Unit){
        if(!ModuleGlobal().isNetworkAvailable(activity.baseContext)) {
            ModuleGlobal().showToast(activity, activity.baseContext.getString(R.string.resulterrorconnection))
        } else {
            val url = ModuleGlobal().getIPServer(activity.baseContext)+urlPath
            val jsonObject = JSONObject()
            try {
                for(key in params.keys){
                    jsonObject.put(key, params[key])
                }
                jsonObject.put("versionapps", activity.packageManager.getPackageInfo(activity.packageName, 0).versionName)
                jsonObject.put("idhardware", ModuleGlobal().getHardWareID(activity.applicationContext))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            AndroidNetworking.post(url)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {
                        funcResponseSuccess(response.toString())
                    }

                    override fun onError(anError: ANError?) {
                        funcResponseFailed(anError.toString())
                    }

                })
        }
    }

    fun uploadImages(context: Context, file: File, funcSuccess:(String)->Unit, funcError:(String)->Unit){
        val urlHttp = ModuleGlobal().getIPServer(context)+"/upload"
        AndroidNetworking.setParserFactory(JacksonParserFactory())
        AndroidNetworking.upload(urlHttp)
            .addMultipartFile("myfile", file)
//                    .addMultipartParameter("mode", "uploadpayment")
//                    .addMultipartParameter("docnumber", docnumberGlobal)
//                    .setTag("ayambakarpakd")
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener { _, _ ->
                // do anything with progress
            }
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    funcSuccess(response.toString())
                }
                override fun onError(anError: ANError?) {
                    funcError(anError.toString())
                }
            })
    }
}