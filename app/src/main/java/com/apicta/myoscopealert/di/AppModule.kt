package com.apicta.myoscopealert.di

import android.content.Context
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.network.Retro
import com.apicta.myoscopealert.network.UserApi
import com.apicta.myoscopealert.repository.DiagnosesRepository
import com.apicta.myoscopealert.repository.UserRepository
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
    fun provideUserApi(): UserApi {
        return Retro.getRetroClientInstance().create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository {
        return UserRepository(userApi)
    }
    @Provides
    @Singleton
    fun provideDiagnosesRepository(userApi: UserApi): DiagnosesRepository {
        return DiagnosesRepository(userApi)
    }
}