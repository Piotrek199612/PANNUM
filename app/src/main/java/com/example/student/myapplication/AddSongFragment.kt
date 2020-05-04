package com.example.student.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.add_song_fragment.*
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.StringBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class AddSongFragment : Fragment() {

    private val PICK_NOTES_RESULT_CODE = 1
    private val PICK_MUSIC_RESULT_CODE = 2
    private val PICK_COVER_RESULT_CODE = 3

    private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 4

    private var notesFileUri : Uri? = null
    private var musicFileUri : Uri? = null
    private var coverFileUri : Uri? = null

    private val viewModel by lazy { ViewModelProviders.of(this).get(MySongsViewModel::class.java)}

    companion object {

        fun newInstance(): AddSongFragment {
            return AddSongFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_song_fragment, container, false)

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_READ_EXTERNAL_STORAGE)

        }

        val buttonXml = view.findViewById<Button>(R.id.buttonXml)
        buttonXml.setOnClickListener {
            val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
            chooseIntent.type = "text/xml"
            Intent.createChooser(chooseIntent, "Choose file")
            startActivityForResult(chooseIntent, PICK_NOTES_RESULT_CODE)
        }

        val buttonCover = view.findViewById<Button>(R.id.buttonCover)
        buttonCover.setOnClickListener {
            val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
            chooseIntent.type = "image/*"
            Intent.createChooser(chooseIntent, "Choose file")
            startActivityForResult(chooseIntent, PICK_COVER_RESULT_CODE)
        }

        val buttonMusic = view.findViewById<Button>(R.id.buttonMusic)
        buttonMusic.setOnClickListener {
            val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
            chooseIntent.type = "audio/mpeg"
            Intent.createChooser(chooseIntent, "Choose file")
            startActivityForResult(chooseIntent, PICK_MUSIC_RESULT_CODE)
        }


        val buttonLoad = view.findViewById<Button>(R.id.buttonLoad)
        buttonLoad.setOnClickListener {
            if (notesFileUri != null && coverFileUri != null && musicFileUri != null){
                val file = context?.contentResolver?.openFileDescriptor(notesFileUri!!, "r")
                val notesXml = FileInputStream(file?.fileDescriptor)
                val song = parseSongXml(notesXml)
                viewModel.addSong(song)

                copyFile(musicFileUri!!, song.songResourceName)
                copyFile(coverFileUri!!, song.coverResourceName)

                val toast = Toast.makeText(context, "Song succesfully added", Toast.LENGTH_SHORT)
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

        return view
    }

    private fun copyFile(fileUri:Uri, newFileName:String){
        val filename = getFileName(fileUri)
        val file = context?.contentResolver?.openFileDescriptor(fileUri, "r")
        val fileMusic = FileInputStream(file?.fileDescriptor)
        val outFile = File(context?.filesDir, newFileName + "." + filename.split('.')[1])
        fileMusic.copyTo(outFile.outputStream())
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
            val buttonLoad = view?.findViewById<Button>(R.id.buttonLoad)
            buttonLoad?.isEnabled = true
        }
    }

    fun getFileName(uri:Uri):String{
        val splittedPath = uri.path?.split('/')
        return splittedPath!![splittedPath.size - 1]
    }
    fun parseSongXml(fileContent :InputStream) :SongEntity{
        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(fileContent)


        val title = doc.getElementsByTagName("title").item(0).firstChild.nodeValue
        val artist = doc.getElementsByTagName("artistName").item(0).firstChild.nodeValue
        val year = doc.getElementsByTagName("albumYear").item(0).firstChild.nodeValue.toInt()
        val songResourceName = title.replace(" ", "").toLowerCase()
        val album = doc.getElementsByTagName("albumName").item(0).firstChild.nodeValue
        val coverResourceName = album.replace(" ", "").toLowerCase()

        val xpath = XPathFactory.newInstance().newXPath()
        val expr = xpath.compile("//level[not(@difficulty < //level/@difficulty)]/notes/*")
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