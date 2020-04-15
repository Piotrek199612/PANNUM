package com.example.kjankiewicz.android_07c01

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class PracZespEtatContentProvider : ContentProvider() {

    private var mOpenHelper: PracZespEtatDbHelper? = null
    private lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
        mOpenHelper = PracZespEtatDbHelper(context)

        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        db = mOpenHelper!!.writableDatabase
        when (sUriMatcher.match(uri)) {
            ZESPOLY -> return db.delete(PracZespEtatContract.Zespoly.TABLE_NAME, selection, selectionArgs)
            ETATY -> return db.delete(PracZespEtatContract.Etaty.TABLE_NAME, selection, selectionArgs)
            /*TODO: dodaj obsługę tabeli PRACOWNICY dla metody delete */
            else -> throw IllegalArgumentException("Not yet implemented")
        }
    }

    override fun getType(uri: Uri): String? {

        when (sUriMatcher.match(uri)) {
            ZESPOLY -> return "vnd.android.cursor.dir/vnd.com.example.kjankiewicz.android_07c01.praczespetat" + PracZespEtatContract.Zespoly.TABLE_NAME
            ETATY -> return "vnd.android.cursor.dir/vnd.com.example.kjankiewicz.android_07c01.praczespetat." + PracZespEtatContract.Etaty.TABLE_NAME
            /*TODO: dodaj obsługę tabeli PRACOWNICY dla metody getType */
            else -> throw IllegalArgumentException("Not yet implemented")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val newRowId: Long
        db = mOpenHelper!!.writableDatabase
        return when (sUriMatcher.match(uri)) {
            ZESPOLY -> {
                newRowId = db.insert(PracZespEtatContract.Zespoly.TABLE_NAME, null, values)
                ContentUris.withAppendedId(uri, newRowId)
            }
            ETATY -> {
                newRowId = db.insert(PracZespEtatContract.Etaty.TABLE_NAME, null, values)
                ContentUris.withAppendedId(uri, newRowId)
            }
            /*TODO: dodaj obsługę tabeli PRACOWNICY dla metody insert */
            else -> throw IllegalArgumentException("Not yet implemented")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        db = mOpenHelper!!.readableDatabase
        return when (sUriMatcher.match(uri)) {
            ZESPOLY -> db.query(PracZespEtatContract.Zespoly.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder)
            ETATY -> db.query(PracZespEtatContract.Etaty.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder)
            /*TODO: dodaj obsługę tabeli PRACOWNICY dla metody query */
            else -> throw IllegalArgumentException("Not yet implemented")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {

        db = mOpenHelper!!.writableDatabase
        return when (sUriMatcher.match(uri)) {
            ZESPOLY -> db.update(PracZespEtatContract.Zespoly.TABLE_NAME, values, selection, selectionArgs)
            ETATY -> db.update(PracZespEtatContract.Etaty.TABLE_NAME, values, selection, selectionArgs)
            /*TODO: dodaj obsługę tabeli PRACOWNICY dla metody update */
            else -> throw IllegalArgumentException("Not yet implemented")
        }
    }

    companion object {

        private const val ZESPOLY = 1
        private const val ZESPOLY_ID = 2
        private const val ETATY = 3
        private const val ETATY_ID = 4
        private const val PRACOWNICY = 7
        private const val PRACOWNICY_ID = 8

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(PracZespEtatContract.AUTHORITY, "zespoly", ZESPOLY)
            sUriMatcher.addURI(PracZespEtatContract.AUTHORITY, "zespoly/#", ZESPOLY_ID)
            sUriMatcher.addURI(PracZespEtatContract.AUTHORITY, "etaty", ETATY)
            sUriMatcher.addURI(PracZespEtatContract.AUTHORITY, "etaty/#", ETATY_ID)
            sUriMatcher.addURI(PracZespEtatContract.AUTHORITY, "pracownicy", PRACOWNICY)
            sUriMatcher.addURI(PracZespEtatContract.AUTHORITY, "pracownicy/#", PRACOWNICY_ID)
        }
    }
}
