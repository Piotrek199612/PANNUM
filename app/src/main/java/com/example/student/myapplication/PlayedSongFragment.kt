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

class PlayedSongFragment : Fragment() {

    companion object {

        var notes = arrayOf(listOf(0,0,0))
        var title = ""
        var artist = ""
        var markPoint = 0
        var songResourceName = ""
        var coverResourceName = ""
        var stringNotes = ""
        var stringTacts = ""
        var tacts = emptyArray<Int>()


    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = false

        val view = inflater.inflate(R.layout.played_song_fragment, container, false)
        val playPauseButton = view!!.findViewById<ImageButton>(R.id.playPauseButton)
        val myView = view.findViewById<NotesView>(R.id.myView)


        if (arguments != null) {
            title = arguments!!.get("title").toString()
            artist = arguments!!.get("artist").toString()
            songResourceName = arguments!!.get("songResourceName").toString()
            coverResourceName = arguments!!.get("coverResourceName").toString()
            stringNotes = arguments!!.get("notes").toString()
            stringTacts = arguments!!.getString("tacts").toString()

        }
        view.findViewById<TextView>(R.id.titleText).text = title
        view.findViewById<TextView>(R.id.artistText).text = artist



        val singleNotes = stringNotes.split(';')

        val a = arrayListOf<List<Int>>()
        singleNotes.forEach { a.add(it.split(',').map { it.toInt() } ) }
        notes = arrayOf(listOf(0,0,0))
        notes = a.toArray(notes)

        myView.setNotes(notes)

        tacts = stringTacts.split(';').map { it.toInt() }.toTypedArray()
        myView.setTacts(tacts)



        val scrollView = view!!.findViewById<HorizontalScrollView>(R.id.horizontalScroll)
        var state = 0


        val songId = resources.getIdentifier(songResourceName, "raw", context?.packageName)
        mMediaPlayer = MediaPlayer.create(context, songId)

        myView.setLength(mMediaPlayer.duration)

        val animation = MyAnimator(myView, myView.totalLength, mMediaPlayer.duration.toLong(), {
            playPauseButton.setImageResource(R.mipmap.ic_pause_foreground)
            state = 0
        })


        //SAVED STATE
        myView.setMarkPosition(markPoint)
        animation.setAnimationTime(markPoint.toLong())
        mMediaPlayer.seekTo(animation.getCurrentTime().toInt())



        myView.isClickable = true
        class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(event: MotionEvent): Boolean {
                myView.invalidate()
                if (state == 0) {
                    val newPosition = if ( event.x - myView.start < 0) 0f else event.x - myView.start
                    myView.setMarkPosition(newPosition.toInt())
                    animation.setAnimationTime(newPosition.toLong())
                    mMediaPlayer.seekTo(animation.getCurrentTime().toInt())
                }
                return true
            }}

        val mDetector = GestureDetectorCompat(context, MyGestureListener())
        myView.setOnTouchListener { v, event ->
            mDetector.onTouchEvent(event)
        }





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->  myView.invalidate()}
        }

        var isMediaPlayerPrepared = false
        mMediaPlayer.setOnPreparedListener {
            isMediaPlayerPrepared = true
        }

        playPauseButton.setOnClickListener{
            if (state == 0){
                if (isMediaPlayerPrepared){
                    playPauseButton.setImageResource(R.mipmap.ic_pause_foreground)
                    animation.startAnimation()
                    state = 1
                    mMediaPlayer.start()
                }

            }
            else if (state == 1) {
                playPauseButton.setImageResource(R.mipmap.ic_play_foreground)
                animation.pauseAnimation()
                state = 0
                mMediaPlayer.pause()
            }
        }


        return view
    }
    private lateinit var mMediaPlayer: MediaPlayer
    override fun onStop() {
        super.onStop()
        mMediaPlayer.stop()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = true
        //val myView = view!!.findViewById<NotesView>(R.id.myView)
        //markPoint = myView.getMarkPosition()
    }
}