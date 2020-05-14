package com.example.student.myapplication

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.played_song_fragment.*
import java.io.FileInputStream

class PlayedSongFragment : Fragment(), AudioManager.OnAudioFocusChangeListener {

    private lateinit var currentSong: SongEntity
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mMyAnimator: MyAnimator
    private var state = 0

    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(MySongsViewModel::class.java)}

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.played_song_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val markPoint = viewModel.markPoint
        currentSong = viewModel.currentSong!!

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
                val audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                    playPauseButton.setImageResource(R.mipmap.ic_pause_foreground)
                    mMyAnimator.startAnimation()
                    state = 1
                    mMediaPlayer.start()
                }
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

        viewModel.markPoint =  notesView?.getMarkPosition()!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.played_song_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.arcade_menu_item-> {
                findNavController().navigate(R.id.nav_arcade)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when(focusChange){
            AudioManager.AUDIOFOCUS_LOSS -> {
                mMediaPlayer.pause()
                mMyAnimator.pauseAnimation()
                state = 0
                playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
            }
        }
    }

    private fun createMediaPlayer():MediaPlayer{
        val mediaPlayer = MediaPlayer()
        val appData = context?.packageManager?.getPackageInfo(context!!.packageName, 0)?.applicationInfo?.dataDir.toString()
        val fos = FileInputStream("$appData/files/" + currentSong.songResourceName)

        mediaPlayer.setDataSource(fos.fd)
        mediaPlayer.prepare()
        return mediaPlayer
    }
}