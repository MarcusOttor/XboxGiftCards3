package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.advertisements

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.CoinsManager
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.PreferencesManager

class AdvertisementManager {

    private var preferencesManager: PreferencesManager

    var house: HouseOffers? = null
    var adcolony: AdcolonyVideo? = null
    var offertoro: OffertoroOfferwall? = null
    var vungle: VungleVideo? = null
    var unity: UnityVideo? = null
    var admob: AdmobVideo? = null
    var fyber: FyberManager? = null
    var fyberVideo: FyberVideo? = null

    private var isActivated = false
    private var resuming = false

    constructor(preferencesManager: PreferencesManager, coinsManager: CoinsManager, context: Context) {
        this.preferencesManager = preferencesManager
        this.house = HouseOffers(context, "Coins", coinsManager)
        this.adcolony = AdcolonyVideo(preferencesManager, coinsManager)
        this.offertoro = OffertoroOfferwall(coinsManager, preferencesManager)
        this.vungle = VungleVideo(preferencesManager, coinsManager)
        this.unity = UnityVideo(preferencesManager, coinsManager)
        this.admob = AdmobVideo(coinsManager, preferencesManager, context)
        this.fyber = FyberManager(coinsManager, preferencesManager)
        this.fyberVideo = FyberVideo(preferencesManager, coinsManager)
    }

    fun init(activity: Activity) {
        if (!isActivated) {
            isActivated = true
            if (vungle != null) {
                vungle?.init(activity)
            }

            if (fyber != null) {
                fyber?.init(activity as AppCompatActivity)
            }

            if (offertoro != null) {
                offertoro?.init(activity)
            }
            if (house != null) {
                house?.init()
            }
            if (fyberVideo != null) {
                fyberVideo?.init(activity as AppCompatActivity)
            }
            Thread {
                if (unity != null) {
                    unity?.init(activity)
                }
                if (adcolony != null) {
                    adcolony?.init(activity)
                }
                onResume(activity, false)
            }.start()
        }
    }

    fun onResume(activity: Activity, thread: Boolean) {
        if (!resuming) {
            resuming = true
            if (thread) {
                Thread { resume(activity) }.start()
            } else {
                resume(activity)
            }
            try {
                if (vungle != null) {
                    vungle?.onResume()
                }
                if (fyber != null) {
                    fyber?.onResume(activity as AppCompatActivity)
                }
            } catch (ex: Exception) {
            }
        }
    }

    private fun resume(activity: Activity) {
        try {
            if (adcolony != null) {
                adcolony?.onResume(activity)
            }
        } catch (ex: Exception) {
        }
        resuming = false
    }
}
