package com.example.duqr.data.locale

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QrCode::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun QrCodeDao() : QrCodeDao
}