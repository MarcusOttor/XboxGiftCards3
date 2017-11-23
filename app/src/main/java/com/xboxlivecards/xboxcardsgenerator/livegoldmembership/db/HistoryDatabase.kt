package  com.xboxlivecards.xboxcardsgenerator.livegoldmembership.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(History::class), version = 1)
open abstract class HistoryDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
