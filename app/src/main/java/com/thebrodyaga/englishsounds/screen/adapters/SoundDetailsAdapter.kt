package com.thebrodyaga.englishsounds.screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.formats.NativeAdOptions
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
import kotlinx.android.synthetic.main.item_ad_vertical_short.view.*
import kotlinx.android.synthetic.main.item_show_more.view.*
import kotlinx.android.synthetic.main.item_sound_details.view.*
import kotlinx.android.synthetic.main.item_sound_header.view.*
import kotlinx.android.synthetic.main.item_word.view.*
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
        NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT
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
            include_ad.setAd(adItem, nativeAdLoader, adapterPosition)
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

        fun bind(item: ShortAdItem) = with(itemView) {
            ad_root_view.setAd(item, nativeAdLoader, adapterPosition)
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