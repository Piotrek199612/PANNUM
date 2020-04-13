package com.example.kjankiewicz.android_05c01

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView


class MyCalcActivity : AppCompatActivity() {

    private lateinit var xTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_calc)

        xTextView = findViewById(R.id.xTextView)
        val intent = intent
        xTextView.text = intent.getIntExtra("myX", 0).toString()

        val plusButton = findViewById<Button>(R.id.plusButton)
        plusButton.setOnClickListener { returnIntent(1) }
        val minusButton = findViewById<Button>(R.id.minusButton)
        minusButton.setOnClickListener { returnIntent(2) }
        val timesButton = findViewById<Button>(R.id.timesButton)
        timesButton.setOnClickListener { returnIntent(3) }
    }

    private fun returnIntent(typeOfCalc: Int) {

        var myX = Integer.parseInt(xTextView.text.toString())
        myX = when (typeOfCalc) {
            1 -> myX + 2
            2 -> myX - 2
            else -> myX * 2
        }

        /* TODO: Zakończ działanie aktywności zwracając wynik obliczeń umieszczony w zmiennej myX */

        finish()
    }

}
