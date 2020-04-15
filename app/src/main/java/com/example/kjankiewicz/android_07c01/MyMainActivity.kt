package com.example.kjankiewicz.android_07c01

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import android.support.v7.app.AppCompatActivity

class MyMainActivity : AppCompatActivity() {

    private var mSelection: String? = null
    private var mOrderBy: String? = null
    private var callsCursor: Cursor? = null
    private var mProjection: Array<String>? = null
    private var callsListItems: IntArray? = null
    private var callsCursorAdapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

        showCalls()

    }

    private fun showCalls() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), PERMISSIONS_REQUEST_READ_CALL_LOG)
        } else {

            mProjection = arrayOf(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION)

            /*TODO: Utwórz zmienne mSelection i mSelectionArgs definiujące warunek ograniczający pobierane
            * dane tylko do tych, które zawierają połączenia przychodzące.
            * Nie interesują nas wszystkie połączenia.
            * Warunek selecji powinien ostatecznie mieć postać "TYPE = 'przychodzące'"
            * Do stworzenia warunku wykorzystaj zarówno definicję warunku (mSelection) jak i wartości argumentów warunku (mSelectionArgs)
            * Rzeczywistą wartość kolumny TYPE uzyskaj z klasy kontraktu CallLog.Calls */
            mSelection = " = ? "

            /*Rzeczywistą wartość typu połączeń 'przychodzące' uzyskaj także z tej klasy (przeglądnij dostępne tam stałe)
            * zamień poniższą wartość -1 na odpowiednią stałą ze wspomnianej klasy*/
            val mSelectionArgs = arrayOf(Integer.toString(-1))

            /*TODO: Utwórz zmienną mOrderBy zawierającą klauzulę sortującą połączenia od najnowszych do najstarszych
            * Użyj właściwej stałej klasy kontraktu dla kolumny zawierającej datę */
            mOrderBy = ""

            /*TODO Utwórz cursor pozwalający na dostęp do wykonanych połączeń.
            * Skorzystaj z powyżej zdefniowanych zmiennych.
            * Zaglądnij na stronę https://developer.android.com/reference/android/provider/CallLog.Calls.html
            * aby poznać właściwą stałą zawierającą URI "tabeli" z informacjami o połączeniach  */
            callsCursor = null

            /* Lista kolumn, którą chcemy wyświetlić w ramach ListView */
            val callsListColumns = arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.DATE)

            /*TODO: Zdefiniuj tablicę wskazującą na elementy, z rozkładu my_calls_row.xml,
            *  które są przeznaczone do wyświetlenia powyższych kolumn  */
            callsListItems = intArrayOf()


            /*TODO: Utwórz obiekt adaptera pozwalający na wyświetlanie danych z kursora callsCursor
             * przy wykorzystaniu pliku rozkładu my_calls_row.xml,
             * wyświetający listę kolumn wskazaną w zmiennej callsListColumns
             * za pomocą składowych rozkładu zdefiniowanych w zmiennej callsListItems */
            callsCursorAdapter = null

            val callsListView = findViewById<ListView>(R.id.callsListView)

            callsListView.adapter = callsCursorAdapter
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CALL_LOG) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showCalls()
            } else {
                Toast.makeText(this, "Until you grant the permission, your calls cannot be displayed", Toast.LENGTH_SHORT).show()
            }
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
        if (id == R.id.activity_my_pracownicy) {
            val i = Intent(this, MyPracownicyActivity::class.java)
            startActivity(i)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val PERMISSIONS_REQUEST_READ_CALL_LOG = 100
    }
}
