package tr.xip.turnitup

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import tr.xip.turnitup.WordsActivity.Difficulty

class MainActivity : FullscreenActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        easy.setOnClickListener {
            startActivity(WordsActivity.getLaunchIntent(this@MainActivity, Difficulty.EASY))
        }

        medium.setOnClickListener {
            startActivity(WordsActivity.getLaunchIntent(this@MainActivity, Difficulty.MEDIUM))
        }

        hard.setOnClickListener {
            startActivity(WordsActivity.getLaunchIntent(this@MainActivity, Difficulty.HARD))
        }
    }
}