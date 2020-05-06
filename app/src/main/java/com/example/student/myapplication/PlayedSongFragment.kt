package com.example.student.myapplication

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import java.io.FileInputStream

class PlayedSongFragment : Fragment() {

    private lateinit var currentSong: SongEntity
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mMyAnimator: MyAnimator
    private var state = 0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val markPoint = (context?.applicationContext as MyApplication).markPoint
        currentSong = (context?.applicationContext as MyApplication).currentSong


        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = false

        val view = inflater.inflate(R.layout.played_song_fragment, container, false)
        val playPauseButton = view!!.findViewById<ImageButton>(R.id.playPauseButton)
        val myView = view.findViewById<NotesView>(R.id.myView)

        view.findViewById<TextView>(R.id.titleText).text = currentSong.title
        view.findViewById<TextView>(R.id.artistText).text = currentSong.artist


        val singleNotes = currentSong.notes.split(';')
        val a = arrayListOf<List<Int>>()
        singleNotes.forEach { a.add(it.split(',').map { it.toInt() }) }
        var notes = arrayOf(listOf(0, 0, 0))
        notes = a.toArray(notes)
        myView.setNotes(notes)



        val tacts = currentSong.tacts.split(';').map { it.toInt() }.toTypedArray()
        myView.setTacts(tacts)

        mMediaPlayer = createMediaPlayer()
        myView.setLength(mMediaPlayer.duration)

        mMyAnimator = MyAnimator(myView, myView.totalLength, mMediaPlayer.duration.toLong(), {
            playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
            state = 0
        })

        //SAVED STATE
        myView.setMarkPosition(markPoint)
        mMyAnimator.setAnimationTime(markPoint.toLong())
        mMediaPlayer.seekTo(mMyAnimator.getCurrentTime().toInt())



        myView.isClickable = true
        class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(event: MotionEvent): Boolean {
                myView.invalidate()
                if (state == 0) {
                    val newPosition = if ( event.x - myView.start < 0) 0f else event.x - myView.start
                    mMyAnimator.setAnimationTime(newPosition.toLong())
                    mMediaPlayer.seekTo((newPosition/myView.space).toInt())
                }
                return true
            }}

        val mDetector = GestureDetectorCompat(context, MyGestureListener())
        myView.setOnTouchListener { v, event ->
            mDetector.onTouchEvent(event)
        }


        val scrollView = view.findViewById<HorizontalScrollView>(R.id.horizontalScroll)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener { _, _, _, _, _ -> myView.invalidate() }
        }

        var isMediaPlayerPrepared = false
        mMediaPlayer.setOnPreparedListener {
            isMediaPlayerPrepared = true
        }

        playPauseButton.setOnClickListener{
            if (state == 0){
                if (isMediaPlayerPrepared){
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

        scrollView.post{
            scrollView.scrollX = markPoint
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
        mMyAnimator.pauseAnimation()
        state = 0

        val playPauseButton = view!!.findViewById<ImageButton>(R.id.playPauseButton)
        playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
        
        val myView = view?.findViewById<NotesView>(R.id.myView)
        (context?.applicationContext as MyApplication).markPoint =  myView?.getMarkPosition()!!
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