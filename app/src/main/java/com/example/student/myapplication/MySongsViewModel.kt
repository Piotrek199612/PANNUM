package com.example.student.myapplication

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.NodeList
import java.lang.StringBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
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

    private fun parseSongXML(id:Int, filePath:String, assets: AssetManager) :SongEntity{
        val istream = assets.open(filePath)
        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(istream)


        val title = doc.getElementsByTagName("title").item(0).firstChild.nodeValue
        val artist = doc.getElementsByTagName("artistName").item(0).firstChild.nodeValue
        val year = doc.getElementsByTagName("albumYear").item(0).firstChild.nodeValue.toInt()
        val songResourceName = title.replace(" ", "").toLowerCase()
        val album = doc.getElementsByTagName("albumName").item(0).firstChild.nodeValue
        val coverResourceName = album.replace(" ", "").toLowerCase()

        val xpath = XPathFactory.newInstance().newXPath()
        val expr = xpath.compile("//notes[not(@count < //notes/@count)]/*")
        val notes = expr.evaluate(doc, XPathConstants.NODESET) as NodeList

        val notesString = StringBuilder()
        for (i in 0 until notes.length){
            val time = (notes.item(i).attributes.getNamedItem("time").nodeValue.toFloat()*1000).toInt()
            val fret = notes.item(i).attributes.getNamedItem("fret").nodeValue
            val string = notes.item(i).attributes.getNamedItem("string").nodeValue

            notesString.append("$string,$fret,$time")
            if (i != notes.length-1)
                notesString.append(";")
        }

        val xpathTacts = XPathFactory.newInstance().newXPath()
        val exprTacts = xpathTacts.compile("//ebeats/*[@measure != '-1']")
        val tacts = exprTacts.evaluate(doc, XPathConstants.NODESET) as NodeList

        val tactsString = StringBuilder()
        for (i in 0 until tacts.length){
            val time = (tacts.item(i).attributes.getNamedItem("time").nodeValue.toFloat()*1000).toInt()
            tactsString.append("$time")
            if (i != tacts.length-1)
                tactsString.append(";")
        }

        return SongEntity(
            artist=artist,
            title=title,
            year=year,
            notes = notesString.toString(),
            tacts = tactsString.toString(),
            songResourceName = songResourceName,
            coverResourceName = coverResourceName)
    }
}