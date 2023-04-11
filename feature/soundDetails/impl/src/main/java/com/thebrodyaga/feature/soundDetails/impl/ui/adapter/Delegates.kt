package com.thebrodyaga.feature.soundDetails.impl.ui.adapter

import android.view.View
import androidx.core.view.doOnLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.dsl.DslRowAdapterDelegate
import com.thebrodyaga.brandbook.recycler.dsl.rowDelegate
import com.thebrodyaga.feature.soundDetails.impl.R
import com.thebrodyaga.feature.soundDetails.impl.databinding.ItemSoundDetailsDescriptionBinding
import com.thebrodyaga.feature.soundDetails.impl.databinding.ItemSoundDetailsImageBinding
import com.thebrodyaga.feature.soundDetails.impl.databinding.ItemSoundDetailsVideoBinding
import java.io.File

internal val SOUND_DETAILS_IMAGE_LAYOUT_ID = R.layout.item_sound_details_image
internal val SOUND_DETAILS_IMAGE_VIEW_TYPE = SOUND_DETAILS_IMAGE_LAYOUT_ID

fun soundDetailsImageDelegate(
    inflateListener: ((view: View) -> Unit)? = null,
    bindListener: ((view: View, item: SoundDetailsImageUiModel) -> Unit)? = null,
): DslRowAdapterDelegate<SoundDetailsImageUiModel, View> =
    rowDelegate(SOUND_DETAILS_IMAGE_LAYOUT_ID, SOUND_DETAILS_IMAGE_VIEW_TYPE) {
        onBind { holder, _ ->
            val item = holder.item
            val context = holder.view.context
            val binding = ItemSoundDetailsImageBinding.bind(holder.view)
            binding.itemSoundDetailsImage.doOnLayout {
                Glide.with(context)
                    .load(File(context.filesDir, item.photoPath))
                    .override(it.width, it.height)
                    .into(binding.itemSoundDetailsImage)
            }
        }
    }

data class SoundDetailsImageUiModel(
    val photoPath: String
) : UiModel

internal val SOUND_DETAILS_LAYOUT_ID = R.layout.item_sound_details_video
internal val SOUND_DETAILS_VIEW_TYPE = SOUND_DETAILS_LAYOUT_ID

fun soundDetailsVideoDelegate(
    bindListener: ((view: View, item: SoundDetailsVideoUiModel) -> Unit)? = null,
): DslRowAdapterDelegate<SoundDetailsVideoUiModel, View> =
    rowDelegate(SOUND_DETAILS_LAYOUT_ID, SOUND_DETAILS_VIEW_TYPE) {

        onBind { holder, _ ->
            val item = holder.item
            val binding = ItemSoundDetailsVideoBinding.bind(holder.view)
            binding.itemSoundDetailsVideo.loadYoutubeThumbnail(item.videoUrl)
            bindListener?.invoke(binding.root, item)
        }
    }

data class SoundDetailsVideoUiModel(
    val videoUrl: String
) : UiModel

internal val SOUND_DETAILS_DESCRIPTION_LAYOUT_ID = R.layout.item_sound_details_description
internal val SOUND_DETAILS_DESCRIPTION_VIEW_TYPE = SOUND_DETAILS_DESCRIPTION_LAYOUT_ID

fun soundDetailsDescriptionDelegate(
    inflateListener: ((view: View) -> Unit)? = null,
    bindListener: ((view: View, item: SoundDetailsDescriptionUiModel) -> Unit)? = null,
): DslRowAdapterDelegate<SoundDetailsDescriptionUiModel, View> =
    rowDelegate(SOUND_DETAILS_DESCRIPTION_LAYOUT_ID, SOUND_DETAILS_DESCRIPTION_VIEW_TYPE) {

        onBind { holder, _ ->
            val item = holder.item
            val binding = ItemSoundDetailsDescriptionBinding.bind(holder.view)
            binding.itemSoundDetailsVideoDescription.text = item.description
            bindListener?.invoke(binding.root, item)
        }
    }

data class SoundDetailsDescriptionUiModel(
    val description: String
) : UiModel