package com.joko.movie2021.ui.common

import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joko.movie2021.R
import org.koin.core.KoinComponent

@EpoxyModelClass(layout = R.layout.item_movie_grid)
abstract class MovieModel : EpoxyModelWithHolder<MovieModel.MovieViewHolder>() {

    @EpoxyAttribute
    lateinit var posterUrl: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var transitionName: String

    @EpoxyAttribute
    var movieId: Int? = null

    @EpoxyAttribute
    lateinit var glide: RequestManager

    override fun bind(holder: MovieViewHolder) {
        super.bind(holder)
        glide.load(posterUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply {
                RequestOptions()
//                    .placeholder(R.drawable.ic_round_local_movies_24px)
//                    .error(R.drawable.ic_round_local_movies_24px)
//                    .fallback(R.drawable.ic_round_local_movies_24px)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            .into(holder.poster)
        ViewCompat.setTransitionName(holder.poster, transitionName)
        holder.poster.setOnClickListener(clickListener)
    }

    inner class MovieViewHolder : KotlinEpoxyHolder(), KoinComponent {
        val poster by bind<ImageView>(R.id.ivPoster)
    }
}