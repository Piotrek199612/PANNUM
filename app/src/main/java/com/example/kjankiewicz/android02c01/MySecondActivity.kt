package com.example.kjankiewicz.android02c01

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View


class MySecondActivity : Activity() {

    private var activityTag = "SecondActivityTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_second)
        Log.i(activityTag, "onCreate()")

    }

    override fun onStart() {
        super.onStart()
        Log.i(activityTag, "onStart()")
        // The activity is about to become visible.
    }

    override fun onResume() {
        super.onResume()
        Log.i(activityTag, "onResume()")
        // The activity has become visible (it is now "resumed").
    }

    override fun onPause() {
        super.onPause()
        Log.i(activityTag, "onPause()")
        // Another activity is taking focus (this activity is about to be "paused").
    }

    override fun onStop() {
        super.onStop()
        Log.i(activityTag, "onStop()")
        // The activity is no longer visible (it is now "stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(activityTag, "onDestroy()")
        // The activity is about to be destroyed.
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.my_second, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    fun runWebBrowser(view: View) {
        // Do something in response to button
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://jankiewicz.pl/studenci/panum.html"))
        startActivity(intent)
    }

}
