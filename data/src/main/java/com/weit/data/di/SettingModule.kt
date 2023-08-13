package com.weit.data.di

import android.content.Context
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import com.weit.data.repository.setting.SettingRepositoryImpl
import com.weit.data.source.SettingDataSource
import com.weit.domain.repository.setting.SettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class SettingModule {

    @ActivityScoped
    @Provides
    fun provideSettingRepository(dataSource: SettingDataSource): SettingRepository =
        SettingRepositoryImpl(dataSource)

    @ActivityScoped
    @Provides
    fun provideSettingDataSource(
        powermanager: PowerManager,
        @ActivityContext context: Context,
    ): SettingDataSource =
        SettingDataSource(powermanager, context)

    @ActivityScoped
    @Provides
    fun providePowerManager(@ActivityContext context: Context): PowerManager =
        context.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
}
