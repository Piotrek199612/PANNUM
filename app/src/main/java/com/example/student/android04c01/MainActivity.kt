package com.example.student.android04c01

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity() {
    val GREEN_CHANNEL_ID = "com.example.student.android04c01.green_chanel";
    val GREEN_NOTIFICATION_ID = 123
    private var mManager: NotificationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.green_channel_name)
            val description = getString(R.string.green_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(GREEN_CHANNEL_ID,name, importance)
            channel.description = description
            getNotificationManager()?.createNotificationChannel(channel)
        }

        val toastButton:Button? = this.findViewById(R.id.create_toast_button)
        toastButton?.setOnClickListener {
            Toast.makeText(this, resources.getText(R.string.toast_text), Toast.LENGTH_LONG)
                .show()
        }

        val createNotificationButton:Button? = this.findViewById(R.id.create_notification_button)
        createNotificationButton?.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            val bm = BitmapFactory.decodeResource(resources,R.drawable.oak)

            val notBuilder = NotificationCompat.Builder(this, GREEN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                .setLargeIcon(bm)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(resources.getString(R.string.notification_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            mManager?.notify(GREEN_NOTIFICATION_ID, notBuilder.build())
        }

        val updateNotificationButton:Button? = this.findViewById(R.id.update_notification_button)
        updateNotificationButton?.setOnClickListener {
            mManager?.cancel(GREEN_NOTIFICATION_ID)
        }

    }

    override fun onBackPressed() {
        OnExitDialogFragment().show(supportFragmentManager, "")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.close) {
            OnExitDialogFragment().show(supportFragmentManager, "")
            return true
        } else super.onOptionsItemSelected(item)
    }

    class OnExitDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity!!)
            builder.setMessage(R.string.do_you_want_exit)
                    .setPositiveButton(R.string.yes) { _, _ -> activity!!.finish() }
                    .setNegativeButton(R.string.cancel) { _, _ ->  }
            return builder.create()
        }
    }

    private fun getNotificationManager(): NotificationManager?{
        if (mManager == null) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager
    }
}
