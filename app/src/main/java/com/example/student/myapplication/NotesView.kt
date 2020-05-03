package com.example.student.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView

class NotesView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var markPosition = 0
    private val space = 100//250

    private val paint = Paint()
    private val linePaint = Paint()
    private val markPaint = Paint()
    private val tactPaint = Paint()
    private val textPaint = Paint()
    private val tactNumberPaint = Paint()

    private lateinit var notes: Array<List<String>>
    private lateinit var tacts: Array<Int>

    private var numberOfNotes = 0//notes[0].size
    var totalLength = 0

    val start = 500//((parent as HorizontalScrollView).width *0.3 ).toInt()

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NotesView,
            0, 0)
        markPosition = typedArray.getInt(R.styleable.NotesView_markPosition, 0)
        typedArray.recycle()
        initPaints()
    }

    private lateinit  var mediaPlayer: MediaPlayer
    fun setMediaPlayer(player:MediaPlayer){
        mediaPlayer  = player
    }
    private lateinit  var myAnimator: MyAnimator
     fun setMyAnimator(animator:MyAnimator){
        myAnimator  = animator
    }

    fun setMarkPosition(value:Int)
    {
        val horizontalScroll = parent as HorizontalScrollView
        if (markPosition != value) {
            horizontalScroll.scrollX = value
            Log.w("MARK POSITION", horizontalScroll.scrollX.toString())
            Log.w("MARK POSITION Value", value.toString())
            horizontalScroll.invalidate()
            markPosition = value
            invalidate()
        }
    }

    fun getMarkPosition() : Int{
        return markPosition
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setMarkPosition(markPosition)
        drawBackground(canvas)
    }

    private fun initPaints()
    {
        paint.color = Color.MAGENTA
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        linePaint.color = Color.GRAY
        linePaint.alpha = 85
        linePaint.strokeWidth = 7f

        markPaint.color = Color.GREEN
        markPaint.alpha = 50
        markPaint.strokeWidth = 90f

        tactPaint.color = Color.GRAY
        tactPaint.alpha = 70
        tactPaint.strokeWidth = 7f

        tactNumberPaint.color = Color.DKGRAY
        tactNumberPaint.style = Paint.Style.FILL
        tactNumberPaint.textSize = 40f

        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 60f
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val topBottomMargin = 50
        val spaceBetweenLines = (height - 2*topBottomMargin) / 3.0f

        for (i in 0..3){
            canvas.drawLine(0f, topBottomMargin + i*spaceBetweenLines, width.toFloat(), topBottomMargin + i*spaceBetweenLines, linePaint)
            drawNotes(canvas, i, topBottomMargin + i*spaceBetweenLines + 20)
        }




        for ((tactNumber, i) in tacts.withIndex()){
            canvas.drawLine((start + i*space - 80).toFloat(), 0f ,(start + i*space - 80).toFloat(), height.toFloat(), tactPaint)
            canvas.drawText( (tactNumber+1).toString(), (start + i*space - 80).toFloat(), 50f , tactNumberPaint)
        }

        canvas.drawLine((markPosition + start).toFloat() + 30, 0f ,( markPosition + start).toFloat()+ 30, height.toFloat(), markPaint)
    }

    private fun drawNotes(canvas: Canvas, index:Int, y: Float) {
        val horizontalScroll = parent as HorizontalScrollView
        notes[index].forEachIndexed { i, note ->
            if ((i * space + start + 100) > horizontalScroll.scrollX && (i * space + start - 100) < horizontalScroll.scrollX +horizontalScroll.width ) {
                var value = note
                if (note == "-1") value = ""
                canvas.drawText(value, (i * space + start).toFloat(), y, textPaint)
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = 500
        val displayMetrics = context.resources.displayMetrics
        setMeasuredDimension((start + numberOfNotes * space + (displayMetrics.widthPixels - start)), height)
        setMarkPosition(markPosition)
    }


    fun setNotes(notes: Array<List<String>>){
        this.notes = notes
        this.numberOfNotes = notes[0].size
        this.totalLength = numberOfNotes * space
    }

    fun setTactc(tacts: Array<Int>){
        this.tacts = tacts
    }

}