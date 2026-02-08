package com.example.liliplayer.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.liliplayer.playback.PlaybackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken {
        return SessionToken(context, ComponentName(context, PlaybackService::class.java))
    }
}
