package com.parithidb.zobazeassignment.data.di

import android.content.Context
import com.parithidb.zobazeassignment.data.database.AppDatabase
import com.parithidb.zobazeassignment.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {

    @Provides
    @ActivityRetainedScoped
    fun provideExpenseRepository(
        @ApplicationContext context: Context,
        database: AppDatabase
    ): ExpenseRepository {
        return ExpenseRepository(context, database)
    }

}