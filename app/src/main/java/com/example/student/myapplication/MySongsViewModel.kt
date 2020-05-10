package com.example.student.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MySongsViewModel: ViewModel() {

    private val songDao by lazy { MyApplication.appDatabase.songDao() }

    fun addSong(song: SongEntity){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                songDao.insert(listOf(song))
            }
        }
    }

    fun removeSong(song: SongEntity, appDataPath: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (songDao.canRemoveCover(song.coverResourceName) == 1) {
                    val coverFile = File(appDataPath + "/" + song.coverResourceName)
                    coverFile.delete()
                }
                if (songDao.canRemoveSong(song.songResourceName) == 1) {
                    val songFile = File(appDataPath + "/" + song.songResourceName)
                    songFile.delete()
                }
                songDao.removeSong(song)
            }
        }
    }

    fun getAllSongs(): LiveData<List<SongEntity>> =
        songDao.getAll()

}