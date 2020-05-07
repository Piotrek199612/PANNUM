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

    @Query("SELECT COUNT(*) FROM songentity WHERE coverResourceName == :coverResourceName")
    fun canRemoveCover(coverResourceName: String) : Int

    @Query("SELECT COUNT(*) FROM songentity WHERE songResourceName == :songResourceName")
    fun canRemoveSong(songResourceName: String) : Int
}