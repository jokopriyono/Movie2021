package com.joko.movie2021.repository.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.joko.movie2021.repository.collections.CollectionNames.FAVOURITES_NAME
import com.joko.movie2021.repository.collections.CollectionNames.IN_THEATRES_NAME
import com.joko.movie2021.repository.collections.CollectionNames.POPULAR_NAME
import com.joko.movie2021.repository.collections.CollectionNames.UPCOMING_NAME
import com.joko.movie2021.repository.collections.CollectionNames.WATCHLIST_NAME
import com.joko.movie2021.repository.movies.Movie

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey
    val name: String,
    @ColumnInfo(name = "contents")
    val contents: List<Int>
) {
    @Ignore
    var movies: List<Movie>? = null
}

object CollectionNames {
    const val FAVOURITES_NAME = "favourite"
    const val WATCHLIST_NAME = "watchlist"
    const val POPULAR_NAME = "popular"
    const val UPCOMING_NAME = "upcoming"
    const val IN_THEATRES_NAME = "in-theatres"
}

sealed class CollectionType(val name: String) {
    object Favourite : CollectionType(FAVOURITES_NAME)
    object Watchlist : CollectionType(WATCHLIST_NAME)
    object Popular : CollectionType(POPULAR_NAME)
    object Upcoming : CollectionType(UPCOMING_NAME)
    object InTheatres : CollectionType(IN_THEATRES_NAME)
}
