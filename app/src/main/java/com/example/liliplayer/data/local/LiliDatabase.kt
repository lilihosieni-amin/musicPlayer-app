package com.example.liliplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.liliplayer.data.local.dao.*
import com.example.liliplayer.data.local.entity.*

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        FavoriteEntity::class,
        RecentEntity::class,
        PlayCountEntity::class,
        TagEntity::class,
        TagSongCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
abstract class LiliDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistSongDao(): PlaylistSongDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao
    abstract fun playCountDao(): PlayCountDao
    abstract fun tagDao(): TagDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `tags` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT NOT NULL DEFAULT '#6941C6'
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `tag_songs` (
                        `tagId` INTEGER NOT NULL,
                        `songId` INTEGER NOT NULL,
                        PRIMARY KEY(`tagId`, `songId`)
                    )
                """)
            }
        }
    }
}
