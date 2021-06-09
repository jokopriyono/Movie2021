package com.joko.movie2021.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joko.movie2021.R
import com.joko.movie2021.core.extensions.gone
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
            .into(holder.imgMovie)
        ViewCompat.setTransitionName(holder.imgMovie, transitionName)
        holder.imgMovie.setOnClickListener(clickListener)
    }

    inner class MovieViewHolder : KotlinEpoxyHolder(), KoinComponent {
        val imgMovie by bind<ImageView>(R.id.img_movie)
    }
}

@EpoxyModelClass(layout = R.layout.item_popular_grid)
abstract class MovieGridModel : EpoxyModelWithHolder<MovieGridModel.MovieViewHolder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    lateinit var overview: String

    @EpoxyAttribute
    lateinit var posterUrl: String

    @EpoxyAttribute
    lateinit var genres: List<String>

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
        holder.txtTitle.text = title
        holder.txtOverview.text = overview
        if (genres.isNotEmpty()) {
            var text = ""
            genres.mapIndexed { idx, t ->
                text += if (idx != genres.size - 1) "$t, " else t
            }
            holder.txtGenres.text = text
        } else {
            holder.txtGenres.gone()
        }
        glide.load(posterUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply {
                RequestOptions()
//                    .placeholder(R.drawable.ic_round_local_movies_24px)
//                    .error(R.drawable.ic_round_local_movies_24px)
//                    .fallback(R.drawable.ic_round_local_movies_24px)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            .into(holder.imgMovie)
        ViewCompat.setTransitionName(holder.imgMovie, transitionName)
        holder.imgMovie.setOnClickListener(clickListener)
    }

    inner class MovieViewHolder : KotlinEpoxyHolder(), KoinComponent {
        val imgMovie by bind<ImageView>(R.id.img_movie)
        val txtTitle by bind<TextView>(R.id.txt_title)
        val txtOverview by bind<TextView>(R.id.txt_overview)
        val txtGenres by bind<TextView>(R.id.txt_genres)
    }
}

@EpoxyModelClass(layout = R.layout.item_cast)
abstract class CastModel : EpoxyModelWithHolder<CastModel.ActorHolder>() {

    @EpoxyAttribute
    var actorId: Int? = null

    @EpoxyAttribute
    lateinit var name: String

    @EpoxyAttribute
    lateinit var pictureUrl: String

    @EpoxyAttribute
    lateinit var transitionName: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var glide: RequestManager

    override fun bind(holder: ActorHolder) {
        super.bind(holder)
        with(holder) {
            actorName.text = name
            glide
                .load(pictureUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(
                    RequestOptions()
//                        .placeholder(R.drawable.ic_round_account_circle_24px)
//                        .error(R.drawable.ic_round_account_circle_24px)
//                        .fallback(R.drawable.ic_round_account_circle_24px)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                )
                .into(actorPicture)
            actorPicture.setOnClickListener(clickListener)
            ViewCompat.setTransitionName(actorPicture, transitionName)
        }
    }

    override fun unbind(holder: ActorHolder) {
        super.unbind(holder)
        glide.clear(holder.actorPicture)
    }

    inner class ActorHolder : KotlinEpoxyHolder(), KoinComponent {
        val actorName by bind<TextView>(R.id.txt_actor_name)
        val actorPicture by bind<ImageView>(R.id.img_actor)
    }
}