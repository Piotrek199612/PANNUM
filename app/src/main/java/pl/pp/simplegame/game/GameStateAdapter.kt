package pl.pp.simplegame.game

import android.content.res.Resources
import android.graphics.*
import pl.pp.simplegame.R

class GameStateAdapter(private val resources: Resources) {

    private val bmpBlood = BitmapFactory.decodeResource(resources, R.drawable.blood1)
    private val scale = resources.displayMetrics.density
    private val paint = Paint().apply {
        typeface = Typeface.create("Helvetica", Typeface.BOLD)
        color = Color.RED
        flags = Paint.ANTI_ALIAS_FLAG
        textAlign = Paint.Align.CENTER
        textSize = 40f * scale
    }

    fun draw(canvas: Canvas, gameState: GameState) {
        canvas.drawColor(Color.BLACK)

        for (temp in gameState.temps) {
            drawTempSprite(canvas, temp)
        }

        for (sprite in gameState.sprites) {
            drawSprite(canvas, sprite)
        }

        gameState.star?.let { drawStar(canvas, it) }

        if (gameState.isGameOver()) {
            val x = (canvas.width / 2).toFloat()
            val y =
                (canvas.height / 2).toFloat()
            if (gameState.endState == GameState.EndState.BAD_WINS) {
                canvas.drawText(resources.getString(R.string.bad_wins), x, y, paint)
            } else if (gameState.endState == GameState.EndState.GOOD_WINS) {
                canvas.drawText(resources.getString(R.string.good_wins), x, y, paint)
            }
        }
    }

    fun drawSprite(canvas: Canvas, sprite: Sprite) {
        val srcX = sprite.currentFrame * sprite.width
        val srcY = Sprite.DIRECTION_TO_ANIMATION_MAP[sprite.getDirection()] * sprite.height
        val src = Rect(srcX, srcY, srcX + sprite.width, srcY + sprite.height)
        val dst = Rect(
            sprite.x,
            sprite.y,
            sprite.x + sprite.width,
            sprite.y + sprite.height
        )
        canvas.drawBitmap(sprite.bmp, src, dst, null)
    }

    private fun drawTempSprite(canvas: Canvas, temp: TempSprite) {
        if (temp.canBeRemoved()) return
        canvas.drawBitmap(
            bmpBlood,
            temp.x - bmpBlood.width / 2,
            temp.y - bmpBlood.height / 2,
            null
        )
    }

    private fun drawStar(canvas: Canvas, star: Star) {
        canvas.drawBitmap(star.bmp, star.x.toFloat(), star.y.toFloat(), null)
    }
}