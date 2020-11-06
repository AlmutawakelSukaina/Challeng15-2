package com.example.crimeintent1

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var date: Date = Date(),
                 var isSolved: Boolean = false,var suspect: String = "",var number:String="")

{
    val photoFileName
        get() = "IMG_$id.jpg"
}
