package com.example.student.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MySongsViewModel: ViewModel() {

    private val songDao by lazy { MyApplication.appDatabase.songDao() }

    fun addDefaultData() {
        val idSongTab = arrayOf(100, 110, 120, 130, 140, 150, 160, 170, 190, 180, 200, 210, 220, 230)
        val artistTab = arrayOf("Muse", "The White stripes", "Nothing but thieves", "BRZEZINSKI", "MORZY", "KROLIKOWSKI", "KOSZLAJDA", "JEZIERSKI", "MATYSIAK", "MAREK", "ZAKRZEWICZ", "BIALY", "KONOPKA", "HAPKE")
        val titleTab = arrayOf("Muscle museum", "Seven nation army", "Trip switch", "PROFESOR", "PROFESOR", "ADIUNKT", "ADIUNKT", "ASYSTENT", "ASYSTENT", "SEKRETARKA", "STAZYSTA", "STAZYSTA", "ASYSTENT", "ASYSTENT")
        val yearTab = arrayOf(1730, 1350, 1070, 960, 830, 645, 590, 439,371, 410, 208, 250, 480, 480)
        val playedTab = arrayOf(1730, 1350, 1070, 960, 830, 645, 590, 439,371, 410, 208, 250, 480, 480)
        val notesTab = arrayOf("WEGLARZ", "BLAZEWICZ", "SLOWINSKI", "BRZEZINSKI", "MORZY", "KROLIKOWSKI", "KOSZLAJDA", "JEZIERSKI", "MATYSIAK", "MAREK", "ZAKRZEWICZ", "BIALY", "KONOPKA", "HAPKE")
        val songResourceName = arrayOf("WEGLARZ", "BLAZEWICZ", "SLOWINSKI", "BRZEZINSKI", "MORZY", "KROLIKOWSKI", "KOSZLAJDA", "JEZIERSKI", "MATYSIAK", "MAREK", "ZAKRZEWICZ", "BIALY", "KONOPKA", "HAPKE")
        val coverResourceName = arrayOf("sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy", "sevennationarmy")

        val defaultData = mutableListOf<SongEntity>()
        for (i in idSongTab.indices) {
            defaultData.add(SongEntity(idSongTab[i],artistTab[i],titleTab[i],yearTab[i], playedTab[i], notesTab[i], songResourceName[i], coverResourceName[i]))
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            songDao.insert(defaultData)
            }
        }
//        TODO: uwórz korutynę wstawiająca domyślne dane do bazy danych, skorzystaj z obiekty viewModelScope.launch{ }
    }

    fun getAllSongs(): LiveData<List<SongEntity>> =
        songDao.getAll()

    fun deteleAllSongs() {
//        TODO: utwórz korytunę usuwającą dane
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                songDao.removeAll()
            }
        }
    }
}