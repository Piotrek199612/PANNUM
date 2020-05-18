package com.example.student.myapplication

import android.app.Application
import androidx.room.Room
import com.example.student.myapplication.database.AppDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "songs.db").build()
    }

    companion object {
        lateinit var appDatabase: AppDatabase
    }
}