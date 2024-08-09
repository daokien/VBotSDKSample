package com.vpmedia.vbotsdksample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vpmedia.sdkvbot.client.ClientListener
import com.vpmedia.sdkvbot.domain.pojo.mo.Hotline
import com.vpmedia.sdkvbot.en.AccountRegistrationState
import com.vpmedia.sdkvbot.en.CallState
import com.vpmedia.vbotsdksample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), ChooseHotline.ListenerBottomSheet {

    private lateinit var binding: ActivityMainBinding

    private var listener = object : ClientListener() {
        //Lắng nghe trạng thái Account register
        override fun onAccountRegistrationState(status: AccountRegistrationState, reason: String) {
            updateView()
        }

        //Lắng nghe trạng thái cuộc gọi
        override fun onCallState(state: CallState) {
            //call đến mở màn hình call
            if (state == CallState.Outgoing) {
                MyApplication.state = state
                startActivity(
                    Intent(this@MainActivity, CallActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    )
                )
            }
        }

        //Lắng nghe lỗi
        override fun onErrorCode(erCode: Int, message: String) {
            super.onErrorCode(erCode, message)
            Toast.makeText(
                this@MainActivity,
                "erCode=$erCode--message=$message",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //khởi tạo
        MyApplication.initClient(this)
        //add listener
        MyApplication.client.addListener(listener)

        //click button logout
        binding.btnLogout.setOnClickListener {
            MyApplication.client.disconnect()
            //bật màn hình login
//            startActivity(Intent(this, LoginActivity::class.java))
//            finishAffinity()
        }

        //click button delete account
        binding.btnDelete.setOnClickListener {
            MyApplication.client.unregisterAndDeleteAccount()
            updateView()
        }

        //click button add account
        binding.btnAddAccount1.setOnClickListener {
            MyApplication.client.setup()
        }

        binding.btnConnect.setOnClickListener {
            MyApplication.client.connect(binding.etToken.text.toString(), MyApplication.tokenFirebase)
        }


        //click button select hotline
        binding.btnSelectHotline.setOnClickListener {
            //lấy list hotline
            CoroutineScope(Dispatchers.IO).launch {
                runBlocking {
                    val list = MyApplication.client.getHotlines()
                    if (list != null) {
                        val chooseHotlineCallBottomSheet = ChooseHotline()
                        chooseHotlineCallBottomSheet.show(
                            supportFragmentManager, "chooseHotlineCallBottomSheet"
                        )
                        chooseHotlineCallBottomSheet.setListener(this@MainActivity, list)
                    }
                }
            }
        }

        //click button call
        binding.btnCall.setOnClickListener {
            binding.btnCall.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                binding.btnCall.isEnabled = true
            }, 1000)
            if (hasPermission(this, Manifest.permission.RECORD_AUDIO) && hasPermission(
                    this, Manifest.permission.READ_PHONE_STATE
                )
            ) {
                val hotline = binding.etHotline.text.toString().trim()
                val to = binding.etNumber.text.toString().trim()
                Log.d("LogApp", to)
                //tạo call đi
                if (to.isNotEmpty()) {
                    MyApplication.client.startCall(hotline, to)
                }
            } else {
                //check quyền
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE
                    ), 1
                )
            }
        }

        updateView()

        //check quyền hiển thị trên ứng dụng khác
        if (!Settings.canDrawOverlays(this)) {
            if (isMiuiWithApi28OrMore()) {
                goToXiaomiPermissions(this)
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                @Suppress("DEPRECATION")
                startActivityForResult(intent, 23052)
            }
        }
    }

    private fun updateView() {
        binding.tvStatus.text = "State: ${MyApplication.client.getStateAccount()}"
        binding.tvName.text = "Name: ${MyApplication.client.getAccountUsername()}"
    }

    override fun onDestroy() {
        super.onDestroy()
        //xoá listener
        MyApplication.client.removeListener(listener)
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            val hotline = binding.etHotline.text.toString().trim()
            val to = binding.etNumber.text.toString().trim()
            if (to.isNotEmpty()) {
                runOnUiThread {
                    MyApplication.client.startCall(hotline, to)
                }
            }
        }
    }

    private fun isMiuiWithApi28OrMore(): Boolean {
        return isMiUi() && Build.VERSION.SDK_INT >= 26
    }

    private fun isMiUi(): Boolean {
        return getSystemProperty()?.isNotBlank() == true
    }

    private fun getSystemProperty(): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    private fun goToXiaomiPermissions(context: Context) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
    }

    override fun onClickHotline(hotline: Hotline) {
        //set số hotline
        binding.etHotline.setText(hotline.phoneNumber)
    }
}