package com.example.student.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val artist: String,
    val title: String,
    val year: Int,
    val played: Int,
    val notes: String,
    val songResourceName: String,
    val coverResourceName: String
)