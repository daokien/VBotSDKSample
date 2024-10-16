package com.vpmedia.vbotsdksample

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.vpmedia.sdkvbot.client.ClientListener
import com.vpmedia.sdkvbot.en.CallState

class CallManager(val context: Context, val hashMap: HashMap<String, String>) {

    private var listener = object : ClientListener() {
        override fun onCallState(state: CallState) {
            super.onCallState(state)
            Log.d("Kdkahkdhsad", state.name)

            MyApplication.state = state
            when (state) {
                CallState.Incoming -> {
                    MyApplication.client.startRinging()
                }

                CallState.Connecting, CallState.Calling -> {
                    MyApplication.client.stopRinging()
                    val hotlineName = hashMap["hotlineName"].toString()
                    val name = hashMap["name"].toString()
                    val intent = Intent(context, CallActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("hotlineName", hotlineName)
                    intent.putExtra("name", name)
                    context.startActivity(intent)
                    MyApplication.client.removeListener(this)
                }

                CallState.Confirmed -> {
                }

                CallState.Disconnected -> {
                    MyApplication.client.stopRinging()
                    MyApplication.client.removeListener(this)
                }

                else -> {
//                    MyApplication.client.stopRinging()
//                    MyApplication.client.removeListener(this)
                }
            }
        }
    }

    fun incomingCall() {
        Handler(Looper.getMainLooper()).post {
            MyApplication.initClient(context)
            if (!MyApplication.client.isSetup()) {
                MyApplication.client.setup()
            }
            MyApplication.client.addListener(listener)
            MyApplication.client.notificationCall(hashMap)
        }
    }
}