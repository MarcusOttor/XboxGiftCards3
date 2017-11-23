package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.AppTools
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.MyApplication
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.PreferencesManager
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.receiver.ClaimTimerFinishReceiver
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.inject.AppModule
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.inject.DaggerAppComponent
import java.util.*
import javax.inject.Inject

class ClaimService : Service() {

    private var timer: Timer = Timer()
    @Inject lateinit var preferencesManager: PreferencesManager

    companion object {
        var isTimerRunning = false
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .mainModule((application as MyApplication).mainModule)
                .build().inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (AppTools.isNetworkAvaliable(applicationContext)) {
            if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) != 30) {
                startTimer()
            } else {
                stopService()
            }
        }
        return START_STICKY
    }

    private fun startTimer() {
        isTimerRunning = true
        timer.schedule(ClaimTextUpdateTimer(), 0, 1000)
    }

    private fun stopService() {
        isTimerRunning = false
        timer.cancel()
        stopSelf()
    }

    inner class ClaimTextUpdateTimer : TimerTask() {
        override fun run() {
            if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) < 30) {
                if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) < 59) {
                    preferencesManager.put(PreferencesManager.CLAIM_SECONDS,
                            preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) + 1)
                } else {
                    preferencesManager.put(PreferencesManager.CLAIM_MINUTES,
                            preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) + 1)
                    preferencesManager.put(PreferencesManager.CLAIM_SECONDS, 0)
                }
            } else {
                sendBroadcast(Intent(applicationContext, ClaimTimerFinishReceiver::class.java))
                stopService()
            }
        }
    }
}
