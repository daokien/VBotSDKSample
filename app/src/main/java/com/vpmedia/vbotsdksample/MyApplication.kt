package com.vpmedia.vbotsdksample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.vpmedia.sdkvbot.client.VBotClient
import com.vpmedia.sdkvbot.en.CallState

class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var client: VBotClient
        var state: CallState = CallState.Null
        lateinit var tokenFirebase: String

        fun clientExists(): Boolean {
            return ::client.isInitialized
        }

        fun initClient(context: Context) {
            if (clientExists() && client.clientIsStart()) {
                Log.d("LogApp", "Skipping Client creation")
                return
            }
            Log.d("LogApp", "startClient")
            client = VBotClient(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        getTokenFirebase()
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