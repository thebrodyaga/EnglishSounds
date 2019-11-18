package com.thebrodyaga.englishsounds.screen.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundHeader
import kotlinx.android.synthetic.main.item_sound.view.*
import kotlinx.android.synthetic.main.item_sound_header.view.*
import java.lang.IllegalArgumentException
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem
import timber.log.Timber

class SoundsAdapter constructor(
    private val onCardSoundClick: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = listOf<SoundsListItem>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SOUND_VIEW -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_sound,
                    parent,
                    false
                )
            )
            SOUND_HEADER_VIEW -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_sound_header,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("хуйня в onCreateViewHolder")
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {
            is AmericanSoundDto -> (holder as ViewHolder).bind(item)
            is SoundHeader -> (holder as HeaderViewHolder).bind(item)
            else -> throw IllegalArgumentException("хуйня в onBindViewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is AmericanSoundDto -> SOUND_VIEW
            is SoundHeader -> SOUND_HEADER_VIEW
            else -> throw IllegalArgumentException("хуйня в getItemViewType")
        }
    }

    fun setData(newData: List<SoundsListItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newData.toList()))
        list = newData
        diffResult.dispatchUpdatesTo(this)
//        list  = newData
//        notifyDataSetChanged()
    }

    companion object {
        const val SOUND_VIEW = 0
        const val SOUND_HEADER_VIEW = 1
    }

    private val soundsBackgroundColors =
        mutableMapOf<@ColorRes Int, ColorStateList>()

    fun getColor(context: Context, @ColorRes colorRes: Int): ColorStateList? {
        return soundsBackgroundColors[colorRes]
            ?: ContextCompat.getColorStateList(context, colorRes)
                ?.also { soundsBackgroundColors[colorRes] = it }
    }

    private var singleCharMaxWight: Float? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val mPaint: Paint = Paint()
        var font: Typeface? = null
        fun font(context: Context): Typeface {
            return font ?: ResourcesCompat.getFont(context, R.font.font_regular)
                .also { font = it }
            ?: throw Resources.NotFoundException()

        }
        mPaint.isAntiAlias = true
        mPaint.textSize = recyclerView.context.resources.getDimension(R.dimen.bodySize)
        mPaint.typeface = font(recyclerView.context)
        singleCharMaxWight = mPaint.measureText("a")
    }

    private inner class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(v: View) {
            val pair = Pair(itemView, ViewCompat.getTransitionName(v.sound_root_view) ?: "")
            item?.also { onCardSoundClick.invoke(it, arrayOf(pair)) }        }

        private var item: AmericanSoundDto? = null

        init {
            view.sound_root_view.setOnClickListener(this)
        }

        fun bind(soundDto: AmericanSoundDto) = with(itemView) {
            ViewCompat.setTransitionName(sound_root_view, soundDto.transcription)
            item = soundDto
            val maxWordLength = word.width / (singleCharMaxWight?.toInt() ?: 1)
            sound.text = soundDto.transcription
            word.text = soundDto.spellingWordList
                .filter { it.name.length <= maxWordLength }
                .let {
                    if (it.isNotEmpty())
                        it.random()
                    else soundDto.spellingWordList
                        .minBy { word -> word.name.length }
                }?.name
            sound.backgroundTintList = getColor(context, soundDto.soundType.color())
        }
    }

    private inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(soundHeader: SoundHeader) = with(itemView) {
            title.setText(soundHeader.soundType.humanName())
        }
    }

    class DiffCallback constructor(
        private var oldList: List<SoundsListItem>,
        private var newList: List<SoundsListItem>
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
                oldItem is AmericanSoundDto && newItem is AmericanSoundDto ->
                    oldItem.transcription == newItem.transcription
                oldItem is SoundHeader && newItem is SoundHeader ->
                    oldItem.soundType == newItem.soundType
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is AmericanSoundDto && newItem is AmericanSoundDto ->
                    oldItem == newItem
                oldItem is SoundHeader && newItem is SoundHeader ->
                    oldItem == newItem
                else -> false
            }
        }
    }
}