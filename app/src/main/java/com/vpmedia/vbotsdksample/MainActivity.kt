package com.vpmedia.vbotsdksample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vpmedia.sdkvbot.client.ClientListener
import com.vpmedia.sdkvbot.domain.pojo.mo.Hotline
import com.vpmedia.sdkvbot.en.AccountRegistrationState
import com.vpmedia.sdkvbot.en.CallState
import com.vpmedia.vbotsdksample.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var listener = object : ClientListener(), ChooseHotline.ListenerBottomSheet {
        override fun onAccountRegistrationState(status: AccountRegistrationState, reason: String) {
            updateView()
        }

        override fun onCallState(state: CallState) {
            if (state == CallState.Outgoing) {
                MyApplication.state = state
                startActivity(
                    Intent(this@MainActivity, CallActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    )
                )
            }
        }

        override fun onErrorCode(erCode: Int, message: String) {
            super.onErrorCode(erCode, message)
            Toast.makeText(
                this@MainActivity,
                "erCode=$erCode--message=$message",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onListHotline(listHotline: List<Hotline>) {
            val chooseHotlineCallBottomSheet = ChooseHotline()
            chooseHotlineCallBottomSheet.show(
                supportFragmentManager, "chooseHotlineCallBottomSheet"
            )
            chooseHotlineCallBottomSheet.setListener(this, listHotline)
            super.onListHotline(listHotline)
        }

        override fun onClickHotline(hotline: Hotline) {
            binding.etHotline.setText(hotline.phoneNumber)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MyApplication.initClient(this)
        MyApplication.client.addListener(listener)

        binding.btnStop.setOnClickListener {
            MyApplication.client.stopClient()
            finishAffinity()
        }

        binding.btnLogout.setOnClickListener {
            MyApplication.client.stopClient()
            MyApplication.client.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        binding.btnDelete.setOnClickListener {
            MyApplication.client.unregisterAndDeleteAccount()
            updateView()
        }

        binding.btnAddAccount1.setOnClickListener {
            MyApplication.client.startClient()
        }

        binding.btnSelectHotline.setOnClickListener {
            MyApplication.client.getListHotline()
        }


        binding.btnCall.setOnClickListener {
            if (hasPermission(this, Manifest.permission.RECORD_AUDIO) && hasPermission(
                    this, Manifest.permission.READ_PHONE_STATE
                )
            ) {
                val hotline = binding.etHotline.text.toString().trim()
                val to = binding.etNumber.text.toString().trim()
                Log.d("LogApp", to)

                if (to.isNotEmpty()) {
                    MyApplication.client.addOutgoingCall(hotline, to)
                }
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE
                    ), 1
                )
            }
        }

        updateView()

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
                    MyApplication.client.addOutgoingCall(hotline, to)
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
}