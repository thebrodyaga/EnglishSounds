package com.thebrodyaga.legacy.delegates

import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdvancedExercisesVideoItem
import com.thebrodyaga.legacy.ContrastingSoundVideoItem
import com.thebrodyaga.legacy.SoundVideoItem
import com.thebrodyaga.legacy.VideoItem
import com.thebrodyaga.legacy.adapters.utils.SoundItemViewCache
import com.thebrodyaga.legacy.color
import com.thebrodyaga.legacy.databinding.ItemYoutubeVideoBinding
import timber.log.Timber
import kotlinx.android.synthetic.main.item_sound_min.view.*
import kotlinx.android.synthetic.main.item_youtube_video.*
import kotlinx.android.synthetic.main.item_youtube_video.view.*
import kotlinx.android.synthetic.main.view_youtube_thumbnail.view.*

fun videoItemDelegate(
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL,
    viewCache: () -> SoundItemViewCache?,
    getColor: (context: Context, colorRes: Int) -> ColorStateList?,
    onSoundClick: (transcription: String) -> Unit,
    youtubeScreenFactory: YoutubeScreenFactory,
    router: Router,
) = adapterDelegateViewBinding<VideoItem, Any, ItemYoutubeVideoBinding>(
    { layoutInflater, root -> ItemYoutubeVideoBinding.inflate(layoutInflater, root, false) }
) {

    val constraintSet = ConstraintSet()
    val onSoundViewClick = View.OnClickListener { onSoundClick(it.sound.text.toString()) }

    (itemView as ConstraintLayout).apply {
        if (orientation == RecyclerView.VERTICAL) {
            itemView.layoutParams = itemView.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            constraintSet.clone(this)
            constraintSet.constrainHeight(
                youtube_video_layout.id,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            constraintSet.applyTo(this)
        }
        setOnClickListener {
            router.navigateTo(youtubeScreenFactory.youtubeScreen(
                PlayVideoExtra(item.videoId, item.title)
            ))
        }
        item_youtube_video_thumbnail.youtube_play_icon.isVisible = false
    }

    fun addIfNeedSoundItems(item: VideoItem) = with(itemView) {
        val sounds = when {
            item is ContrastingSoundVideoItem -> ArrayList<AmericanSoundDto>().apply {
                item.firstTranscription?.let { add(it) }
                item.secondTranscription?.let { add(it) }
            }
            item is AdvancedExercisesVideoItem -> ArrayList<AmericanSoundDto>().apply {
                item.firstTranscription?.let { add(it) }
                item.secondTranscription?.let { add(it) }
            }
            item is SoundVideoItem && item.sound != null -> mutableListOf(item.sound)
            else -> return
        }
        sounds.forEach { soundDto ->
            val soundView = viewCache()?.popViewFromCache() ?: return@with
            soundView.setOnClickListener(onSoundViewClick)
            val sound = soundView.sound
            soundDto?.let {
                sound.text = soundDto.transcription
                sound.backgroundTintList = getColor(context, soundDto.soundType.color())
            }
            youtube_video_layout.addView(soundView)
        }
        var previousSoundItem: CardView? = null
        val soundViews = youtube_video_layout.children.filterIsInstance<CardView>()
        constraintSet.clone(youtube_video_layout)

        soundViews.forEachIndexed { index, soundView ->
            constraintSet.setDimensionRatio(soundView.id, "w,1:1")
            constraintSet.constrainHeight(soundView.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainWidth(soundView.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.setHorizontalChainStyle(soundView.id, ConstraintSet.CHAIN_PACKED)
            constraintSet.setHorizontalBias(soundView.id, 0f)
            constraintSet.connect(
                soundView.id,
                ConstraintSet.TOP,
                guideline.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                soundView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
            when (index) {
                0 -> {
                    constraintSet.connect(
                        soundView.id,
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START
                    )
                    constraintSet.connect(
                        soundView.id,
                        ConstraintSet.END,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.END
                    )
                    previousSoundItem = soundView
                }
                sounds.lastIndex -> {
                    constraintSet.connect(
                        soundView.id,
                        ConstraintSet.END,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.END
                    )
                    previousSoundItem?.let { previous ->
                        constraintSet.connect(
                            soundView.id,
                            ConstraintSet.START,
                            previous.id,
                            ConstraintSet.END
                        )
                        constraintSet.connect(
                            previous.id,
                            ConstraintSet.END,
                            soundView.id,
                            ConstraintSet.START
                        )
                    }
                }
                else -> {
                    previousSoundItem?.let { previous ->
                        constraintSet.connect(
                            soundView.id,
                            ConstraintSet.START,
                            previous.id,
                            ConstraintSet.END
                        )
                        constraintSet.connect(
                            previous.id,
                            ConstraintSet.END,
                            soundView.id,
                            ConstraintSet.START
                        )
                    }
                    previousSoundItem = soundView
                }
            }
        }
        constraintSet.applyTo(youtube_video_layout)
    }

    fun removeSoundViews(itemView: View) = with(itemView) {
        youtube_video_layout.children.filterIsInstance<CardView>().forEach {
            Timber.i("removeSoundView")
            youtube_video_layout.removeViewInLayout(it)
            viewCache()?.setToCache(it)
        }
    }

    onViewRecycled { removeSoundViews(itemView) }

    bind {
        binding.itemYoutubeVideoThumbnail.loadYoutubeThumbnail(item.videoId)
        binding.itemYoutubeVideoTitle.text = item.title
        removeSoundViews(itemView)
        addIfNeedSoundItems(item)
    }
}