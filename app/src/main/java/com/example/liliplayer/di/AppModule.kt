package com.example.liliplayer.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.example.liliplayer.data.local.LiliDatabase
import com.example.liliplayer.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LiliDatabase {
        return Room.databaseBuilder(
            context,
            LiliDatabase::class.java,
            "lili_player.db"
        )
            .addMigrations(LiliDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun providePlaylistDao(db: LiliDatabase): PlaylistDao = db.playlistDao()

    @Provides
    fun providePlaylistSongDao(db: LiliDatabase): PlaylistSongDao = db.playlistSongDao()

    @Provides
    fun provideFavoriteDao(db: LiliDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideRecentDao(db: LiliDatabase): RecentDao = db.recentDao()

    @Provides
    fun providePlayCountDao(db: LiliDatabase): PlayCountDao = db.playCountDao()

    @Provides
    fun provideTagDao(db: LiliDatabase): TagDao = db.tagDao()
}
