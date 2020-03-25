package com.example.kjankiewicz.android_03c01

import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem


class MyMainActivity : AppCompatActivity(), MyFragmentMain.ButtonPressListener
/* DONE: Zadeklaruj w aktywności implementację interfejsu ButtonPressListener
   dostarczanego przez fragment MyFragmentMain */ {


    private val fragmentOne = MyFragmentOne()
    private val fragmentTwo = MyFragmentTwo()
    private val fragmentThree = MyFragmentThree()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        /* DONE: Załaduj menu opcji z pliku definicji my_main.xml,
           skorzystaj z poniższego kodu, w miejscu trzech kropek wstaw odwołanie do zasobu będącego definicją menu */
           menuInflater.inflate(R.menu.my_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            R.id.goto_fragment_one -> replaceFragment(1)
            R.id.goto_fragment_two -> replaceFragment(2)
            R.id.goto_fragment_three -> replaceFragment(3)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(index: Int) {
        val anotherFragment: Fragment
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        /* DONE: W zależności od wartości parametru index zamień składową układu aktywności o identyfikatorze
           R.id.myFragmentAnother na fragment związany z odpowiednią klasą.
        * */
        when (index) {
            1 -> {
                ft.replace(R.id.myFragmentAnother, fragmentOne)
            }
            2 -> {
                ft.replace(R.id.myFragmentAnother, fragmentTwo)
            }
            3 -> {
                ft.replace(R.id.myFragmentAnother, fragmentThree)
            }
        }
        ft.commit()
    }

    /* DONE: Zaimplementuj brakującą metodę interfejsu ButtonPressListener */
    override fun onButtonPressed(button: Int){
        replaceFragment(button)
    }

}
