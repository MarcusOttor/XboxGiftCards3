package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.AppTools
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.R
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.analytics.Analytics
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.managers.PreferencesManager
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindCoinView()
        bind()

        toolbarText.text = "Gift Cards"

        initScroll()

        initBanner()

        if (intent.getBooleanExtra("notification", false)) {
            coinsManager.addCoins(4)
            updateCoins()
            Analytics.report(Analytics.NOTIFICATION)
            dialogsManager.showAlertDialog(supportFragmentManager,
                    "You got 4 coins!", {
                admobInterstitial?.show {  }
            })
        }

        checkBonusCoins()
    }

    @OnClick(R.id.addCoinsText)
    fun back() {
        startActivity(Intent(this, OffersActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    private fun initScroll() {
        cardScrollView.adapter = InfiniteScrollAdapter.wrap(CardsScrollAdapter(arrayOf(
                Pair(R.drawable.card_25, 12000),
                Pair(R.drawable.card_50, 20000),
                Pair(R.drawable.card_75, 28000),
                Pair(R.drawable.card_100, 36000),
                Pair(R.drawable.card_150, 48000),
                Pair(R.drawable.card_200, 64000))))
        cardScrollView.setSlideOnFling(true)
        cardScrollView.scrollToPosition(0)
    }

    @OnClick(R.id.claim, R.id.offers, R.id.logOut, R.id.share, R.id.rateUs, R.id.ticketcsGame, R.id.history)
    fun controlMain(view: View) {
        when (view.id) {
            R.id.claim -> {
                startActivity(Intent(this, ClaimActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.offers -> {
                startActivity(Intent(this, OffersActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.share -> {
                var mess = "I'am using this app to get free Xbox Gift Cards: \"https://play.google.com/store/apps/details?id=" +
                        packageName + "\"" + " Here is my invite code: " +
                        preferencesManager.get(PreferencesManager.INVITE_CODE, "") +
                        " Install an app and enter this code to get 200 Coins!"
                try {
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, mess), "Share"))
                } catch (ex: Exception) {}
            }
            R.id.rateUs -> {
                dialogsManager.showAlertDialog(supportFragmentManager, "Ple".plus("ase, ").plus("rat").plus("e us")
                        .plus(" 5").plus(" sta").plus("rs!"), {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
                    } catch (ex: Exception) {}
                })
            }
            R.id.logOut -> {
                dialogsManager.showAdvAlertDialog(supportFragmentManager, "Are you sure?", {
                    preferencesManager.deleteAll()
                    coinsManager.deleteall()
                    startActivity(Intent(this, StartActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }, {})
            }
            R.id.ticketcsGame -> {
                if (AppTools.isNetworkAvaliable(this)) {
                    startActivity(Intent(this, GameActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No internet connection!", {
                        admobInterstitial?.show {  }
                    })
                }
            }
            R.id.history -> {
                dialogsManager.showHistoryDialog(supportFragmentManager)
            }
        }
    }

    override fun onBackPressed() {
        dialogsManager.showAdvAlertDialog(supportFragmentManager, "Do you really want to exit?", {
            finish()
        }, {
            admobInterstitial?.show {  }
        })
    }

    private fun checkBonusCoins() {
        if (preferencesManager.get(PreferencesManager.LAST_CHECKED, 0L) <= System.currentTimeMillis()) {
            preferencesManager.put(PreferencesManager.LAST_CHECKED, (System.currentTimeMillis() + (60 * 60 * 1000)))
            retrofitManager.invitecoins(preferencesManager.get(PreferencesManager.USERNAME, ""),
                    preferencesManager.get(PreferencesManager.PASSWORD, ""), { coins ->
                if (coins > 0) {
                    coinsManager.addCoins(coins)
                    updateCoins()
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Someone entered your invite code! You got $coins Coins!", {})
                }
            }, {})
        }
    }



    inner class CardsScrollAdapter(private val cards: Array<Pair<Int, Int>>): RecyclerView.Adapter<CardsScrollAdapter.CardsScrollHolder>() {
        override fun getItemCount(): Int {
            return cards.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardsScrollHolder {
            return CardsScrollHolder(LayoutInflater.from(parent?.context).inflate(R.layout.card_item, parent, false))
        }

        override fun onBindViewHolder(holder: CardsScrollHolder?, position: Int) {
            holder?.itemView!!.cardImg.setImageDrawable(resources.getDrawable(cards[position].first))
            holder.itemView!!.cardImg.setOnClickListener {
                var intent = Intent(this@MainActivity, RedeemActivity::class.java)
                        .putExtra("card", cards[position].first)
                        .putExtra("price", cards[position].second)
                startActivity(intent)
                finish()
            }
        }

        inner class CardsScrollHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    }
}
