import com.example.student.myapplication.NotesView

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

class MyAnimator(animationTarget: NotesView, private val endValue :Int, private val totalMillis:Long, doOnEnd: () -> Unit = {}, propertyName : String = "markPosition") {

    private var animation : ObjectAnimator =
        ObjectAnimator.ofInt(animationTarget, propertyName,  0, endValue)

    init {
        animation.interpolator = LinearInterpolator()
        animation.duration = totalMillis
        animation.doOnEnd { doOnEnd() }
    }

    fun startAnimation(){
        if (animation.isPaused) {
            animation.resume()
            return
        }
        if (isFinished())
            animation.cancel()
        animation.start()
    }

    fun pauseAnimation(){
        if (animation.isRunning)
            animation.pause()
    }

    fun setAnimationTime(newValue: Long){
            val newTime = (newValue*1.0f/endValue) * totalMillis
            animation.cancel()
            animation.currentPlayTime = newTime.toLong()
    }

    fun getCurrentTime(): Long {
        return animation.currentPlayTime
    }

    private fun isFinished() : Boolean{
        return totalMillis == getCurrentTime()
    }

}