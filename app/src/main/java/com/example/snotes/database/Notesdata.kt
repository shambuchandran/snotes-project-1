package com.example.snotes.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_data")
data class Notesdata(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val title:String,
    val subTitle:String,
    val text:String,
    val date: String,
    val alarm:String ="",
    val imagePaths:MutableList<String> = mutableListOf(),
    val audioPaths: MutableList<String> = mutableListOf(),
    val audioDuration: MutableList<String> = mutableListOf(),
    val audioFilename:MutableList<String> = mutableListOf(),
)
