package pl.pp.simplegame

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context) {

    private var gameControllerCallback: GameControllerInterface? = null

    fun addCallback(callback: GameControllerInterface?) {
        gameControllerCallback = callback
        holder.addCallback(callback)
    }

    fun drawInHolder(call: (Canvas) -> Unit) {
        var c: Canvas? = null
        try {
            c = holder.lockCanvas()
            call(c)
        } finally {
            c?.let { holder.unlockCanvasAndPost(it) }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP -> {
                val eventDuration = event.eventTime - event.downTime
                if (eventDuration < 500){
                    gameControllerCallback?.throwStar(x, y)
                }
                else {
                    gameControllerCallback?.clickHere(event.x, event.y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                gameControllerCallback?.clickHere(event.x, event.y)
            }
        }
        return true
    }

    interface GameControllerInterface : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}
        fun clickHere(x: Float, y: Float)
        fun throwStar(x: Float, y: Float)
    }
}