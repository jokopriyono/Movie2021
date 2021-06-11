package com.joko.movie2021.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joko.movie2021.R
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.ui.common.EpoxyCallbacks


class SliderAdapter(
    private val context: Context,
    private val movies: List<Movie>,
    private val glide: RequestManager,
    private val callbacks: EpoxyCallbacks
) : PagerAdapter() {
    override fun getCount() = movies.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = inflater.inflate(R.layout.item_slider, container, false)
        val imgMovie = itemView.findViewById<ImageView>(R.id.img_movie)
        glide.load(movies[position].backdropPath)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply {
                RequestOptions()
//                    .placeholder(R.drawable.ic_round_local_movies_24px)
//                    .error(R.drawable.ic_round_local_movies_24px)
//                    .fallback(R.drawable.ic_round_local_movies_24px)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            .into(imgMovie)
        imgMovie.setOnClickListener {
            callbacks.onMovieItemClicked(movies[position].id, sharedView = null)
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}