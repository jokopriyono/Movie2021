package com.joko.movie2021.repository.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joko.movie2021.repository.actors.Actor
import com.joko.movie2021.repository.actors.ActorsDao
import com.joko.movie2021.repository.collections.Collection
import com.joko.movie2021.repository.collections.CollectionDao
import com.joko.movie2021.repository.data.local.Converters
import com.joko.movie2021.repository.movies.*

@Database(
    entities = [Movie::class, Actor::class, Collection::class, AccountState::class, Cast::class, MovieTrailer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDBDatabase : RoomDatabase() {

    internal abstract fun moviesDao(): MovieDao
    internal abstract fun actorsDao(): ActorsDao
    internal abstract fun collectionsDao(): CollectionDao
}
