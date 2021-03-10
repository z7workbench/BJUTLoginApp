package top.z7workbench.bjutloginapp.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val migration1To2 = migration(1, 2) {
    it.execSQL("""alter table user add column secret text""")
//    it.execSQL("""create table info(id integer primary key, time long, place text, message text, level text)""")
}

fun migration(startVersion: Int, endVersion: Int, migrationFunc: (SupportSQLiteDatabase) -> Unit): Migration =
        object : Migration(startVersion, endVersion) {
            override fun migrate(database: SupportSQLiteDatabase) {
                migrationFunc(database)
            }
        }