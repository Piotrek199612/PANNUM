package com.example.kjankiewicz.android_05c02

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MyMainActivity : AppCompatActivity() {

    private lateinit var secondActivityRadioButton: RadioButton
    private lateinit var makeCalcRadioButton: RadioButton
    private lateinit var wwwPanumRadioButton: RadioButton
    private lateinit var wwwJRadioButton: RadioButton

    private lateinit var valueForCalcEditText: EditText
    private lateinit var letsGoButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

        secondActivityRadioButton = findViewById(R.id.secondActivityRadioButton)
        makeCalcRadioButton = findViewById(R.id.makeCalcRadioButton)
        wwwPanumRadioButton = findViewById(R.id.wwwPanumRadioButton)
        wwwJRadioButton = findViewById(R.id.wwwJRadioButton)

        valueForCalcEditText = findViewById(R.id.valueForCalcEditText)

        /*<intent-filter>
        <action android:name="com.example.kjankiewicz.android_05c01.CALC" />
        <category android:name="android.intent.category.DEFAULT"/>
        </intent-filter>*/

        letsGoButton = findViewById(R.id.letsGoButton)
        letsGoButton.setOnClickListener {
            var intent = Intent()
            when {
                secondActivityRadioButton.isChecked -> {
                    /* DONE: Uruchom aktywność MySecondActivity za pomocą jawnej intencji */
                    intent.setClassName(this@MyMainActivity,
                            "com.example.kjankiewicz.android_05c02.MySecondActivity")
                    startActivity(intent)

                }
                makeCalcRadioButton.isChecked -> {
                    /* DONE: Uruchom aktywność aplikacji Android-05c01 umożliwiającą wykonywanie obliczeń.
                     * Przekaż jej parametr pochodzący z elementu aktywności
                     * dostępnego za pomocą atrybutu valueForCalcEditText
                     * jako kod żądania wykorzystaj stałą GET_CALC_RESULT.
                     * Skorzystaj intencji niejawnej - niech system sam dobierze właściwą aktywność na podstawie akcji */
                    intent.action = "com.example.kjankiewicz.android_05c01.CALC"
                    intent.putExtra("myX", Integer.parseInt(valueForCalcEditText.text.toString()))
                    startActivityForResult(intent, GET_CALC_RESULT)
                }
                wwwPanumRadioButton.isChecked -> {
                    /* DONE: Uruchom aktywność pozwalającą na oglądnięcie strony http://jankiewicz.pl/studenci/panum.html
                    * Skorzystaj z intencji niejawnej
                    * W przypadku istnienia wielu komponentów mogących obsłużyć utworzoną intencję zapytaj użytkownika
                    * o właściwy komponent za pomocą okna dialogowego z tytułem "Otwórz stronę za pomocą..." */

                    val myPage = Uri.parse("http://jankiewicz.pl/studenci/panum.html")
                    intent.action = Intent.ACTION_VIEW
                    intent.data = myPage
                    if (intent.resolveActivity(packageManager) != null){
                        intent = Intent.createChooser(intent, "Otwórz stronę za pomocą: ")
                        startActivity(intent)
                    }
                }
                wwwJRadioButton.isChecked -> {
                    /* DONE: Uruchom aktywność pozwalającą na oglądnięcie strony http://jankiewicz.pl
                     * Skorzystaj z intencji niejawnej */
                    val myPage = Uri.parse("http://jankiewicz.pl")
                    intent.action = Intent.ACTION_VIEW
                    intent.data = myPage
                    if (intent.resolveActivity(packageManager) != null){
                        startActivity(intent)
                    }


                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == GET_CALC_RESULT) {
                /* DONE: Odbierz wynik z wywołanej aktywności
                 * i wstaw go do elementu dostępnego za pomocą atrybutu valueForCalcEditText */
                valueForCalcEditText.setText(data?.extras?.getInt("myX", 0).toString())
            }
        }
    }

    companion object {

        internal var GET_CALC_RESULT = 1
    }
}
