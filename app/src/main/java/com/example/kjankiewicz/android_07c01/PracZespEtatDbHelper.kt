package com.example.kjankiewicz.android_07c01

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PracZespEtatDbHelper : SQLiteOpenHelper {

    internal constructor(context: Context?) : super(context, DATABASE_NAME, null, DATABASE_VERSION)

    constructor(context: Context?, dbName: String, dbVersion: Int) : super(context, dbName, null, dbVersion)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ZESPOLY)
        db.execSQL(SQL_CREATE_ETATY)
        db.execSQL(SQL_CREATE_PRACOWNICY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_PRACOWNICY)
        db.execSQL(SQL_DELETE_ETATY)
        db.execSQL(SQL_DELETE_ZESPOLY)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {

        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val REAL_TYPE = " REAL"
        private const val COMMA_SEP = ","
        private val SQL_CREATE_ZESPOLY = "CREATE TABLE " + PracZespEtatContract.Zespoly.TABLE_NAME + " (" +
                PracZespEtatContract.Zespoly._ID + " INTEGER PRIMARY KEY," +
                PracZespEtatContract.Zespoly.COLUMN_NAME_ID_ZESP + INTEGER_TYPE + COMMA_SEP +
                PracZespEtatContract.Zespoly.COLUMN_NAME_NAZWA + TEXT_TYPE + COMMA_SEP +
                PracZespEtatContract.Zespoly.COLUMN_NAME_ADRES + TEXT_TYPE +
                " )"

        private val SQL_CREATE_ETATY = "CREATE TABLE " + PracZespEtatContract.Etaty.TABLE_NAME + " (" +
                PracZespEtatContract.Etaty._ID + " INTEGER PRIMARY KEY," +
                PracZespEtatContract.Etaty.COLUMN_NAME_NAZWA + TEXT_TYPE + COMMA_SEP +
                PracZespEtatContract.Etaty.COLUMN_NAME_PLACA_MIN + REAL_TYPE + COMMA_SEP +
                PracZespEtatContract.Etaty.COLUMN_NAME_PLACA_MAX + REAL_TYPE +
                " )"

        private val SQL_CREATE_PRACOWNICY = "CREATE TABLE " + PracZespEtatContract.Pracownicy.TABLE_NAME + " (" +
                PracZespEtatContract.Pracownicy._ID + " INTEGER PRIMARY KEY," +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_PRAC + TEXT_TYPE + COMMA_SEP +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_NAZWISKO + TEXT_TYPE + COMMA_SEP +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_ETAT + TEXT_TYPE + COMMA_SEP +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_PLACA_POD + REAL_TYPE + COMMA_SEP +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_PLACA_DOD + REAL_TYPE + COMMA_SEP +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_SZEFA + INTEGER_TYPE + COMMA_SEP +
                PracZespEtatContract.Pracownicy.COLUMN_NAME_ID_ZESP + INTEGER_TYPE +
                " )"

        private const val SQL_DELETE_ZESPOLY = "DROP TABLE IF EXISTS " + PracZespEtatContract.Zespoly.TABLE_NAME
        private const val SQL_DELETE_ETATY = "DROP TABLE IF EXISTS " + PracZespEtatContract.Etaty.TABLE_NAME
        private const val SQL_DELETE_PRACOWNICY = "DROP TABLE IF EXISTS " + PracZespEtatContract.Pracownicy.TABLE_NAME
        internal const val DATABASE_VERSION = 1
        internal const val DATABASE_NAME = "PracZespEtat.db"
    }
}

