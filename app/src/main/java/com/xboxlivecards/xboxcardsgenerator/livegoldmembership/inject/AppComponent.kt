package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.inject

import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.MyApplication
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.core.services.ClaimService
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens.BaseActivity
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.screens.dialogs.*
import dagger.Component

@Component(modules = arrayOf(AppModule::class, MainModule::class))
interface AppComponent {

    fun inject(screen: BaseActivity)
    fun inject(app: MyApplication)
    fun inject(dialog: LoginDialog)
    fun inject(dialog: SignupDialog)
    fun inject(dialog: PromocodeDialog)
    fun inject(dialog: RedeemDialog)
    fun inject(service: ClaimService)
    fun inject(dialog: HistoryDialog)
}
