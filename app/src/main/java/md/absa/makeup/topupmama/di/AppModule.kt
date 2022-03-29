package md.absa.makeup.topupmama.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import md.absa.makeup.App
import md.absa.makeup.topupmama.data.api.MyHttpClient
import md.absa.makeup.topupmama.data.api.RetrofitInstance
import md.absa.makeup.topupmama.data.api.RetrofitInterface
import md.absa.makeup.topupmama.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): App {
        return app as App
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(retrofitInstance: RetrofitInstance): RetrofitInterface {
        return retrofitInstance.retrofitInterface
    }

    @Singleton
    @Provides
    fun provideMyHttpClient(@ApplicationContext context: Context): MyHttpClient {
        return MyHttpClient(context)
    }

    @Singleton
    @Provides
    fun provideMakeUpDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}
