package com.thebrodyaga.legacy.delegates

import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.thebrodyaga.core.uiUtils.inflate
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.legacy.R

abstract class SoundItemDelegate(
    private val itemClickedListener: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    private val singleCharMaxWight: () -> Float?
) : AdapterDelegate<List<Any>>() {

    override fun onBindViewHolder(
        items: List<Any>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) = (holder as SoundItemVH).bind(items[position] as AmericanSoundDto)

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        SoundItemVH(parent.inflate(getLayout()), itemClickedListener, singleCharMaxWight)

    @LayoutRes
    abstract fun getLayout(): Int
}

class ConsonantSoundsDelegate constructor(
    itemClickedListener: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    singleCharMaxWight: () -> Float?
) : SoundItemDelegate(itemClickedListener, singleCharMaxWight) {

    override fun getLayout(): Int = R.layout.item_sound_consonat

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        (items[position] as? AmericanSoundDto)?.soundType == SoundType.CONSONANT_SOUND
}

class VowelSoundsDelegate constructor(
    itemClickedListener: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    singleCharMaxWight: () -> Float?
) : SoundItemDelegate(itemClickedListener, singleCharMaxWight) {

    override fun getLayout(): Int = R.layout.item_sound_vowel

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        (items[position] as? AmericanSoundDto)?.soundType == SoundType.VOWEL_SOUNDS
}

class RControlledSoundsDelegate constructor(
    itemClickedListener: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    singleCharMaxWight: () -> Float?
) : SoundItemDelegate(itemClickedListener, singleCharMaxWight) {

    override fun getLayout(): Int = R.layout.item_sound_r_controlled

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        (items[position] as? AmericanSoundDto)?.soundType == SoundType.R_CONTROLLED_VOWELS
}

class SoundItemVH constructor(
    view: View,
    private val itemClickedListener: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    private val singleCharMaxWight: () -> Float?
) : RecyclerView.ViewHolder(view) {

    var item: AmericanSoundDto? = null
    private val word = view.findViewById<TextView>(R.id.word)
    private val sound = view.findViewById<TextView>(R.id.sound)

    init {
        itemView.setOnClickListener {
            val pair = Pair(itemView, ViewCompat.getTransitionName(itemView) ?: "")
            item?.also { itemClickedListener(it, arrayOf(pair)) }
        }
    }

    fun bind(item: AmericanSoundDto) = with(itemView) {
        this@SoundItemVH.item = item
        ViewCompat.setTransitionName(itemView, item.transcription)
        val maxWordLength = word.width / (singleCharMaxWight()?.toInt() ?: 1)
        sound.text = item.transcription
        word.text = item.spellingWordList.filter { it.name.length <= maxWordLength }.let {
            if (it.isNotEmpty()) it.random()
            else item.spellingWordList.minBy { word -> word.name.length }
        }?.name
    }
}