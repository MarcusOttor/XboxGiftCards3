package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.AppTools
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.R
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.db.History
import kotlinx.android.synthetic.main.redeem_activity.*
import kotlinx.android.synthetic.main.toolbar_back.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class RedeemActivity : BaseActivity(){

    private var card: Int = 0
    private var price: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_activity)

        bindCoinView()
        bind()

        toolbarText.text = "Redeem"

        initBanner()

        initCard()
    }

    @OnClick(R.id.addCoinsText)
    fun back(view: View) {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    private fun initCard() {
        card = intent.getIntExtra("card", 0)
        redeem_card.setImageDrawable(resources.getDrawable(card))
        price = intent.getIntExtra("price", 0)
    }

    @OnClick(R.id.moreCoinsBtn)
    fun earnMore() {
        startActivity(Intent(this, OffersActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.redeemBtn)
    fun redeem() {
        if (AppTools.isNetworkAvaliable(this)) {
            if (coinsManager.getCoins() >= price) {
                if (AppTools.isEmailAdressCorrect(emailText.text.toString())) {
                    var dismisser = dialogsManager.showProgressDialog(supportFragmentManager)
                    thread {
                        Thread.sleep(3000)
                        runOnUiThread {
                            coinsManager.subtractCoins(price)
                            updateCoins()
                            database.historyDao().insert(History(
                                    SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), coinsManager.getCoins()))
                            dismisser.dismiss()
                            dialogsManager.showAlertDialog(supportFragmentManager,
                                    "You will receive your Gift Card in 3 - 7 days!", {
                                onBackPressed()
                            })
                        }
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Email is not valid!", {
                        admobInterstitial?.show { admobInterstitial?.show { } }
                    })
                }
            } else {
                dialogsManager.showAlertDialog(supportFragmentManager,
                        "Not enough coins!", {
                    admobInterstitial?.show { admobInterstitial?.show { } }
                })
            }
        } else {
            dialogsManager.showAlertDialog(supportFragmentManager,
                    "No internet connection!", {
                admobInterstitial?.show { admobInterstitial?.show {  } }
            })
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}