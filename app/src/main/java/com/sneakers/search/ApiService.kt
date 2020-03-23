package com.sneakers.search

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ApiService: IntentService(ApiService::class.java.canonicalName) {
    companion object {
        val REQUEST_ERROR = 0
        val REQUEST_SUCCESS = 1
        val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
        fun createRequest(context: Context?, route: String, requestBody: String?): Request {
            val requestBuilder = Request.Builder()
            requestBuilder.url(route)
            try {
                val body = JSONObject(requestBody ?: "{}")
                requestBuilder.post(RequestBody.create(JSON, body.toString()))
                Log.i("QKS", body.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return requestBuilder.build()
        }
    }
    override fun onHandleIntent(intent: Intent?) {
        val receiver: ResultReceiver? = intent!!.getParcelableExtra("receiver")
        try {
            val request: Request = createRequest(
                this,
                intent.getStringExtra("url"),
                intent.getStringExtra("requestBody")
            )
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, exception: IOException) {
                    receiver!!.send(REQUEST_ERROR, Bundle.EMPTY)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val bundle = Bundle()
                        if (response.isSuccessful) {
                            val jsonResponse = JSONObject(response.body!!.string())
                            bundle.putString("data", jsonResponse.getString("data"))
                            receiver!!.send(REQUEST_SUCCESS, bundle)
                        } else {
                            val jsonResponse = JSONObject(response.body!!.string())
                            bundle.putString("message", jsonResponse.getString("message"))
                            receiver!!.send(REQUEST_ERROR, bundle)
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        receiver!!.send(REQUEST_ERROR, Bundle.EMPTY)
                    }
                }
            })
        }catch (exception: Exception){
            exception.printStackTrace()
            receiver!!.send(REQUEST_ERROR, Bundle.EMPTY)
        }
    }

}