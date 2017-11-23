package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.advertisements

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.analytics.Analytics
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.CoinsManager
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.PreferencesManager
import com.fyber.ads.AdFormat
import com.fyber.cache.CacheManager
import com.fyber.cache.OnVideoCachedListener
import com.fyber.currency.VirtualCurrencyErrorResponse
import com.fyber.currency.VirtualCurrencyResponse
import com.fyber.requesters.*
import com.sponsorpay.publisher.mbe.player.caching.SPCacheManager

class FyberVideo(
        private var preferencesManager: PreferencesManager,
        private var coinsManager: CoinsManager) {

    private var isAvailable = false
    private var videoIntent: Intent? = null
    private var activity: AppCompatActivity? = null
    private var coinsText: TextView? = null

    fun init(activity: AppCompatActivity) {
        this.activity = activity
        SPCacheManager.resumeDownloads(activity)
        CacheManager.registerOnVideoCachedListener(VideoCachedCallback(), activity)
        if (CacheManager.hasCachedVideos()) {
            isAvailable = true
        }
        requestVideo()
    }

    private fun requestVideo() {
        RewardedVideoRequester.create(object : RequestCallback {
            override fun onAdAvailable(p0: Intent?) {
                videoIntent = p0
                println("FYBER_VIDEO_AVAILABLE")
            }

            override fun onAdNotAvailable(p0: AdFormat?) {
                println("FYBER_VIDEO_UNAVAILABLE")
            }

            override fun onRequestError(p0: RequestError?) {
                println("FYBER_VIDEO_ERROR")
            }
        })
                .withVirtualCurrencyRequester(VirtualCurrencyRequester.create(VirtualCallbackCurrency()))
                .notifyUserOnCompletion(false)
                .request(activity)
    }

    fun isAvailable(): Boolean {
        return ((videoIntent != null) and isAvailable)
    }

    fun show(activity: AppCompatActivity, coinsText: TextView): Boolean {
        this.coinsText = coinsText
        return if (((videoIntent != null) and isAvailable)) {
            activity?.startActivity(videoIntent)
            Analytics.report(Analytics.VIDEO, Analytics.FYBER, Analytics.OPEN)
            videoIntent = null
            requestVideo()
            true
        } else {
            requestVideo()
            false
        }
    }

    inner class VideoCachedCallback : OnVideoCachedListener() {
        override fun onVideoCached(p0: Boolean) {
            isAvailable = p0
        }
    }

    inner class VirtualCallbackCurrency : VirtualCurrencyCallback {
        override fun onSuccess(p0: VirtualCurrencyResponse?) {
            coinsManager.addCoins(((p0?.deltaOfCoins?.toInt() ?: 0)))
            coinsText?.text = coinsManager.getCoins().toString()
            Analytics.report(Analytics.VIDEO, Analytics.FYBER, Analytics.REWARD)
        }
        override fun onRequestError(p0: RequestError?) {}
        override fun onError(p0: VirtualCurrencyErrorResponse?) {}
    }
}
