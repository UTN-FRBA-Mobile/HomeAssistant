package ar.edu.utn.frba.homeassistant.di

import android.content.Context
import androidx.room.Room
import ar.edu.utn.frba.homeassistant.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "home-assistant"
    )
        .fallbackToDestructiveMigration() // Warning! This may cause data loss
        .build()

    @Singleton
    @Provides
    fun provideDeviceDao(db: AppDatabase) = db.deviceDao()

    @Singleton
    @Provides
    fun provideSceneDao(db: AppDatabase) = db.sceneDao()

    @Singleton
    @Provides
    fun provideAutomationDao(db: AppDatabase) = db.automationDao()
}