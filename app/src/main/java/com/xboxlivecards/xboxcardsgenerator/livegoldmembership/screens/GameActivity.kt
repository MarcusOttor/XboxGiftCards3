package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import butterknife.BindViews
import butterknife.OnClick
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.R
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.MyApplication
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.advertisements.AdvertisementManager
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.PreferencesManager
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class GameActivity : BaseActivity(), Runnable {

    @BindViews(R.id.life1, R.id.life2, R.id.life3)
    lateinit var lives : Array<ImageView>

    private var isGameStarted = false

    private var handler = Handler()

    private var ads: AdvertisementManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        bindCoinView()
        bind()

        ads = (application as MyApplication).advertisement

        toolbarText.text = "Roulette"

        updateLives()

        if (preferencesManager.get(PreferencesManager.TICKETS_LIFES, 3) == 0) {
            if (preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L) < System.currentTimeMillis()) {
                preferencesManager.put(PreferencesManager.TICKETS_LIFES, 3)
                preferencesManager.put(PreferencesManager.ADDITIONAL_ATTEMPT, true)
                preferencesManager.put(PreferencesManager.ADDITIONAL_LIFE, false)
                updateLives()
            } else {
                timer.visibility = View.VISIBLE
                handler.post(this)
                scheduleAlarm()
            }
        }

        initBanner()
    }

    @OnClick(R.id.addCoinsText)
    fun back() {
        onBackPressed()
    }

    private fun updateLives() {
        lives.forEach { l ->
            l.setImageDrawable(resources.getDrawable(
                    if (lives.indexOf(l) + 1 <= preferencesManager.get(PreferencesManager.TICKETS_LIFES, 3)) {
                        R.drawable.heart_active
                    } else {
                        R.drawable.heart_inactive
                    }))
        }
    }

    @OnClick(R.id.roulette)
    fun spin() {
        if (!isGameStarted) {
            if (preferencesManager.get(PreferencesManager.TICKETS_LIFES, 3) > 0) {
                isGameStarted = true
                val randomTime = Random().nextInt(6000 - 3000 + 1) + 3000
                val randomDegree = Random().nextInt(4000 - 1000 + 1) + 1000
                roulette.animate().rotationBy(randomDegree.toFloat())
                        .setDuration(randomTime.toLong()).setListener(spinListener {
                    roulette.rotation %= 360
                    val revenue = checkRotation(roulette.rotation)
                    coinsManager.addCoins(revenue)
                    updateCoins()
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Congratulations! You got $revenue Coins", {
                        admobInterstitial?.show {  }
                    })
                    println(roulette.rotation)
                    preferencesManager.put(PreferencesManager.TICKETS_LIFES, preferencesManager.get(PreferencesManager.TICKETS_LIFES, 3) - 1)
                    if (preferencesManager.get(PreferencesManager.TICKETS_LIFES, 3) == 0) {
                        preferencesManager.put(PreferencesManager.TICKETS_TIME, System.currentTimeMillis() + 4 * 60 * 60 * 1000)
                        timer.visibility = View.VISIBLE
                        scheduleAlarm()
                        handler.post(this)
                    }
                    updateLives()
                    isGameStarted = false
                }).start()
            } else {
                dialogsManager.showAlertDialog(supportFragmentManager,
                        "Sorry, the game is cooling down!", {
                    admobInterstitial?.show {  }
                })
            }
        }
    }

    private fun checkRotation(degree: Float): Int {
        return if ((degree >= 0) and (degree < 60)) {
            2
        } else if ((degree >= 60) and (degree < 120)) {
            8
        } else if ((degree >= 120) and (degree < 180)) {
            10
        } else if ((degree >= 180) and (degree < 240)) {
            4
        } else if ((degree >= 240) and (degree < 300)) {
            12
        } else if ((degree >= 300) and (degree <= 360)) {
            6
        } else {
            2
        }
    }

    private fun spinListener(onComplete: () -> Unit) = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            onComplete()
        }
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
    }

    override fun onResume() {
        super.onResume()
        updateLives()
    }

    override fun onBackPressed() {
        if (!isGameStarted) {
            startActivity(Intent(this, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    override fun run() {
        if (preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L) <= System.currentTimeMillis()) {
            preferencesManager.put(PreferencesManager.TICKETS_LIFES, 3)
            timer.visibility = View.GONE
            updateLives()
            handler.removeCallbacks(this)
        } else {
            updateTimer()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(this)
        super.onDestroy()
    }

    private fun updateTimer() {
        var timeDifference = (preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L) - System.currentTimeMillis()) / 1000
        var hour = (timeDifference / 3600).toInt()
        var minute = ((timeDifference - hour * 3600) / 60).toInt()
        var second = (timeDifference - (hour * 3600).toLong() - (minute * 60).toLong()).toInt()
        timer.text = time(hour.toString()) + ":" + time(minute.toString()) + ":" + time(second.toString())
    }

    private fun time(time: String) : String = if (time.length == 1) { "0" + time } else { time }
}

