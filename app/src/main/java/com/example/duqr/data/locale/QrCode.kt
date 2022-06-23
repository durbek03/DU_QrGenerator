package com.example.duqr.data.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class QrCode(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val path: String
)