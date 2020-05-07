package com.example.student.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.played_song_fragment.*
import java.io.FileInputStream

class PlayedSongFragment : Fragment() {

    private lateinit var currentSong: SongEntity
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mMyAnimator: MyAnimator
    private var state = 0
    private lateinit var application: MyApplication

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.played_song_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        application = (context?.applicationContext as MyApplication)
        val markPoint = application.markPoint
        currentSong = application.currentSong

        //Enable play song menu item
        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = false

        titleText.text = currentSong.title
        artistText.text = currentSong.artist


        val singleNotes = currentSong.notes.split(';')
        val notes = arrayListOf<List<Int>>()
        singleNotes.forEach { notes.add(it.split(',').map { it.toInt() }) }
        notesView.setNotes(notes)


        val tacts = currentSong.tacts.split(';').map { it.toInt() }.toTypedArray()
        notesView.setTacts(tacts)

        mMediaPlayer = createMediaPlayer()
        notesView.setLength(mMediaPlayer.duration)
        mMyAnimator = MyAnimator(notesView, notesView.totalLength, mMediaPlayer.duration.toLong(), {
            playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
            state = 0
        })

        //Restore state
        notesView.setMarkPosition(markPoint)
        mMyAnimator.setAnimationTime(markPoint.toLong())
        mMediaPlayer.seekTo(mMyAnimator.getCurrentTime().toInt())

        notesView.clickAction = {event ->
            notesView.invalidate()
            if (state == 0) {
                val newPosition = if ( event.x - notesView.start < 0) 0f else event.x - notesView.start
                mMyAnimator.setAnimationTime(newPosition.toLong())
                mMediaPlayer.seekTo((newPosition/notesView.space).toInt())
            }
        }

        playPauseButton.setOnClickListener{
            if (state == 0){
                playPauseButton.setImageResource(R.mipmap.ic_pause_foreground)
                mMyAnimator.startAnimation()
                state = 1
                mMediaPlayer.start()
            }
            else if (state == 1) {
                playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
                mMyAnimator.pauseAnimation()
                state = 0
                mMediaPlayer.pause()
            }
        }

        horizontalScroll.post{
            horizontalScroll.scrollX = markPoint
        }
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
        mMyAnimator.pauseAnimation()
        state = 0

        playPauseButton.setImageResource(R.mipmap.ic_play_foreground)

        application.markPoint =  notesView?.getMarkPosition()!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = true
    }

    private fun createMediaPlayer():MediaPlayer{
        val songId = resources.getIdentifier(currentSong.songResourceName, "raw", context?.packageName)
        var mediaPlayer:MediaPlayer? = null
        if (songId != 0)
            mediaPlayer = MediaPlayer.create(context, songId)
        else{
            val appData = context?.packageManager?.getPackageInfo(context!!.packageName, 0)?.applicationInfo?.dataDir.toString()
            val fos = FileInputStream("$appData/files/" + currentSong.songResourceName)
            mediaPlayer = MediaPlayer()

            mediaPlayer.setDataSource(fos.fd)
            mediaPlayer.prepare()
        }
        return mediaPlayer!!
    }
}