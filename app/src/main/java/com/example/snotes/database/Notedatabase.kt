package com.example.snotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Notesdata::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class Notedatabase :RoomDatabase() {
    abstract fun notesdao():Notesdao

    companion object{
        @Volatile
        private var INSTANCE: Notedatabase? = null

        fun getDatabase(context: Context): Notedatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Notedatabase::class.java,
                    "notes_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
        fun closeDatabase(context: Context) {
            val instance = INSTANCE ?: return
            instance.close()
            INSTANCE = null
        }
        fun deleteDatabase(context: Context) {
            //closeDatabase(context)
            val databaseFile = context.getDatabasePath("notes_database")
            if (databaseFile.exists()) {
                databaseFile.delete()
            }
        }
    }
}