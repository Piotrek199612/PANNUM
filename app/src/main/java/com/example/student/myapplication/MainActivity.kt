package com.example.student.myapplication

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.HorizontalScrollView
import android.view.GestureDetector
import android.widget.GridView
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room


class MainActivity : AppCompatActivity() {


    private lateinit var mMediaPlayer: MediaPlayer
    private val viewModel by lazy { ViewModelProviders.of(this).get(MySongsViewModel::class.java)}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        val myView = findViewById<NotesView>(R.id.myView)
        val scrollView = findViewById<HorizontalScrollView>(R.id.horizontalScroll)
        var state = 0

        var dataAdded = false


        //mMediaPlayer = MediaPlayer.create(this, R.raw.musclemuseumshort)
        mMediaPlayer = MediaPlayer.create(this, R.raw.sevennationarmy)
        mMediaPlayer.setOnPreparedListener{
            //mMediaPlayer.start()
            Log.w("STURMER", "Music plaued")
        }
        mMediaPlayer.currentPosition

        val animation = MyAnimator(myView, myView.totalLength, mMediaPlayer.duration.toLong(), {
            button.text = "START"
            state = 0
        })

        myView.setMediaPlayer(mMediaPlayer)
        myView.setMyAnimator(animation)





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

        val mDetector = GestureDetectorCompat(this, MyGestureListener())
        myView.setOnTouchListener { v, event ->
            mDetector.onTouchEvent(event)
        }





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->  myView.invalidate()}
        }

        var isMediaPlayerPrepared = false
        mMediaPlayer.setOnPreparedListener{
            isMediaPlayerPrepared = true
        }

        button.setOnClickListener {
            if (state == 0){
                if (isMediaPlayerPrepared){
                button.text = "STOP"
                animation.startAnimation()
                state = 1
                mMediaPlayer.start()
                }

            }
            else if (state == 1) {
                button.text = "START"
                animation.pauseAnimation()
                state = 0
                mMediaPlayer.pause()
            }

            if (dataAdded) {
                viewModel.addDefaultData()
                dataAdded = false
            }
            else{
                viewModel.deteleAllSongs()
                dataAdded = true
            }
        }



        val adapter = PracownikAdapter(baseContext)
        findViewById<GridView>(R.id.songsGridView).adapter = adapter
        viewModel.getAllSongs().observe(this, Observer<List<SongEntity>> {
            adapter.clear()
            adapter.addAll(it)
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.stop()
    }

}
