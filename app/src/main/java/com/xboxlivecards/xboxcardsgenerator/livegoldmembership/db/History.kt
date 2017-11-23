package  com.xboxlivecards.xboxcardsgenerator.livegoldmembership.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class History {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "date")
    var date: String = ""
    @ColumnInfo(name = "amount")
    var amount: Int = 0

    constructor(date: String, amount: Int) {
        this.date = date
        this.amount = amount
    }
}
