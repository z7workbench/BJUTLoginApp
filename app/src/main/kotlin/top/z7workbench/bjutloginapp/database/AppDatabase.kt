package top.z7workbench.bjutloginapp.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import top.z7workbench.bjutloginapp.model.User

/**
 * Created by ZeroGo on 2017/11/2.
 */
@Database(entities = [(User::class)], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
//    abstract fun infoDao(): InfoDao
}