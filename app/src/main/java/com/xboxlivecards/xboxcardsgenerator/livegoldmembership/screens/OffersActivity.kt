package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.R
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.MyApplication
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.PreferencesManager
import kotlinx.android.synthetic.main.toolbar.*

class OffersActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        bindCoinView()
        bind()

        preferencesManager.put(PreferencesManager.ADDITIONAL_LIFE, false)

        toolbarText.text = "Free Coins"

        initBanner()
    }

    @OnClick(R.id.addCoinsText)
    fun back(view: View) {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    override fun onResume() {
        super.onResume()
        (application as MyApplication).advertisement?.fyber?.onResume(this)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.bestOffers,
            R.id.awesomeOffers,
            R.id.bestVideo,
            R.id.beautifulVideo,
            R.id.brilliantVideo,
            R.id.fantasticVideo,
            R.id.offersChest,
            R.id.coolVideo)
    fun offersClick(view: View) {
        animationsManager.btnClick(view, {}, {
            if (view.id == R.id.bestOffers) {
                if ((application as MyApplication).advertisement?.house != null) {
                    (application as MyApplication).advertisement?.house?.show(coinsView)
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }
            } else if (view.id == R.id.awesomeOffers) {
                if ((application as MyApplication).advertisement?.fyber != null) {
                    (application as MyApplication).advertisement?.fyber?.show(this, coinsView)
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }
            } else if (view.id == R.id.offersChest) {
                if ((application as MyApplication).advertisement?.offertoro != null) {
                    (application as MyApplication).advertisement?.offertoro?.show(this, coinsView)
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }
            } else if (view.id == R.id.bestVideo) {
                if ((application as MyApplication).advertisement?.unity != null) {
                    if (!(application as MyApplication).advertisement?.unity?.showVideo(this, coinsView)!!) {
                        dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                            admobInterstitial?.show { }
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }
            } else if (view.id == R.id.beautifulVideo) {
                if ((application as MyApplication).advertisement?.vungle != null) {
                    if (!(application as MyApplication).advertisement?.vungle?.showVideo(coinsView)!!) {
                        dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                            admobInterstitial?.show { }
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }
            } else if (view.id == R.id.brilliantVideo) {
if ((application as MyApplication).advertisement?.adcolony != null) {
                    if (!(application as MyApplication).advertisement?.adcolony?.showVideo(coinsView)!!) {
                        dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                            admobInterstitial?.show { }
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }          
            } else if (view.id == R.id.fantasticVideo) {
                if ((application as MyApplication).advertisement?.admob != null) {
                    if (!(application as MyApplication).advertisement?.admob?.show(coinsView)!!) {
                        dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                            admobInterstitial?.show {  }
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show {  }
                    })
                }
            } else if (view.id == R.id.coolVideo) {
                if ((application as MyApplication).advertisement?.fyberVideo != null) {
                    if (!(application as MyApplication).advertisement?.fyberVideo?.show(this, coinsView)!!) {
                        dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                            admobInterstitial?.show { }
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No offers available!", {
                        admobInterstitial?.show { }
                    })
                }
            }
        })
    }
}
