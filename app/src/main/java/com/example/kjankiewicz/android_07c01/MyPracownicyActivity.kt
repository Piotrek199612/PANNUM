package com.example.kjankiewicz.android_07c01

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.GridView
import android.widget.SimpleCursorAdapter
import android.widget.Toast


class MyPracownicyActivity : AppCompatActivity() {

    internal var mDbHelper: PracZespEtatDbHelper? = null
    internal var db: SQLiteDatabase? = null
    internal var deleteResult: Int = 0
    internal var countCursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pracownicy)

        /*TODO: Utwórz instancję klasy SQLiteOpenHelper korzystając z konstruktora właściwej klasy
        * Za jej pomocą będzie możliwe operowanie na zawartości bazy danych */
        mDbHelper =
                null

        findViewById<Button>(R.id.createNewDatasetButton).setOnClickListener {

            val idPracTab = arrayOf(100, 110, 120, 130, 140, 150, 160, 170, 190, 180, 200, 210, 220, 230)
            val nazwiskoTab = arrayOf("WEGLARZ", "BLAZEWICZ", "SLOWINSKI", "BRZEZINSKI", "MORZY", "KROLIKOWSKI", "KOSZLAJDA", "JEZIERSKI", "MATYSIAK", "MAREK", "ZAKRZEWICZ", "BIALY", "KONOPKA", "HAPKE")
            val etatTab = arrayOf("DYREKTOR", "PROFESOR", "PROFESOR", "PROFESOR", "PROFESOR", "ADIUNKT", "ADIUNKT", "ASYSTENT", "ASYSTENT", "SEKRETARKA", "STAZYSTA", "STAZYSTA", "ASYSTENT", "ASYSTENT")
            val idSzefaTab = arrayOf(null, 100, 100, 100, 130, 130, 130, 130, 140, 100, 140, 130, 110, 120)
            val zatrudnionyTab = arrayOf("1968-01-01", "1973-05-01", "1977-09-01", "1968-07-01", "1975-09-15", "1977-09-01", "1985-03-01", "1992-10-01", "1993-09-01", "1985-02-20", "1994-07-15", "1993-10-15", "1993-10-01", "1992-09-01")
            val placaPodTab = arrayOf(1730.00, 1350.00, 1070.00, 960.00, 830.00, 645.50, 590.00, 439.70, 371.00, 410.20, 208.00, 250.00, 480.00, 480.00)
            val placaDodTab = arrayOf(420.50, 210.00, null, null, 105.00, null, null, 80.50, null, null, null, 170.60, null, 90.00)
            val idZespTab = intArrayOf(10, 40, 30, 20, 20, 20, 20, 20, 20, 10, 30, 30, 20, 30)

            /*TODO: Otwórz bazę danych w trybie do zapisu i pobierz do niej referencję
             * Skorzystaj z metody getWritableDatabase klasy SQLiteOpenHelper */
            db = null

            /*TODO: Usuń zawartość tabeli PRACOWNICY z bazy danych SQLite */
            deleteResult = -1

            val values = ContentValues()
            for (i in 0..13) {
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_PRAC, idPracTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_NAZWISKO, nazwiskoTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_ETAT, etatTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_SZEFA, idSzefaTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_SZEFA, zatrudnionyTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_PLACA_POD, placaPodTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_PLACA_DOD, placaDodTab[i])
                values.put(PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_ZESP, idZespTab[i])

                /*TODO: Umieść instrukcję wstawiającą do tabeli PRACOWNICY kolejne wiersze na podstawie zmiennej values
                * mapującej określone wartości do określonych kolumn */
            }
            /*TODO: Umieść instrukcję zamykającą bazę danych SQLite dostępną za pomocą zmiennej db */
        }

        findViewById<Button>(R.id.refreshButton).setOnClickListener { v ->

            val pracownicyProjection = arrayOf(PracZespEtatContract.Pracownicy._ID, PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_PRAC, PracZespEtatContract.Pracownicy.COLUMN_NAME_NAZWISKO, PracZespEtatContract.Pracownicy.COLUMN_NAME_ETAT, PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_ZESP, PracZespEtatContract.Pracownicy.COLUMN_NAME_PLACA_POD)

            val pracownicyOrderBy = PracZespEtatContract.Pracownicy.COLUMN_NAME_NAZWISKO + " ASC"

            val pracownicyColumn = arrayOf(PracZespEtatContract.Pracownicy.COLUMN_NAME_NAZWISKO, PracZespEtatContract.Pracownicy.COLUMN_NAME_ETAT)

            val pracownicyListItems = intArrayOf()// R.id.nazwiskoTextView,
            // R.id.etatTextView


            val db = mDbHelper!!.readableDatabase

            val pracownicyCursor = db.query(
                    PracZespEtatContract.Pracownicy.TABLE_NAME,
                    pracownicyProjection, null, null, null, null, // filtr na grupach
                    pracownicyOrderBy
            )// selekcja
            // argumenty selekcji
            // grupowanie

            val pracownicyCursorAdapter = SimpleCursorAdapter(
                    applicationContext,
                    R.layout.my_pracownik_row,
                    pracownicyCursor,
                    pracownicyColumn,
                    pracownicyListItems,
                    0)

            findViewById<GridView>(R.id.pracownicyGridView).adapter = pracownicyCursorAdapter

        }

        findViewById<Button>(R.id.getCountButton).setOnClickListener { view ->
            /*TODO: Popraw poniższe dwie instrukcje tak, aby poznać liczbę rekordów w tabeli PRACOWNICY
            * Uzyskaj te informacje za pomocą uzupełnionego dostawcy usług PracZespEtatContentProvider */
            val projection: Array<String> = arrayOf()

            countCursor = contentResolver.query(
                    null!!,
                    projection, null, null, null)

            Toast.makeText(
                    applicationContext,
                    "Znaleziono " + countCursor!!.count.toString() + " rek.",
                    Toast.LENGTH_SHORT).show()
        }
    }
}
