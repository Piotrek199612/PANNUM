package pl.pp.simplegame.game

class TempSprite(val x: Float, val y: Float) {
    private var life = 15

    fun update() {
        --life
    }

    fun canBeRemoved(): Boolean {
        return life < 0
    }
}