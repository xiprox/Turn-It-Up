package tr.xip.turnitup

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_words.*
import org.apache.commons.io.IOUtils
import java.util.*

class WordsActivity : FullscreenActivity() {
    private var difficulty = Difficulty.EASY
    private var selectedTimerDuration = TIMER_1_DURATION

    private var words: List<Word>? = null

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words)
        volumeControlStream = AudioManager.STREAM_MUSIC

        Sounds.init(this)

        next.setOnClickListener {
            if (timer == null) {
                startTimer()

                // Flip to the timer status
                flipper.displayedChild = FLIPPER_TIMER_STATUS

                // Change the icon to forward icon
                next.setImageResource(R.drawable.ic_arrow_forward_gray_24dp)

                // Load the first word
                loadWord()
            } else {
                loadWord()
            }
        }

        timerStop.setOnClickListener {
            resetTimer()

            timer?.cancel()
            timer = null

            // Flip to the timer selection
            flipper.displayedChild = FLIPPER_TIMER_SELECTION
        }

        timer1.setOnClickListener {
            selectedTimerDuration = TIMER_1_DURATION
            timer1.setTextColor(resources.getColor(R.color.colorAccentLight))
            timer2.setTextColor(resources.getColor(android.R.color.white))
            timer3.setTextColor(resources.getColor(android.R.color.white))
        }

        timer2.setOnClickListener {
            selectedTimerDuration = TIMER_2_DURATION
            timer1.setTextColor(resources.getColor(android.R.color.white))
            timer2.setTextColor(resources.getColor(R.color.colorAccentLight))
            timer3.setTextColor(resources.getColor(android.R.color.white))
        }

        timer3.setOnClickListener {
            selectedTimerDuration = TIMER_3_DURATION
            timer1.setTextColor(resources.getColor(android.R.color.white))
            timer2.setTextColor(resources.getColor(android.R.color.white))
            timer3.setTextColor(resources.getColor(R.color.colorAccentLight))
        }

        difficulty = Difficulty.fromInt(intent.extras.getInt(ARG_DIFFICULTY))

        loadWords()
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    private fun loadWords() {
        words = Gson().fromJson<ArrayList<Word>>(
                IOUtils.toString(assets.open("words.json"), "UTF-8"),
                object : TypeToken<ArrayList<Word>>() {}.type
        ).filter { it.difficulty == difficulty.value }
    }

    private fun loadWord() {
        if (words == null) return
        word.text = words!![Random().nextInt(words!!.size)].word
    }

    private fun resetTimer() {
        timerText.text = getString(R.string.time_out)
        timerText.setTextColor(resources.getColor(R.color.timer_end))
        next.setImageResource(R.drawable.ic_play_arrow_gray_24dp)
    }

    private fun startTimer() {
        timerText.setTextColor(resources.getColor(android.R.color.white))

        timer = object : CountDownTimer(selectedTimerDuration, 1000) {
            override fun onFinish() {
                runOnUiThread {
                    resetTimer()

                    Sounds.play(Sounds.alarm)

                    this.cancel()
                    timer = null
                }
            }

            override fun onTick(millisRemaining: Long) {
                runOnUiThread {
                    val minutes = millisRemaining / 60000
                    val seconds = (millisRemaining - minutes * 60000) / 1000
                    timerText.text = getString(R.string.m_s_left, minutes, seconds)

                    if (minutes < 1 && seconds < 10) {
                        Sounds.play(Sounds.intenseTick)
                    } else {
                        Sounds.play(Sounds.casualTick)
                    }
                }
            }
        }.start()
    }

    enum class Difficulty(val value: Int) {
        EASY(1), MEDIUM(2), HARD(3);

        companion object {
            fun fromInt(int: Int?): Difficulty {
                return when (int) {
                    1 -> EASY
                    2 -> MEDIUM
                    3 -> HARD
                    else -> EASY
                }
            }
        }
    }

    companion object {
        private val ARG_DIFFICULTY = "difficulty"

        private val TIMER_1_DURATION = 90000L
        private val TIMER_2_DURATION = 120000L
        private val TIMER_3_DURATION = 150000L

        private val FLIPPER_TIMER_SELECTION = 0
        private val FLIPPER_TIMER_STATUS = 1

        fun getLaunchIntent(context: Context, difficulty: Difficulty): Intent {
            val intent = Intent(context, WordsActivity::class.java)
            intent.putExtra(ARG_DIFFICULTY, difficulty.value)
            return intent
        }
    }
}
