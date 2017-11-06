package party.iobserver.bjutloginapp.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import party.iobserver.bjutloginapp.model.User

/**
 * Created by ZeroGo on 2017/11/2.
 */
@Database(entities = arrayOf(User::class), version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}