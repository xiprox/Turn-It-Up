package tr.xip.turnitup

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool

/**
 * Created by xip on 12/10/17.
 */
object Sounds {
    private val sp = SoundPool(10, AudioManager.STREAM_MUSIC, 0)

    var casualTick = 0
    var intenseTick = 1
    var timeOut = 2
    var alarm = 3

    fun init(context: Context) {
        casualTick = sp.load(context, R.raw.casual_tick, 1)
        intenseTick = sp.load(context, R.raw.intense_tick, 1)
        timeOut = sp.load(context, R.raw.casual_tick, 1)
        alarm = sp.load(context, R.raw.alarm, 1)
    }

    fun play(sound: Int) {
        sp.play(sound, 1f, 1f, 0, 0, 1f)
    }
}