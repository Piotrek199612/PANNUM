package com.example.student.myapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM songentity")
    fun getAll(): LiveData<List<SongEntity>>

    @Insert
    fun insert(songs: List<SongEntity>)

    @Query("DELETE FROM songentity")
    fun removeAll()

    @Delete
    fun removeSong(song: SongEntity)
}