package com.example.student.android_08c01

import android.annotation.TargetApi
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.io.FileOutputStream

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.example.student.android_08c01.action.FOO"
private const val ACTION_BAZ = "com.example.student.android_08c01.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.example.student.android_08c01.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.example.student.android_08c01.extra.PARAM2"

const val NETWORK_HISTORY_LOG = "network_history.log"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class MyNetworkCheckIntentService : IntentService("MyNetworkCheckIntentService") {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        makeMeForeground()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_FOO -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionFoo(param1, param2)
            }
            ACTION_BAZ -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionBaz(param1, param2)
            }
        }
    }

    private fun makeMeForeground() {
        val notBuilder: NotificationCompat.Builder

        val bm = BitmapFactory.decodeResource(
            resources,
            R.drawable.oak_foreground)

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                ""
            }

        notBuilder = NotificationCompat.Builder(
            this, channelId)
            .setContentTitle("Foreground")
            .setContentText("Notification for Foreground Service")
            .setSmallIcon(R.drawable.oak_foreground)
            .setLargeIcon(bm)
            .setAutoCancel(false)

        startForeground(MainActivity.MY_NOTIFICATION_ID,
            notBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {

        val channelId = "PlayerServiceChannel"
        val channelName = "Player Service Channel"
        val channel = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {

        var networkInfo: NetworkInfo?
        var networkInfoStr = ""
        var outputStream: FileOutputStream
        var count = 0
        val interval = Integer.parseInt(param1)
        while (count < 10) {
            /* TODO: Wykorzystując instancję klasy ConnectivityManager pobierz informacje na temat aktywnego połączenia */
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkInfo = connMgr.activeNetworkInfo


            /* TODO: Jeśli aktywne połączenie istnieje przypisz do zmiennej networkInfoStr ciąg znaków* składający się z
                typu oraz podtypu aktywnego połączenia zakończony znakiem nowej linii.* W przeciwnym przypadku przypisz
                do zmiennej ciąg znaków "No Active Network \n" */
            networkInfoStr = if (networkInfo != null)
                getNetworkType(connMgr) + " " + networkInfo.subtype + "\n"
            else "No Active Network \n"
            applicationContext.openFileOutput(
                NETWORK_HISTORY_LOG,
                Context.MODE_APPEND
            ).use { it.write(networkInfoStr.toByteArray()) }
            try {
                Thread.sleep((interval * 1000).toLong())
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            count++
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getNetworkType(connMgr: ConnectivityManager): String {
        val nets = connMgr.allNetworks
        for (n in nets) {
            val np = connMgr.getNetworkCapabilities(n)
            if (np != null) {
                for (cap in hashMapOf(
                    NetworkCapabilities.TRANSPORT_CELLULAR to "MOBILE",
                    NetworkCapabilities.TRANSPORT_WIFI to "WIFI",
                    NetworkCapabilities.TRANSPORT_BLUETOOTH to "BLUETOOTH",
                    NetworkCapabilities.TRANSPORT_ETHERNET to "ETHERNET"
                ))
                    if (np.hasTransport(cap.key))
                        return cap.value
            }
        }
        return "NOTHING"
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        val outputStream: FileOutputStream
        val string = ""
        try {
            outputStream = openFileOutput(
                NETWORK_HISTORY_LOG,
                Context.MODE_PRIVATE
            )
            outputStream.write (string.toByteArray())
            outputStream.close ()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, MyNetworkCheckIntentService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, MyNetworkCheckIntentService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
