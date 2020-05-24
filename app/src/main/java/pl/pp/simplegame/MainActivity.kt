package pl.pp.simplegame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import pl.pp.simplegame.game.GameState
import pl.pp.simplegame.game.GameStateAdapter

class MainActivity : AppCompatActivity(), GameView.GameControllerInterface {

    private lateinit var gameView: GameView
    private lateinit var picture: Bitmap
    private lateinit var gameStateAdaper: GameStateAdapter
    private val gameState = GameState()

    private var loopingThread: LoopingThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        gameView.addCallback(this)
        setContentView(gameView)

        picture = BitmapFactory.decodeResource(resources, R.drawable.good1)
        gameStateAdaper = GameStateAdapter(resources)
        gameState.loadSoundPool(this)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        loopingThread?.stopLoop()
    }

    @Synchronized
    override fun surfaceCreated(holder: SurfaceHolder?) {
        gameState.initGame(resources, gameView.width, gameView.height)
        loopingThread = LoopingThread(this::loop)
        loopingThread?.startLoop()
    }

    @Synchronized
    private fun loop() {
        gameState.update(gameView.width, gameView.height)
        gameView.drawInHolder {
            it.drawColor(Color.BLACK)
            gameStateAdaper.draw(it, gameState)
        }
    }

    @Synchronized
    override fun clickHere(x: Float, y: Float) {
        gameState.moveSprite(x,y, gameView.width, gameView.height)
    }

    @Synchronized
    override fun throwStar(x: Float, y: Float) {
        gameState.throwStar()
    }
}
