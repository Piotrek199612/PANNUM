package com.example.student.myapplication.fragments

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.student.myapplication.MyAnimator
import com.example.student.myapplication.database.SongsViewModel
import com.example.student.myapplication.R
import com.example.student.myapplication.database.SongEntity
import kotlinx.android.synthetic.main.arcade_fragment.*
import java.io.FileInputStream

class ArcadeFragment : Fragment(), AudioManager.OnAudioFocusChangeListener  {

    private lateinit var currentSong: SongEntity
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mMyAnimator: MyAnimator
    private var state = 0
    private var lastNote = 0

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(SongsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.arcade_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val markPoint = viewModel.markPoint
        currentSong = viewModel.currentSong!!

        titleText.text = currentSong.title
        artistText.text = currentSong.artist

        val singleNotes = currentSong.notes.split(';')
        val notes = arrayListOf<List<Int>>()
        singleNotes.forEach { notes.add(it.split(',').map { it.toInt() }) }
        notesView.setNotes(notes)


        val tacts = currentSong.tacts.split(';').map { it.toInt() }.toTypedArray()
        notesView.setTacts(tacts)

        val backgrounds = BooleanArray(notes.size).toTypedArray()
        notesView.setBackgrounds(backgrounds)

        mMediaPlayer = createMediaPlayer()
        notesView.setLength(mMediaPlayer.duration)
        mMyAnimator = MyAnimator(
            notesView,
            notesView.totalLength,
            mMediaPlayer.duration.toLong(),
            {
                playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
                state = 0
            })

        notesView.setMarkPosition(markPoint)
        mMyAnimator.setAnimationTime(markPoint.toLong())
        mMediaPlayer.seekTo(mMyAnimator.getCurrentTime().toInt())

        notesView.clickAction = { event ->
            notesView.invalidate()
            if (state == 0) {
                val newPosition =
                    if (event.x - notesView.start < 0) 0f else event.x - notesView.start
                mMyAnimator.setAnimationTime(newPosition.toLong())
                mMediaPlayer.seekTo((newPosition / notesView.space).toInt())
                notes.forEachIndexed{i,note ->
                    val time = note[2]
                    if (time > newPosition / notesView.space)
                        backgrounds[i] = false
                }
                lastNote = 0
            }
        }

        horizontalScroll.post {
            horizontalScroll.scrollX = markPoint
        }

        playPauseButton.setOnClickListener {
            if (state == 0) {
                val audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                    playPauseButton.setImageResource(R.mipmap.ic_pause_foreground)
                    mMyAnimator.startAnimation()
                    state = 1
                    mMediaPlayer.start()
                }
            } else if (state == 1) {
                playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
                mMyAnimator.pauseAnimation()
                state = 0
                mMediaPlayer.pause()
            }
        }

        stringRed.setOnClickListener {
            processClick(0, notes, backgrounds)
        }

        stringYellow.setOnClickListener {
            processClick(1, notes, backgrounds)
        }

        stringBlue.setOnClickListener {
            processClick(2, notes, backgrounds)
        }

        stringOrange.setOnClickListener {
            processClick(3, notes, backgrounds)
        }
    }

    private fun processClick(
        string: Int,
        notes: ArrayList<List<Int>>,
        backgrounds: Array<Boolean>
    ) {
        val millis =
            (notesView.getMarkPosition().toFloat() / notesView.totalLength) * mMediaPlayer.duration
        val dt = 200
        for (i in lastNote until notes.size) {
            if (millis > notes[i][2] - dt && millis < notes[i][2] + dt && notes[i][0] == string) {
                lastNote = i + 1
                backgrounds[i] = true
                break
            } else if (millis < notes[i][2] - dt) {
                break
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
        mMyAnimator.pauseAnimation()
        state = 0

        playPauseButton.setImageResource(R.mipmap.ic_play_foreground)

        viewModel.markPoint = notesView?.getMarkPosition()!!
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (mMediaPlayer.isPlaying) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    mMediaPlayer.pause()
                    mMyAnimator.pauseAnimation()
                    state = 0
                    playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
                }
            }
        }
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mediaPlayer = MediaPlayer()
        val appData = context?.packageManager?.getPackageInfo(context!!.packageName, 0)
            ?.applicationInfo?.dataDir.toString()
        val fos = FileInputStream("$appData/files/" + currentSong.songResourceName)

        mediaPlayer.setDataSource(fos.fd)
        mediaPlayer.prepare()
        return mediaPlayer
    }
}
