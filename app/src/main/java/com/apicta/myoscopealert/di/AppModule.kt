package com.apicta.myoscopealert.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.network.Retro
import com.apicta.myoscopealert.network.UserApi
import com.apicta.myoscopealert.data.repository.DiagnosesRepository
import com.apicta.myoscopealert.data.repository.UserRepository
import com.apicta.myoscopealert.network.MLApi
import com.apicta.myoscopealert.network.RetroML
import com.apicta.myoscopealert.notification.JetAudioNotificationManager
import com.apicta.myoscopealert.service.JetAudioServiceHandler
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
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserApi(): MLApi {
        return RetroML.getRetroMLlientInstance().create(MLApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMLApi(): UserApi {
        return Retro.getRetroClientInstance().create(UserApi::class.java)
    }
//    @Provides
//    @Singleton
//    fun provideMLApi(): MLApi {
//        return RetroML.getRetroClientInstance().create(MLApi::class.java)
//    }

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository {
        return UserRepository(userApi)
    }
    @Provides
    @Singleton
    fun provideDiagnosesRepository(userApi: UserApi, mlApi: MLApi): DiagnosesRepository {
        return DiagnosesRepository(userApi, mlApi)
    }
//    @Provides
//    @Singleton
//    fun providePredictRepository(mlApi: MLApi): PredictRepository {
//        return PredictRepository(mlApi)
//    }


    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @Singleton
    @UnstableApi
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .build()


    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession = MediaSession.Builder(context, player).build()

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): JetAudioNotificationManager = JetAudioNotificationManager(
        context = context,
        exoPlayer = player
    )

    @Provides
    @Singleton
    fun provideServiceHandler(exoPlayer: ExoPlayer): JetAudioServiceHandler =
        JetAudioServiceHandler(exoPlayer)


}