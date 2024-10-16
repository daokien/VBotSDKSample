package com.vpmedia.vbotsdksample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vpmedia.sdkvbot.client.ClientListener
import com.vpmedia.sdkvbot.en.AccountRegistrationState
import com.vpmedia.vbotsdksample.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var listener = object : ClientListener() {
        //Lắng nghe trạng thái Account register
        override fun onAccountRegistrationState(status: AccountRegistrationState, reason: String) {
            loginState(status)
        }

        //Lắng nghe lỗi
        override fun onErrorCode(erCode: Int, message: String) {
            super.onErrorCode(erCode, message)
            runOnUiThread {
                binding.tvStatus.text = "Error: $erCode -- $message"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyApplication.initClient(this)
        MyApplication.client.addListener(listener)

        //click button login
        binding.btnLogin.setOnClickListener {
            val token = binding.etToken.text.toString().trim()
            when {
                token.isEmpty() -> {
                    Toast.makeText(this, "Token is empty", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    binding.tvStatus.text = "Loading"
                    MyApplication.client.connect(token, MyApplication.tokenFirebase)
                }
            }
        }
        //start vbot client
        MyApplication.client.setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            MyApplication.client.removeListener(listener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //check state register account
    private fun loginState(state: AccountRegistrationState) {
        Log.d("LogApp", "state=$state")

        when (state) {
            AccountRegistrationState.Ok -> {
                MyApplication.client.removeListener(listener)
                //chuyển màn hình main
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }

            else -> {
                binding.tvStatus.text = "Error"
            }
        }
    }
}