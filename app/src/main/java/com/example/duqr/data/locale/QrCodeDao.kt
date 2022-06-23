package com.example.duqr.data.locale

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrCodeDao {
    @Insert
    fun saveQrCode(qrCode: QrCode)

    @Query("select * from QrCode")
    fun getQrCodePaths() : Flow<QrCode>
}