package com.vpmedia.vbotsdksample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.messaging.FirebaseMessaging
import com.vpmedia.sdkvbot.client.VBotClient
import com.vpmedia.sdkvbot.en.CallState

class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var client: VBotClient
        var state: CallState = CallState.Null
        lateinit var tokenFirebase: String

        @SuppressLint("StaticFieldLeak")
        lateinit var callManager: CallManager

        // kiểm tra khởi tạo VBotClient
        fun clientExists(): Boolean {
            return ::client.isInitialized
        }

        //Khởi tạo VBotClient
        fun initClient(context: Context) {
            if (clientExists() && client.isSetup()) {
                Log.d("LogApp", "Skipping Client creation")
                return
            }
            Log.d("LogApp", "startClient")
            client = VBotClient(context)
        }

        fun initCallManager(context: Context, hashMap: HashMap<String, String>) {
            if (callManagerExists()) {
                return
            }
            callManager = CallManager(context, hashMap)
        }

        fun callManagerExists(): Boolean {
            return ::callManager.isInitialized
        }
    }

    override fun onCreate() {
        super.onCreate()
        //Lấy firebase Token
        getTokenFirebase()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    private fun getTokenFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            try {
                if (task.isSuccessful) {
                    val token = task.result
                    if (!token.isNullOrEmpty()) {
                        tokenFirebase = token
                        Log.d("token", token)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}