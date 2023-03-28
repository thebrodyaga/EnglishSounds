package com.thebrodyaga.feature.soundDetails.impl.ui.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.feature.soundDetails.impl.databinding.ItemSoundDetailsDescriptionBinding
import com.thebrodyaga.feature.soundDetails.impl.databinding.ItemSoundDetailsImageBinding
import com.thebrodyaga.feature.soundDetails.impl.databinding.ItemSoundDetailsVideoBinding
import java.io.File

fun soundDetailsImageDelegate(
    inflateListener: ((view: View) -> Unit)? = null,
    bindListener: ((view: View, item: SoundDetailsImageUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<SoundDetailsImageUiModel, UiModel, ItemSoundDetailsImageBinding>(
        viewBinding = { inflater, parent -> ItemSoundDetailsImageBinding.inflate(inflater, parent, false) }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            Glide.with(context)
                .load(File(context.filesDir, item.photoPath))
                .into(binding.itemSoundDetailsImage)
            bindListener?.invoke(binding.root, item)
        }
    }

data class SoundDetailsImageUiModel(
    val photoPath: String
) : UiModel

fun soundDetailsVideoDelegate(
    inflateListener: ((view: View) -> Unit)? = null,
    bindListener: ((view: View, item: SoundDetailsVideoUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<SoundDetailsVideoUiModel, UiModel, ItemSoundDetailsVideoBinding>(
        viewBinding = { inflater, parent -> ItemSoundDetailsVideoBinding.inflate(inflater, parent, false) }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            binding.itemSoundDetailsVideo.loadYoutubeThumbnail(item.videoUrl)
            bindListener?.invoke(binding.root, item)
        }
    }

data class SoundDetailsVideoUiModel(
    val videoUrl: String
) : UiModel

fun soundDetailsDescriptionDelegate(
    inflateListener: ((view: View) -> Unit)? = null,
    bindListener: ((view: View, item: SoundDetailsDescriptionUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<SoundDetailsDescriptionUiModel, UiModel, ItemSoundDetailsDescriptionBinding>(
        viewBinding = { inflater, parent -> ItemSoundDetailsDescriptionBinding.inflate(inflater, parent, false) }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            binding.itemSoundDetailsVideoDescription.text = item.description
            bindListener?.invoke(binding.root, item)
        }
    }

data class SoundDetailsDescriptionUiModel(
    val description: String
) : UiModel