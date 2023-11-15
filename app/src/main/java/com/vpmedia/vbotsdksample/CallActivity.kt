package com.vpmedia.vbotsdksample

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.vpmedia.sdkvbot.client.ClientListener
import com.vpmedia.sdkvbot.en.CallState
import com.vpmedia.vbotsdksample.databinding.ActivityCallBinding

class CallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallBinding
    private var isIncoming = false
    private var ismic = true
    private var speak = false
    private var audioManager: AudioManager? = null
    private var bAnswer = true
    private var hold = false

    private var listener = object : ClientListener() {

        override fun onCallState(state: CallState) {
            MyApplication.state = state
            when (state) {
                CallState.Incoming -> {
                    micUpdate(ismic)
                }

                CallState.Connecting -> {
                    MyApplication.client.checkAndStopLocalRingBackTone()
                    if (isIncoming) {
                        speakUpdate(false)
                    }
                    binding.llIncoming.visibility = View.GONE
                    binding.btnHangUp.visibility = View.VISIBLE
                    binding.tvStatus.text = MyApplication.state.toString()
                    startTime()
                }

                CallState.Disconnected -> {
                    destroy()
                }

                else -> {

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        MyApplication.initClient(this)
        MyApplication.client.startClient()
        MyApplication.client.addListener(listener)

        binding.btnMic.setOnClickListener {
            if (MyApplication.client.isCall()) {
                micUpdate(!ismic)
            }
        }
        binding.btnSpeak.setOnClickListener {
            speakUpdate(!speak)
        }
        binding.btnAnswer.setOnClickListener {
            MyApplication.client.checkAndStopLocalRingBackTone()
            bAnswer = true
            binding.llIncoming.visibility = View.GONE
            binding.btnHangUp.visibility = View.VISIBLE
            if (MyApplication.client.isCall()) {
                MyApplication.client.answerIncomingCall()
            } else {
                Thread {
                    try {
                        while (!MyApplication.client.isCall()) {
                            Thread.sleep(500)
                            if (!bAnswer) {
                                break
                            }
                            if (!MyApplication.client.isCall()) {
                                binding.tvStatus.post {
                                    binding.tvStatus.text = "Đang kết nối"
                                }
                                Log.d("hsjdhs","Đang kết nối")
                            } else {
                                Log.d("hsjdhs","answerIncomingCall")
                                MyApplication.client.answerIncomingCall()
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }
        binding.btnHold.setOnClickListener {
            hold = !hold
            MyApplication.client.setHold(hold)
        }
        binding.btnDecline.setOnClickListener {
            MyApplication.client.declineIncomingCall(true)
        }
        binding.btnHangUp.setOnClickListener {
            MyApplication.client.hangupCall()
        }

        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (MyApplication.state == CallState.Outgoing) {
            binding.tvName.text = MyApplication.client.getRemoteAddressCall()
            binding.btnHangUp.visibility = View.VISIBLE
            binding.llIncoming.visibility = View.GONE
            speak = false
            micUpdate(ismic)
        } else {
            val transId = intent.getStringExtra("transId").toString()
            val hotlineName = intent.getStringExtra("hotlineName")
            val name = intent.getStringExtra("name").toString()
            MyApplication.client.addIncomingCall(transId)
            binding.tvName.text = name
            isIncoming = true
            if (hotlineName != null) {
                binding.tvHotline.text = "Hotline: $hotlineName"
            }
            binding.btnHangUp.visibility = View.GONE
            binding.llIncoming.visibility = View.VISIBLE
            speak = true
            MyApplication.client.startRinging()
        }

        speakUpdate(speak)

        binding.tvStatus.text = MyApplication.state.toString()

    }

    private fun micUpdate(enable: Boolean) {
        ismic = enable
        MyApplication.client.isMic(ismic)
        binding.btnMic.text = "Mic: $ismic"
    }

    @SuppressLint("SetTextI18n")
    private fun speakUpdate(enable: Boolean) {
        speak = enable
        isSpeak(enable)
        binding.btnSpeak.text = "Speak: $speak"
    }

    private fun isSpeak(enable: Boolean) {
        if (enable) {
            audioManager?.mode = AudioManager.MODE_IN_CALL
        } else {
            audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
        }
        audioManager?.isSpeakerphoneOn = enable
    }

    private fun startTime() {
        if (MyApplication.client.isCall()) {
            val duration = if (MyApplication.client.getDuration() != null) {
                MyApplication.client.getDuration()!!
            } else {
                0
            }
            binding.cTime.base = SystemClock.elapsedRealtime() - (1000 * duration)
            binding.cTime.start()
        }
    }

    private fun destroy() {
        bAnswer = false
        MyApplication.client.checkAndStopLocalRingBackTone()
        binding.cTime.stop()
        MyApplication.client.removeListener(listener)
        finish()
    }
}