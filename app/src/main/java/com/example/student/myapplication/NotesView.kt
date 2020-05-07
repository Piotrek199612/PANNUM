package com.example.student.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.played_song_fragment.*

class NotesView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var markPosition = 0
    val space = 0.6f

    private val paint = Paint()
    private val linePaint = Paint()
    private val markPaint = Paint()
    private val tactPaint = Paint()
    private val textPaint = Paint()
    private val tactNumberPaint = Paint()

    private lateinit var notes: ArrayList<List<Int>>
    private lateinit var tacts: Array<Int>

    var clickAction: (event: MotionEvent) -> Unit = {}

    var totalLength = 0

    val start = 500//((parent as HorizontalScrollView).width *0.3 ).toInt()

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NotesView,
            0, 0)
        markPosition = typedArray.getInt(R.styleable.NotesView_markPosition, 0)
        typedArray.recycle()
        initPaints()
        isClickable = true

        class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(event: MotionEvent): Boolean {
                clickAction(event)
                return true
            }
        }

        val mDetector = GestureDetectorCompat(context, MyGestureListener())
        setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
        }
    }

    fun setMarkPosition(value:Int)
    {
        val horizontalScroll = parent as HorizontalScrollView
        if (markPosition != value) {
            horizontalScroll.scrollX = value
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
        drawBackground(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val topBottomMargin = 50
        val spaceBetweenLines = (height - 2*topBottomMargin) / 3.0f

        for (i in 0..3){
            canvas.drawLine(0f, topBottomMargin + i*spaceBetweenLines, width.toFloat(), topBottomMargin + i*spaceBetweenLines, linePaint)
        }

        drawNotes(canvas, topBottomMargin+ 20, spaceBetweenLines )


        tacts.forEach {
            canvas.drawLine((it * space + start) - 40, 0f ,(it * space + start) - 40, height.toFloat(), tactPaint)
        }

        canvas.drawLine((markPosition + start).toFloat() + 30, 0f ,(markPosition + start).toFloat()+ 30, height.toFloat(), markPaint)
    }

    private fun drawNotes(canvas: Canvas, marginTop:Int, spaceBetween: Float) {
        notes.forEach { note ->
            val string = note[0]
            val fret = note[1]
            val time = note[2]
            canvas.drawText(fret.toString(), (time * space + start), string*spaceBetween + marginTop, textPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = 500
        val displayMetrics = context.resources.displayMetrics
        setMeasuredDimension((start + totalLength + (displayMetrics.widthPixels - start)), height)
    }


    fun setNotes(notes: ArrayList<List<Int>>){
        this.notes = notes
    }

    fun setTacts(tacts: Array<Int>) {
        this.tacts = tacts
    }

    fun setLength(millis: Int){
        totalLength = (millis*space).toInt()
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

}