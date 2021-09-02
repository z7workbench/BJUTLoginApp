package top.z7workbench.bjutloginapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import top.z7workbench.bjutloginapp.model.BundledUser
import top.z7workbench.bjutloginapp.model.User

/**
 * Created by ZeroGo on 2017/11/2.
 */
@Dao
interface UserDao {
    @Query("select * from user order by id")
    fun all(): LiveData<List<User>>

    @Query("select id, name from user order by id")
    fun allUsers(): LiveData<List<BundledUser>>

    @Query("select * from user where id = :id")
    suspend fun find(id: Int): User?

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}