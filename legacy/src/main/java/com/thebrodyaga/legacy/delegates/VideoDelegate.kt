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
import android.widget.TextView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdvancedExercisesVideoItem
import com.thebrodyaga.legacy.ContrastingSoundVideoItem
import com.thebrodyaga.legacy.R
import com.thebrodyaga.legacy.SoundVideoItem
import com.thebrodyaga.legacy.VideoItem
import com.thebrodyaga.legacy.adapters.utils.SoundItemViewCache
import com.thebrodyaga.legacy.color
import com.thebrodyaga.legacy.databinding.ItemYoutubeVideoBinding
import timber.log.Timber

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

    (itemView as ConstraintLayout).apply {
        if (orientation == RecyclerView.VERTICAL) {
            itemView.layoutParams = itemView.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            constraintSet.clone(this)
            constraintSet.constrainHeight(
                binding.youtubeVideoLayout.id,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            constraintSet.applyTo(this)
        }
        setOnClickListener {
            router.navigateTo(
                youtubeScreenFactory.youtubeScreen(
                    PlayVideoExtra(item.videoId, item.title)
                )
            )
        }
        binding.itemYoutubeVideoThumbnail.binding.youtubePlayIcon.isVisible = false
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
            val sound = soundView.findViewById<TextView>(R.id.sound)
            soundView.setOnClickListener { onSoundClick(sound.text.toString()) }
            soundDto.let {
                sound.text = soundDto.transcription
                sound.backgroundTintList = getColor(context, soundDto.soundType.color())
            }
            binding.youtubeVideoLayout.addView(soundView)
        }
        var previousSoundItem: CardView? = null
        val soundViews = binding.youtubeVideoLayout.children.filterIsInstance<CardView>()
        constraintSet.clone(binding.youtubeVideoLayout)

        soundViews.forEachIndexed { index, soundView ->
            constraintSet.setDimensionRatio(soundView.id, "w,1:1")
            constraintSet.constrainHeight(soundView.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainWidth(soundView.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.setHorizontalChainStyle(soundView.id, ConstraintSet.CHAIN_PACKED)
            constraintSet.setHorizontalBias(soundView.id, 0f)
            constraintSet.connect(
                soundView.id,
                ConstraintSet.TOP,
                binding.guideline.id,
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
        constraintSet.applyTo(binding.youtubeVideoLayout)
    }

    fun removeSoundViews(itemView: View) = with(itemView) {
        binding.youtubeVideoLayout.children.filterIsInstance<CardView>().forEach {
            Timber.i("removeSoundView")
            binding.youtubeVideoLayout.removeViewInLayout(it)
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