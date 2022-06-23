package com.example.duqr.di

import android.content.Context
import androidx.room.Room
import com.example.duqr.constants.Constants
import com.example.duqr.data.locale.AppDatabase
import com.example.duqr.data.locale.QrCodeDao
import com.example.duqr.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "qrcodeDatabase")
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideQrCodeDao(appDatabase: AppDatabase): QrCodeDao {
        return appDatabase.QrCodeDao()
    }

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }
}