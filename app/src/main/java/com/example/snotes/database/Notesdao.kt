package com.example.snotes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Notesdao {

    @Insert
    suspend fun insert(notesData: Notesdata)

    @Query("SELECT * FROM notes_data ORDER BY id ASC")
    fun getAllNotes():LiveData<List<Notesdata>>

    @Query("SELECT imagePaths FROM notes_data")
    fun getAllImagePaths(): LiveData<List<String>>

    @Query("SELECT audioPaths FROM notes_data")
    fun getAllAudioPaths(): LiveData<List<String>>

    @Update
    suspend fun update(notesData: Notesdata)

    @Delete
    suspend fun delete(notesData: Notesdata)


}