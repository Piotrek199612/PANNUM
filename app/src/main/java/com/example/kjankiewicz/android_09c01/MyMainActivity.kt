package com.example.kjankiewicz.android_09c01

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_my_main.*
import java.lang.ref.WeakReference


class MyMainActivity : Activity() {

    private lateinit var downloadedBitmaps: Array<Bitmap?>
    private var bitmapToShow: Int = 0

    lateinit var slideShowHandler: Handler
    lateinit var slideShowRunnable: Runnable

    internal var slideShowOk: Boolean = true

    internal var batteryStatusReceiver: BroadcastReceiver? = null

    internal var batteryIntentFilter: IntentFilter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

        val filesToDownload = arrayOf(R.raw.b2elements010, R.raw.b17biddy004, R.raw.b17geoff_vane057, R.raw.b17mattphilip019)
        slideShowProgressBar.max = filesToDownload.size
        slideShowProgressBar.progress = 0


        val downloadBitmaps = DownloadBitmaps(this)
        downloadBitmaps.execute(*filesToDownload)

        batteryStatusReceiver = MyBroadcastReceiver()

    }

    override fun onResume() {
        super.onResume()
        batteryIntentFilter = IntentFilter()
        batteryIntentFilter?.addAction(Intent.ACTION_BATTERY_OKAY)
        batteryIntentFilter?.addAction(Intent.ACTION_BATTERY_LOW)
        registerReceiver(batteryStatusReceiver, batteryIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryStatusReceiver)
    }

    fun newDoSlideShow() {
        slideShowRunnable = Runnable {
            slideShowImageView.setImageBitmap(downloadedBitmaps[bitmapToShow])
            bitmapToShow++
            if (bitmapToShow >= downloadedBitmaps.size) bitmapToShow = 0
        }
        slideShowHandler = Handler()
        val thread = object : Thread() {
            override fun run() {

                try {
                    while (true) {
                        sleep(5000)
                        /* DONE: Wyślij do obiektu klasy Handler stosowne zadanie */
                        if (slideShowOk)
                            slideShowHandler.post(slideShowRunnable)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.my_main, menu)
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

    inner class MyBroadcastReceiver :BroadcastReceiver (){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (action == Intent.ACTION_BATTERY_OKAY)
                slideShowOk = true
            else if (action == Intent.ACTION_BATTERY_LOW)
                slideShowOk = false
        }

    }

    class DownloadBitmaps(activity: MyMainActivity) : AsyncTask<Int, Int, Array<Bitmap?>>() {
        private val mMainActivity = WeakReference(activity)

        override fun doInBackground(vararg files: Int?): Array<Bitmap?>? {
            /* DONE: Zaimplementuj "pobieranie" plików do wnętrza tablicy bitmap
             * Po "pobraniu" każdego z plików wywołaj stosowną metodę umożliwiając
             * informowanie użytkownika o postępie */
            val myBitmaps = arrayOfNulls<Bitmap?>(files.size)
            val activity = mMainActivity.get()
            if (activity == null || activity.isFinishing) return null

            for (i in files.indices) {
                myBitmaps[i] = BitmapFactory.decodeResource(activity.resources, files[i]!!)
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                publishProgress(i + 1)
            }
            return myBitmaps
        }

        override fun onPostExecute(result: Array<Bitmap?>) {
            /* DONE: Przypisz do atrybutu downloadedBitmaps wynik
             * działania metody doInBackground()
             * Wyzeruj zmienną bitmapToShow */
            val activity = mMainActivity.get()
            if (activity == null || activity.isFinishing) return

            activity.downloadedBitmaps = result
            activity.bitmapToShow = 0
            activity.newDoSlideShow()
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            /* DONE: Poinformuj użytkownika o postępie prac w metodzie
             * doInBackground()
             * Wykorzystaj do tego celu pasek postępu */

            val activity = mMainActivity.get()
            if (activity == null || activity.isFinishing) return
            activity.slideShowProgressBar.progress = values[0]!!
        }
    }


}
