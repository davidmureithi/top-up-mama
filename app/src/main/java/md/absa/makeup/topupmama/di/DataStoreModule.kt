package md.absa.makeup.topupmama.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import md.absa.makeup.topupmama.data.prefs_datastore.PrefsStore
import md.absa.makeup.topupmama.data.prefs_datastore.PrefsStoreImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    abstract fun bindPrefsStore(prefsStoreImpl: PrefsStoreImpl): PrefsStore
}
