package com.bpmn.editor.data

import com.bpmn.editor.model.Actor
import com.bpmn.editor.model.Scheme
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides

    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Scheme::class, Actor::class
            )
        ).deleteRealmIfMigrationNeeded()
            .compactOnLaunch()
            .build()
        return Realm.open(config)
    }
    @Singleton
    @Provides
    fun provideMongoRepository(realm: Realm): MongoRepository {
        return MongoRepositoryImpl(realm = realm)
    }
}
