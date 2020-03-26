package com.example.student.android04c01

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toastButton:Button? = this.findViewById(R.id.create_toast_button)
        toastButton?.setOnClickListener {
            Toast.makeText(this, resources.getText(R.string.toast_text), Toast.LENGTH_LONG)
                .show()
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
}
