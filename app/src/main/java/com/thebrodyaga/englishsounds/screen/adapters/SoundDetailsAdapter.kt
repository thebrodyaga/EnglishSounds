package com.thebrodyaga.englishsounds.screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.data.PracticeWordDto
import com.thebrodyaga.englishsounds.domine.entities.data.SpellingWordDto
import com.thebrodyaga.englishsounds.domine.entities.data.WordDto
import com.thebrodyaga.englishsounds.domine.entities.ui.*
import com.thebrodyaga.englishsounds.screen.getVideoAndDescription
import com.thebrodyaga.englishsounds.screen.isGone
import com.thebrodyaga.englishsounds.tools.AudioPlayer
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import com.thebrodyaga.englishsounds.youtube.YoutubePlayerActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.item_show_more.view.*
import kotlinx.android.synthetic.main.item_sound_details.view.*
import kotlinx.android.synthetic.main.item_sound_header.view.*
import kotlinx.android.synthetic.main.item_word.view.*
import kotlinx.android.synthetic.main.item_ad_vertical_short.view.*
import timber.log.Timber
import java.io.File

class SoundDetailsAdapter constructor(
    private val audioPlayer: AudioPlayer,
    context: Context,
    lifecycle: Lifecycle
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var videoMap: MutableMap<String, String>? = null
    private var descriptionMap: MutableMap<String, String>? = null
    private val compositeAdLoader = CompositeAdLoader(
        context,
        lifecycle,
        NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE
    )

    var list = listOf<SoundsDetailsListItem>()
        private set

    fun setData(newData: List<SoundsDetailsListItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newData.toList()))
        list = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val videoAndDescription = recyclerView.context.getVideoAndDescription()
        videoMap = videoAndDescription.first
        descriptionMap = videoAndDescription.second
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        fun inflate(@LayoutRes res: Int) =
            LayoutInflater.from(parent.context)
                .inflate(res, parent, false)
        return when (viewType) {
            DETAIL -> SoundDetailsVH(inflate(R.layout.item_sound_details), compositeAdLoader)
            WORD_HEADER -> HeaderViewHolder(inflate(R.layout.item_sound_header))
            SHOW_MORE -> ShowMoreVH(inflate(R.layout.item_show_more)) { _, _ -> }
            SpellingWord -> SpellingWordVH(inflate(R.layout.item_word))
            PracticeWordDto -> PracticeWordVH(inflate(R.layout.item_word))
            ShortAdViewHolder -> ShortAdViewHolder(
                inflate(R.layout.item_ad_vertical_short), compositeAdLoader
            )
            else -> throw IllegalArgumentException("хуйня в onCreateViewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is SoundsDetailsWithAd -> DETAIL
            is WordsHeader -> WORD_HEADER
            is ShowMore -> SHOW_MORE
            is SpellingWordDto -> SpellingWord
            is PracticeWordDto -> PracticeWordDto
            is ShortAdItem -> ShortAdViewHolder
            else -> throw IllegalArgumentException("хуйня в getItemViewType")
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {
            is SoundsDetailsWithAd -> (holder as SoundDetailsVH).bind(item.sound, item.shortAdItem)
            is WordsHeader -> (holder as HeaderViewHolder).bind(item)
            is ShowMore -> (holder as ShowMoreVH).bind(item)
            is SpellingWordDto -> (holder as SpellingWordVH).bind(item)
            is PracticeWordDto -> (holder as PracticeWordVH).bind(item)
            is ShortAdItem -> (holder as ShortAdViewHolder).bind(item)
            else -> throw IllegalArgumentException("хуйня в onBindViewHolder")
        }
    }

    companion object {
        private const val DETAIL = 0
        private const val WORD_HEADER = 1
        private const val SHOW_MORE = 2
        private const val SpellingWord = 3
        private const val PracticeWordDto = 4
        private const val ShortAdViewHolder = 5
    }

    private abstract inner class BaseWordVH<T : WordDto>(view: View) :
        RecyclerView.ViewHolder(view) {

        init {
            view.word_root_view.setOnClickListener { it.play_icon.performClick() }
            view.play_icon.setRecordVoice(audioPlayer)
        }

        open fun bind(item: T) = with(itemView) {
            play_icon.audioFile = File(context.filesDir, item.audioPath)
        }
    }

    private inner class SoundDetailsVH constructor(
        view: View,
        private val nativeAdLoader: CompositeAdLoader
    ) : RecyclerView.ViewHolder(view) {
        private var item: AmericanSoundDto? = null

        var disposable: Disposable? = null
        val constraintSet = ConstraintSet()
        var adItem: ShortAdItem? = null

        init {
            view.include.setOnClickListener { it.play_icon.performClick() }
            view.include.play_icon.setRecordVoice(audioPlayer)
            view.include.play_icon.setRecordVoice(audioPlayer)
            view.youtube_layout.setOnClickListener {
                item?.let { item ->
                    val videoUrl = videoMap?.get(item.transcription)
                    if (videoUrl != null && videoUrl.isNotEmpty()) {
                        YoutubePlayerActivity.startActivity(
                            it.context,
                            PlayVideoExtra(videoUrl, item.name)
                        )
                    }
                }
            }
            with(itemView) {
                include_ad.ad_view.apply {
                    // The MediaView will display a video asset if one is present in the ad, and the
                    // first image asset otherwise.
                    mediaView = ad_media

                    // Register the view used for each individual asset.
                    headlineView = ad_headline
                    callToActionView = ad_call_to_action
                    priceView = ad_price
                    starRatingView = ad_stars
                    storeView = ad_google_play_icon
                    advertiserView = ad_advertiser
                }
            }

            Timber.i("SoundDetailsVH init")
        }

        fun bind(
            item: AmericanSoundDto,
            adItem: ShortAdItem
        ) = with(itemView) {
            Timber.i("SoundDetailsVH bind")
            this@SoundDetailsVH.item = item
            Glide.with(sound_image.context)
                .load(File(sound_image.context.filesDir, item.photoPath))
                .into(sound_image)
            val videoUrl = videoMap?.get(item.transcription)
            if (videoUrl == null || videoUrl.isEmpty())
                youtube_layout.isGone(true)
            else youtube_layout.loadYoutubeThumbnail(videoUrl)
            include.word.text = item.name.plus(" ").plus("[${item.transcription}]")
            include.play_icon.audioFile = File(context.filesDir, item.audioPath)
            description.text = descriptionMap?.get(item.transcription) ?: ""
            setIsRecyclable(false)

            this@SoundDetailsVH.adItem = adItem
            fun setEmptyView(isEmpty: Boolean) {
                if (isEmpty)
                    constraintSet.setVisibility(ad_empty.id, ConstraintSet.VISIBLE)
                else constraintSet.setVisibility(ad_empty.id, ConstraintSet.INVISIBLE)
            }

            fun populateNativeAdView(
                nativeAd: UnifiedNativeAd,
                adView: UnifiedNativeAdView
            ) {
                // Some assets are guaranteed to be in every UnifiedNativeAd.
                (adView.headlineView as TextView).text = nativeAd.headline
                (adView.callToActionView as Button).text = nativeAd.callToAction

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                if (nativeAd.price == null) {
                    constraintSet.setVisibility(adView.priceView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.priceView.id, ConstraintSet.VISIBLE)
                    (adView.priceView as TextView).text = nativeAd.price
                }
                if (nativeAd.store == null) {
                    constraintSet.setVisibility(adView.storeView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.storeView.id, ConstraintSet.VISIBLE)
                    (adView.storeView as? TextView)?.text = nativeAd.store
                }
                if (nativeAd.starRating == null) {
                    constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.VISIBLE)
                    (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
                }
                if (nativeAd.advertiser == null) {
                    constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.VISIBLE)
                    (adView.advertiserView as TextView).text = nativeAd.advertiser
                }
                // Assign native ad object to the native view.
                adView.setNativeAd(nativeAd)
            }

            disposable?.dispose()
            disposable = nativeAdLoader.getLoader(adItem.adTag, adapterPosition, adItem.customTag)
                .adsObservable
                .subscribe { adBox ->
                    constraintSet.clone(ad_container)
                    adBox.ad?.let {
                        setEmptyView(false)
                        if (it.mediaContent != null) {
                            constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.VISIBLE)
                        } else constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.GONE)
                        populateNativeAdView(it, ad_view)
                    } ?: kotlin.run {
                        setEmptyView(true)
                    }
                    TransitionManager.beginDelayedTransition(ad_container)
                    constraintSet.applyTo(ad_container)
                }
        }
    }

    private inner class SpellingWordVH(view: View) : BaseWordVH<SpellingWordDto>(view) {

        override fun bind(item: SpellingWordDto) = with(itemView) {
            super.bind(item)
            word.text = HtmlCompat.fromHtml(item.transcription, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private inner class PracticeWordVH(view: View) : BaseWordVH<PracticeWordDto>(view) {

        override fun bind(item: PracticeWordDto) = with(itemView) {
            super.bind(item)
            word.text = item.name
        }
    }

    private inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        fun bind(header: WordsHeader) = with(itemView) {
            title.setText(header.type.humanName())
        }
    }

    private inner class ShortAdViewHolder constructor(
        view: View,
        private val nativeAdLoader: CompositeAdLoader
    ) : RecyclerView.ViewHolder(view) {

        var disposable: Disposable? = null
        val constraintSet = ConstraintSet()
        var item: ShortAdItem? = null

        init {
            with(itemView) {
                ad_view.apply {
                    // The MediaView will display a video asset if one is present in the ad, and the
                    // first image asset otherwise.
                    mediaView = ad_media

                    // Register the view used for each individual asset.
                    headlineView = ad_headline
                    callToActionView = ad_call_to_action
                    priceView = ad_price
                    starRatingView = ad_stars
                    storeView = ad_google_play_icon
                    advertiserView = ad_advertiser
                }
            }
        }

        fun bind(item: ShortAdItem) = with(itemView) {
            this@ShortAdViewHolder.item = item
            fun setEmptyView(isEmpty: Boolean) {
                if (isEmpty)
                    constraintSet.setVisibility(ad_empty.id, ConstraintSet.VISIBLE)
                else constraintSet.setVisibility(ad_empty.id, ConstraintSet.INVISIBLE)
            }

            fun populateNativeAdView(
                nativeAd: UnifiedNativeAd,
                adView: UnifiedNativeAdView
            ) {
                // Some assets are guaranteed to be in every UnifiedNativeAd.
                (adView.headlineView as TextView).text = nativeAd.headline
                (adView.callToActionView as Button).text = nativeAd.callToAction

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                if (nativeAd.price == null) {
                    constraintSet.setVisibility(adView.priceView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.priceView.id, ConstraintSet.VISIBLE)
                    (adView.priceView as TextView).text = nativeAd.price
                }
                if (nativeAd.store == null) {
                    constraintSet.setVisibility(adView.storeView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.storeView.id, ConstraintSet.VISIBLE)
                    (adView.storeView as? TextView)?.text = nativeAd.store
                }
                if (nativeAd.starRating == null) {
                    constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.VISIBLE)
                    (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
                }
                if (nativeAd.advertiser == null) {
                    constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.GONE)
                } else {
                    constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.VISIBLE)
                    (adView.advertiserView as TextView).text = nativeAd.advertiser
                }
                // Assign native ad object to the native view.
                adView.setNativeAd(nativeAd)
            }

            disposable?.dispose()
            disposable = nativeAdLoader.getLoader(item.adTag, adapterPosition, item.customTag)
                .adsObservable
                .subscribe { adBox ->
                    constraintSet.clone(ad_container)
                    adBox.ad?.let {
                        setEmptyView(false)
                        if (it.mediaContent != null) {
                            constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.VISIBLE)
                        } else constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.GONE)
                        populateNativeAdView(it, ad_view)
                    } ?: kotlin.run {
                        setEmptyView(true)
                    }
                    TransitionManager.beginDelayedTransition(ad_container)
                    constraintSet.applyTo(ad_container)
                }
        }
    }

    private inner class ShowMoreVH(
        view: View,
        onClick: (adapterPosition: Int, item: ShowMore) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        var item: ShowMore? = null

        init {
            view.button
                .setOnClickListener { item?.also { onClick.invoke(adapterPosition, it) } }
        }

        fun bind(showMore: ShowMore) = with(itemView) {
            item = showMore
        }
    }

    class DiffCallback constructor(
        private var oldList: List<SoundsDetailsListItem>,
        private var newList: List<SoundsDetailsListItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is SoundsDetailsWithAd && newItem is SoundsDetailsWithAd ->
                    oldItem == newItem
                oldItem is WordsHeader && newItem is WordsHeader ->
                    oldItem.type == newItem.type
                oldItem is ShowMore && newItem is ShowMore ->
                    oldItem.key == newItem.key
                oldItem is SpellingWordDto && newItem is SpellingWordDto ->
                    oldItem.name == newItem.name
                oldItem is PracticeWordDto && newItem is PracticeWordDto ->
                    oldItem.name == newItem.name
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is SoundsDetailsWithAd && newItem is SoundsDetailsWithAd ->
                    oldItem == newItem
                oldItem is WordsHeader && newItem is WordsHeader ->
                    oldItem == newItem
                oldItem is ShowMore && newItem is ShowMore ->
                    oldItem == newItem
                oldItem is SpellingWordDto && newItem is SpellingWordDto ->
                    oldItem == newItem
                oldItem is PracticeWordDto && newItem is PracticeWordDto ->
                    oldItem == newItem
                else -> false
            }
        }
    }
}