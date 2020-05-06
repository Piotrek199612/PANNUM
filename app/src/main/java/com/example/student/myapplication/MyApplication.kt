package com.example.student.myapplication

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        TODO: odkomentuj kod tworzący bazę dane po wykonaniu TODO w klasie AppDatabase
        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "songs.db").build()
    }

    companion object {
        lateinit var appDatabase: AppDatabase
    }

    var songPlayed = false
    lateinit var currentSong:SongEntity
    var markPoint = 0
}