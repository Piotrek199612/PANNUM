package com.example.student.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.StringBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import kotlinx.android.synthetic.main.add_song_fragment.*

class AddSongFragment : Fragment() {

    companion object {
        private const val PICK_NOTES_RESULT_CODE = 1
        private const val PICK_MUSIC_RESULT_CODE = 2
        private const val PICK_COVER_RESULT_CODE = 3
        private const val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 4

        private const val NOTES_FILE_URI_SAVE_STATE_KEY = "notesFileUri"
        private const val MUSIC_FILE_URI_SAVE_STATE_KEY = "musicFileUri"
        private const val COVER_FILE_URI_SAVE_STATE_KEY = "coverFileUri"
    }

    private var notesFileUri : Uri? = null
    private var musicFileUri : Uri? = null
    private var coverFileUri : Uri? = null

    private val viewModel by lazy { ViewModelProviders.of(this).get(MySongsViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null){
            notesFileUri = savedInstanceState.getParcelable(NOTES_FILE_URI_SAVE_STATE_KEY)
            musicFileUri = savedInstanceState.getParcelable(MUSIC_FILE_URI_SAVE_STATE_KEY)
            coverFileUri = savedInstanceState.getParcelable(COVER_FILE_URI_SAVE_STATE_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_song_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_READ_EXTERNAL_STORAGE)
        }

        buttonXml.setOnClickListener { createGetContentIntent("text/xml", PICK_NOTES_RESULT_CODE) }
        buttonCover.setOnClickListener { createGetContentIntent("image/*", PICK_COVER_RESULT_CODE) }
        buttonMusic.setOnClickListener { createGetContentIntent("audio/mpeg", PICK_MUSIC_RESULT_CODE) }


        buttonLoad.setOnClickListener {
            if (notesFileUri != null && coverFileUri != null && musicFileUri != null){
                val file = context?.contentResolver?.openFileDescriptor(notesFileUri!!, "r")
                val notesXml = FileInputStream(file!!.fileDescriptor)
                val song = parseSongXml(notesXml)
                viewModel.addSong(song)

                copyFile(musicFileUri!!, song.songResourceName)
                copyFile(coverFileUri!!, song.coverResourceName)

                val toast = Toast.makeText(context, getString(R.string.song_add_success), Toast.LENGTH_SHORT)
                toast.show()
                notesFileUri = null
                coverFileUri = null
                musicFileUri = null
                xmlText.text = resources.getString(R.string.not_chosen)
                coverText.text = resources.getString(R.string.not_chosen)
                musicText.text = resources.getString(R.string.not_chosen)
                buttonLoad.isEnabled = false
            }
        }

        if (notesFileUri != null) xmlText.text = getFileName(notesFileUri!!)
        if (coverFileUri != null) coverText.text = getFileName(coverFileUri!!)
        if (musicFileUri != null) musicText.text = getFileName(musicFileUri!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data?.data != null) {
            when (requestCode) {
                PICK_NOTES_RESULT_CODE -> {
                    notesFileUri = data.data!!
                    xmlText.text = getFileName(notesFileUri!!)
                }
                PICK_MUSIC_RESULT_CODE -> {
                    musicFileUri = data.data!!
                    musicText.text = getFileName(musicFileUri!!)
                }
                PICK_COVER_RESULT_CODE -> {
                    coverFileUri = data.data!!
                    coverText.text = getFileName(coverFileUri!!)
                }
            }
        }
        if (notesFileUri != null && musicFileUri != null && coverFileUri != null){
            buttonLoad?.isEnabled = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(NOTES_FILE_URI_SAVE_STATE_KEY, notesFileUri)
        outState.putParcelable(MUSIC_FILE_URI_SAVE_STATE_KEY, musicFileUri)
        outState.putParcelable(COVER_FILE_URI_SAVE_STATE_KEY, coverFileUri)
    }

    private fun createGetContentIntent(mimeType:String, resultCode:Int){
        val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
        chooseIntent.type = mimeType
        Intent.createChooser(chooseIntent, getString(R.string.choose_file_intent))
        startActivityForResult(chooseIntent, resultCode)
    }

    private fun copyFile(fileUri:Uri, newFileName:String){
        val file = context?.contentResolver?.openFileDescriptor(fileUri, "r")
        val fileMusic = FileInputStream(file?.fileDescriptor)
        val outFile = File(context?.filesDir, newFileName)
        fileMusic.copyTo(outFile.outputStream())
    }

    private fun getFileName(uri:Uri):String{
        val splittedPath = uri.path?.split('/')
        return splittedPath!![splittedPath.size - 1]
    }

    private fun parseSongXml(fileContent :InputStream) :SongEntity{
        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(fileContent)


        val title = doc.getElementsByTagName("title").item(0).firstChild.nodeValue
        val artist = doc.getElementsByTagName("artistName").item(0).firstChild.nodeValue
        val year = doc.getElementsByTagName("albumYear").item(0).firstChild.nodeValue.toInt()
        val songResourceName = title.replace("'", "").replace(" ", "").toLowerCase()
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