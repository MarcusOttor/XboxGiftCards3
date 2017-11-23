package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.R
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.MyApplication
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.advertisements.AdmobBanner
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.advertisements.AdmobInterstitial
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.*
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.receiver.GameCooldownReceiver
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.services.ClaimService
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.db.HistoryDatabase
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.inject.AppModule
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.inject.DaggerAppComponent
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject lateinit var preferencesManager: PreferencesManager
    @Inject lateinit var coinsManager: CoinsManager
    @Inject lateinit var retrofitManager: RetrofitManager
    @Inject lateinit var dialogsManager: DialogsManager
    @Inject lateinit var animationsManager: AnimationsManager
    @Inject lateinit var database: HistoryDatabase

    protected lateinit var coinsView: TextView

    var admobInterstitial: AdmobInterstitial? = null
    var banner: AdmobBanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        if ((application as MyApplication).advertisement != null) {
            (application as MyApplication).advertisement?.init(this)
        }
        
        admobInterstitial = (application as MyApplication).interstitialAdmob

        saveData()

        startService()
    }

    fun startService() {
        if (this !is StartActivity) {
            if (!ClaimService.isTimerRunning) {
                startService(Intent(this, ClaimService::class.java))
            }
        }
    }

    fun initBanner() {
        if (this !is StartActivity) {
            banner = AdmobBanner(preferencesManager, this)
        }
    }

    fun bindCoinView() {
        try {
            coinsView = findViewById<View>(R.id.coinsView) as TextView
        } catch (ex: Exception) {}
    }

    override fun onResume() {
        super.onResume()
        updateCoins()
        (application as MyApplication).advertisement?.onResume(this, true)
    }

    fun bind() {
        ButterKnife.bind(this)
    }

    fun updateCoins() {
        try {
            coinsView.text = coinsManager.getCoins().toString()
        } catch (ex: Exception) {}
    }

    fun inject() {
        DaggerAppComponent.builder()
                .appModule(AppModule(applicationContext))
                .mainModule((application as MyApplication).mainModule)
                .build().inject(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun scheduleAlarm() {
        var intent = Intent(this, GameCooldownReceiver::class.java)
        var pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L), pi)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L), pi)
        }
    }

    private fun saveData() {
        if ((preferencesManager.get(PreferencesManager.LAST_SAVED, 0L) < System.currentTimeMillis())
                and (preferencesManager.get(PreferencesManager.LAST_SAVED, 0L) != 0L)) {

            preferencesManager.put(PreferencesManager.LAST_SAVED, System.currentTimeMillis() + (5 * 60 * 1000))

            var data = ""

            data += "${coinsManager.getCoins()}"

            retrofitManager.savedata(preferencesManager.get(PreferencesManager.USERNAME, ""),
                    preferencesManager.get(PreferencesManager.PASSWORD, ""), data, {}, {})

        } else if (preferencesManager.get(PreferencesManager.LAST_SAVED, 0L) == 0L) {
            preferencesManager.put(PreferencesManager.LAST_SAVED, System.currentTimeMillis() + (1 * 60 * 1000))
        }
    }
}
