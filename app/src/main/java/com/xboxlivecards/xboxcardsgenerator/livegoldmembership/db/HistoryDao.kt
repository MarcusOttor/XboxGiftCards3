package  com.xboxlivecards.xboxcardsgenerator.livegoldmembership.db

import android.arch.persistence.room.*

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Query("DELETE FROM history")
    fun deleteAll()

    @Insert
    fun insert(historyItem: History)

    @Update
    fun update(historyItem: History)

    @Delete
    fun delete(historyItem: History)
}
