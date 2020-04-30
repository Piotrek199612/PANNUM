package com.example.student.android_08c01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener {
            MyNetworkCheckIntentService.startActionFoo(
                applicationContext,
                frequencyEditText.text.toString(), "0"
            )
        }

        showButton.setOnClickListener {
            try {
                val directory = applicationContext.filesDir
                val file = File(directory, NETWORK_HISTORY_LOG)
                val stringBuffer = StringBuilder()
                file.forEachLine { stringBuffer.appendln(it) }
                networkHistoryTextView.text = stringBuffer.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        clearButton.setOnClickListener {
            MyNetworkCheckIntentService.startActionBaz(applicationContext, "", "")
        }
    }
}
