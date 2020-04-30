package com.example.kjankiewicz.android_09c01

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_my_main.*


class MyMainActivity : Activity() {

    private lateinit var downloadedBitmaps: Array<Bitmap?>
    private var bitmapToShow: Int = 0

    internal var slideShowOk: Boolean? = true

    internal var batteryStatusReceiver: BroadcastReceiver? = null

    internal var batteryIntentFilter: IntentFilter? = null

    internal var myBitmaps: Array<Bitmap>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

        val filesToDownload = arrayOf(R.raw.b2elements010, R.raw.b17biddy004, R.raw.b17geoff_vane057, R.raw.b17mattphilip019)
        slideShowProgressBar.max = filesToDownload.size
        slideShowProgressBar.progress = 0

        downloadedBitmaps = downloadFiles(filesToDownload)

        doSlideShow()

    }

    private fun downloadFiles(files: Array<Int>): Array<Bitmap?> {
        val myBitmaps = arrayOfNulls<Bitmap>(files.size)

        for (i in files.indices) {
            myBitmaps[i] = BitmapFactory.decodeResource(resources, files[i])
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            slideShowProgressBar.progress = i + 1
        }

        return myBitmaps
    }

    fun doSlideShow() {
        while (true) {
            slideShowImageView.setImageBitmap(downloadedBitmaps[bitmapToShow])
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            bitmapToShow++
            if (bitmapToShow == downloadedBitmaps.size)
                bitmapToShow = 0
        }
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

    class DownloadBitmaps(): AsyncTask<Int, Int, Array<Bitmap>>() {

        override fun doInBackground(vararg files: Int?): Array<Bitmap>? {
            /* TODO: Zaimplementuj "pobieranie" plików do wnętrza tablicy bitmap
             * Po "pobraniu" każdego z plików wywołaj stosowną metodę umożliwiając
             * informowanie użytkownika o postępie */

            return null
        }

        override fun onPostExecute(result: Array<Bitmap>) {
            /* TODO: Przypisz do atrybutu downloadedBitmaps wynik
             * działania metody doInBackground()
             * Wyzeruj zmienną bitmapToShow */

        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            /* TODO: Poinformuj użytkownika o postępie prac w metodzie
             * doInBackground()
             * Wykorzystaj do tego celu pasek postępu */

        }
    }


}
