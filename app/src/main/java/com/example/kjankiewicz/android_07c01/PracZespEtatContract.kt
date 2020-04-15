package com.example.kjankiewicz.android_07c01

import android.net.Uri

class PracZespEtatContract {

    // substitution of android.provider.BaseColumns
    // another reason for Room
    open class BaseColumns {
        val _ID = "_id"
    }

    abstract class Zespoly : BaseColumns() {
        companion object : BaseColumns() {
            val CONTENT_URI: Uri = Uri.withAppendedPath(AUTHORITY_URI, "zespoly")
            const val TABLE_NAME = "zespoly"
            const val COLUMN_NAME_ID_ZESP = "id_zesp"
            const val COLUMN_NAME_NAZWA = "nazwa"
            const val COLUMN_NAME_ADRES = "adres"
        }
    }

    abstract class Etaty : BaseColumns() {
        companion object : BaseColumns() {
            val CONTENT_URI: Uri = Uri.withAppendedPath(AUTHORITY_URI, "etaty")
            const val TABLE_NAME = "etaty"
            const val COLUMN_NAME_NAZWA = "nazwa"
            const val COLUMN_NAME_PLACA_MAX = "placa_min"
            const val COLUMN_NAME_PLACA_MIN = "placa_max"
        }
    }

    abstract class Pracownicy : BaseColumns() {
        companion object : BaseColumns() {
            val CONTENT_URI: Uri = Uri.withAppendedPath(AUTHORITY_URI, "pracownicy")
            const val TABLE_NAME = "pracownicy"
            const val COLUMN_NAME_ID_PRAC = "id_prac"
            const val COLUMN_NAME_NAZWISKO = "nazwisko"
            const val COLUMN_NAME_ETAT = "etat"
            const val COLUMN_NAME_PLACA_POD = "placa_pod"
            const val COLUMN_NAME_PLACA_DOD = "placa_dod"
            const val COLUMN_NAME_ID_SZEFA = "id_szefa"
            const val COLUMN_NAME_ID_ZESP = "id_zesp"
        }
    }

    companion object {
        const val AUTHORITY = "com.example.kjankiewicz.android_07c01.praczespetat"
        val AUTHORITY_URI: Uri = Uri.parse("content://$AUTHORITY")
    }

}
