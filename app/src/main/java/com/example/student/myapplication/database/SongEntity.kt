package com.example.student.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val artist: String,
    val title: String,
    val year: Int,
    @ColumnInfo(defaultValue = "0") val played: Int = 0,
    val notes: String,
    val tacts: String,
    val songResourceName: String,
    val coverResourceName: String
)