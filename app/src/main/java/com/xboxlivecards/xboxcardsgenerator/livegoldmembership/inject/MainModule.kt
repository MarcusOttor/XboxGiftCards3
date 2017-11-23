package com.xboxlivecards.xboxcardsgenerator.livegoldmembership.inject

import android.arch.persistence.room.Room
import android.content.Context
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.R
import com.xboxlivecards.xboxcardsgenerator.livegoldmembership.db.HistoryDatabase
import com.yandex.metrica.YandexMetricaConfig
import dagger.Module
import dagger.Provides
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

@Module
class MainModule(var context: Context) {

    @Provides
    fun provideAppMetrica() : YandexMetricaConfig.Builder {
        return YandexMetricaConfig
                .newConfigBuilder(context.resources.getString(R.string.metrica))
    }

    @Provides
    fun provideCalligraphy() : CalligraphyConfig {
        return CalligraphyConfig
                .Builder()
                .setDefaultFontPath("fonts/avenir-book.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
    }

    @Provides
    fun provideContext() : Context {
        return context
    }

    @Provides
    fun provideDb() : HistoryDatabase {
        return Room.databaseBuilder(context, HistoryDatabase::class.java, "history_database")
                .allowMainThreadQueries().build()
    }
}
