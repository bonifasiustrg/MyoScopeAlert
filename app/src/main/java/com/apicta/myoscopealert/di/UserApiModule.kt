package com.apicta.myoscopealert.di

import com.apicta.myoscopealert.network.Retro
import com.apicta.myoscopealert.network.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserApiModule {

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return Retro.getRetroClientInstance().create(UserApi::class.java)
    }
}
