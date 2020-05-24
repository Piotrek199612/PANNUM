package pl.pp.simplegame.game

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import pl.pp.simplegame.R
import java.util.*
import kotlin.random.nextInt




class GameState {

    enum class EndState {
        NO, GOOD_WINS, BAD_WINS
    }

    var endState = EndState.NO

    val sprites = mutableListOf<Sprite>()
    val temps = mutableListOf<TempSprite>()
    var star: Star? = null
    private var starBmp: Bitmap? = null
    private var soundPool: SoundPool? = null
    private val soundPoolStreamsNo = 3
    private var badDeathID = 0
    private var goodDeathID = 0
    private var starID = 0
    private var respawnID = 0
    private val soundVolume = 0.5f

    private val respawn = hashMapOf<Sprite, Long>()
    private val RESPAWN_MIN = 8000
    private val RESPAWN_MAX = 20000

    fun initGame(resources: Resources, maxWidth: Int, maxHeight: Int) {
        endState = EndState.NO
        sprites.clear()
        temps.clear()
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad1, false))
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad2, false))
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad3, false))
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad4, false))
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad5, false))
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.bad6, false))
        sprites.add(createSprite(resources, maxWidth, maxHeight, R.drawable.good1, true))
        starBmp = BitmapFactory.decodeResource(resources, R.drawable.star)

    }

    fun update(maxWidth: Int, maxHeight: Int) {
        detectCollision()

        if (isGameOver()) return
        for (sprite in sprites) {
            sprite.update(maxWidth, maxHeight)
        }

        for (i in temps.lastIndex downTo 0) {
            temps[i].update()
            if (temps[i].canBeRemoved())
                temps.removeAt(i)
        }

        star?.update(maxWidth, maxHeight)
        if (star?.visible == false)
            star = null


        val it = respawn.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.value < System.currentTimeMillis()) {
                soundPool?.play(respawnID,soundVolume, soundVolume, 1, 0, 1f)
                sprites.add(entry.key)
                it.remove()
            }
        }

        calcEnd()
    }

    private fun detectCollision() {
        val spritesToRemove = mutableSetOf<Sprite>()
        for (spriteA in sprites) {
            if (spriteA.good) {
                for (spriteB in sprites) {
                    if (!spriteB.good) {
                        if (spriteA.isCollision(spriteB)) {
                            spritesToRemove.add(spriteA)
                            spritesToRemove.add(spriteB)
                            soundPool?.play(goodDeathID,soundVolume, soundVolume, 1, 0, 1f)
                        }
                    }
                }
            }
        }

        if (star?.visible == true) {
            for (sprite in sprites) {
                if (!sprite.good) {
                    if (sprite.isCollision(star)) {
                        spritesToRemove.add(sprite)
                        soundPool?.play(badDeathID,soundVolume, soundVolume, 1, 0, 1f)
                        star = null
                        break
                    }
                }
            }
        }

        for (sprite in spritesToRemove) {
            sprites.remove(sprite)
            temps.add(TempSprite(sprite.x.toFloat(), sprite.y.toFloat()))
            respawn[sprite] = System.currentTimeMillis() + kotlin.random.Random.nextInt(RESPAWN_MIN..RESPAWN_MAX)
        }
    }

    private fun createSprite(
        resources: Resources,
        maxWidth: Int,
        maxHeight: Int,
        resourceId: Int,
        good: Boolean
    ): Sprite {
        val bmp = BitmapFactory.decodeResource(resources, resourceId)
        val rnd = Random()
        val x = rnd.nextInt(maxWidth - bmp.width)
        val y = rnd.nextInt(maxHeight - bmp.width)
        val xSpeed = rnd.nextInt(2 * Sprite.MAX_SPEED) - Sprite.MAX_SPEED
        val ySpeed = rnd.nextInt(2 * Sprite.MAX_SPEED) - Sprite.MAX_SPEED
        return Sprite(bmp, good, x, y, xSpeed, ySpeed)
    }

    fun moveSprite(x: Float, y: Float, width: Int, height: Int) {
        if (isGameOver()) return
        for (i in sprites.lastIndex downTo 0) {
            val sprite = sprites[i]
            if (sprite.good) {
                sprite.setSpeed(x, y, width.toFloat(), height.toFloat())
                break
            }
        }

        if (isGameOver()) return
    }

    private fun calcEnd() {
        val leftGood = sprites.filter { it.good }.size
        val leftBad = sprites.size - leftGood
        if (leftGood == 0) {
            endState = EndState.BAD_WINS
        } else if (leftBad == 0) {
            endState = EndState.GOOD_WINS
        }
    }

    fun isGameOver(): Boolean {
        return endState != EndState.NO
    }

    fun throwStar() {
        if (isGameOver()) return
        if (starBmp == null) return
        for (sprite in sprites) {
            if (sprite.good) {
                val x = sprite.x + sprite.width / 2 - starBmp!!.width / 2
                val y = sprite.y + sprite.height / 2 - starBmp!!.height / 2
                star = Star(starBmp!!, x, y, sprite.xSpeed * 3, sprite.ySpeed * 3)
                soundPool?.play(starID,soundVolume, soundVolume, 1, 0, 1f)
            }
        }
    }

    fun loadSoundPool(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool()
        } else {
            createOldSoundPool()
        }

        soundPool?.setOnLoadCompleteListener { soundPool, sampleId, status -> }

        badDeathID = soundPool!!.load(context, R.raw.bad_death, 1)
        goodDeathID = soundPool!!.load(context, R.raw.good_death, 1)
        starID = soundPool!!.load(context, R.raw.star, 1)
        respawnID = soundPool!!.load(context, R.raw.respawn, 1)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNewSoundPool() {
        val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        soundPool =
            SoundPool.Builder().setMaxStreams(soundPoolStreamsNo).setAudioAttributes(attributes)
                .build()
    }

    @SuppressWarnings("deprecation")
    private fun createOldSoundPool() {
        soundPool = SoundPool(soundPoolStreamsNo, AudioManager.STREAM_MUSIC, 0)
    }
}
