package com.bagas.obrol.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bagas.obrol.data.local.account.AccountDAO
import com.bagas.obrol.data.local.account.AccountEntity
import com.bagas.obrol.data.local.message.MessageDAO
import com.bagas.obrol.data.local.message.MessageEntity
import com.bagas.obrol.data.local.profile.ProfileDAO
import com.bagas.obrol.data.local.profile.ProfileEntity

@Database(entities = [AccountEntity::class, MessageEntity::class, ProfileEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDAO
    abstract fun messageDao(): MessageDAO
    abstract fun profileDao(): ProfileDAO

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "ObrolDatabase")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
