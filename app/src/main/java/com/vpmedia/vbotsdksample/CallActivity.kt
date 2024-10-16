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
    private var ismic = true
    private var speak = false
    private var audioManager: AudioManager? = null
    private var bAnswer = true
    private var hold = false

    private var listener = object : ClientListener() {
        //lắng nghe trạng thái cuộc gọi
        override fun onCallState(state: CallState) {
            MyApplication.state = state
            when (state) {
                CallState.Incoming -> {
                    Log.d("jshdjs", state.name)
                    micUpdate(ismic)
                }

                CallState.Connecting -> {
                    MyApplication.client.stopRinging()
                    speakUpdate(false)
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

        //hiển thị màn hình khí bị khoá
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        MyApplication.initClient(this)
        if (!MyApplication.client.isSetup()) {
            MyApplication.client.setup()
        }
        MyApplication.client.addListener(listener)

        //click button mic
        binding.btnMic.setOnClickListener {
            if (MyApplication.client.isCall()) {
                micUpdate(!ismic)
            }
        }
        //click button speak
        binding.btnSpeak.setOnClickListener {
            speakUpdate(!speak)
        }
        //click button answer
        binding.btnAnswer.setOnClickListener {
            MyApplication.client.stopRinging()
            bAnswer = true
            binding.llIncoming.visibility = View.GONE
            binding.btnHangUp.visibility = View.VISIBLE
            if (MyApplication.client.isCall()) {
                MyApplication.client.answerCall()
            } else {
                //chưa có call -> chờ có call để kết nối
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
                                Log.d("hsjdhs", "Đang kết nối")
                            } else {
                                Log.d("hsjdhs", "answerIncomingCall")
                                MyApplication.client.answerCall()
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }
        //click button hold
        binding.btnHold.setOnClickListener {
            hold = !hold
            MyApplication.client.holdCall(hold)
        }
        //click button decline
        binding.btnDecline.setOnClickListener {
            MyApplication.client.declineIncomingCall(true)
        }
        //click button hangup
        binding.btnHangUp.setOnClickListener {
            MyApplication.client.endCall()
        }

        //khởi tạo audio manager
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val hotlineName = intent.getStringExtra("hotlineName")
        var name = intent.getStringExtra("name").toString()
        if (hotlineName != null) {
            binding.tvHotline.text = "Hotline: $hotlineName"
        }
        if (name.isEmpty()) {
            name = MyApplication.client.callName().toString()
        }
        binding.tvName.text = name
        when (MyApplication.state) {
            CallState.Incoming -> {
                binding.btnHangUp.visibility = View.GONE
                binding.llIncoming.visibility = View.VISIBLE
                speak = true
                MyApplication.client.startRinging()
            }

            CallState.Calling -> {
                binding.btnHangUp.visibility = View.VISIBLE
                binding.llIncoming.visibility = View.GONE
                speak = false
                micUpdate(ismic)
            }

            CallState.Connecting, CallState.Confirmed -> {
                MyApplication.client.stopRinging()
                speakUpdate(false)
                binding.llIncoming.visibility = View.GONE
                binding.btnHangUp.visibility = View.VISIBLE
                binding.tvStatus.text = MyApplication.state.toString()
                startTime()
                micUpdate(ismic)
            }

            else -> {

            }
        }

        speakUpdate(speak)

        binding.tvStatus.text = MyApplication.state.toString()

    }

    //update mic
    private fun micUpdate(enable: Boolean) {
        ismic = enable
        MyApplication.client.muteCall(ismic)
        binding.btnMic.text = "Mic: $ismic"
    }

    //update speak
    private fun speakUpdate(enable: Boolean) {
        speak = enable
        MyApplication.client.onOffSpeaker(enable)
        binding.btnSpeak.text = "Speak: $speak"
    }

    //bật tắt loa
    private fun isSpeak(enable: Boolean) {
        if (enable) {
            audioManager?.mode = AudioManager.MODE_IN_CALL
        } else {
            audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
        }
        audioManager?.isSpeakerphoneOn = enable
    }

    //đếm giây
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

    //tắt màn hình call
    private fun destroy() {
        bAnswer = false
        MyApplication.client.stopRinging()
        binding.cTime.stop()
        MyApplication.client.removeListener(listener)
        finish()
    }
}