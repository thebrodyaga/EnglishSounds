package com.thebrodyaga.englishsounds.screen.fragments.video

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.list.VideoListFragment
import kotlinx.android.synthetic.main.fragment_all_video.*

class AllVideoFragment : BaseFragment() {

    private val showPage: VideoListType by lazy {
        VideoListType.valueOf(
            arguments?.getString(TYPE_EXTRA) ?: throw IllegalAccessException()
        )
    }

    private val pageList = listOf(
        VideoListType.All, VideoListType.ContrastingSounds, VideoListType.MostCommonWords,
        VideoListType.AdvancedExercises, VideoListType.VowelSounds,
        VideoListType.RControlledVowels, VideoListType.ConsonantSounds
    )

    override fun getLayoutId(): Int = R.layout.fragment_all_video

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tab_layout.setupWithViewPager(pager)
        pager.adapter = VideoPageAdapter()
        pager.setCurrentItem(pageList.indexOf(showPage), false)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private inner class VideoPageAdapter :
        FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment =
            VideoListFragment.newInstance(pageList[position])

        override fun getCount(): Int = pageList.size

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getText(pageList[position].titleRes())
        }
    }

    companion object {

        private const val TYPE_EXTRA = "TYPE_EXTRA"

        fun newInstance(showPage: VideoListType) = AllVideoFragment().apply {
            arguments = Bundle().also { it.putString(TYPE_EXTRA, showPage.name) }
        }
    }
}

enum class VideoListType {
    All, ContrastingSounds, MostCommonWords, AdvancedExercises, VowelSounds, RControlledVowels, ConsonantSounds;

    fun titleRes(): Int = when (this) {
        All -> R.string.all
        ContrastingSounds -> R.string.contrasting_sound_video_title
        MostCommonWords -> R.string.most_common_words_video_title
        AdvancedExercises -> R.string.advanced_exercises_video_title
        VowelSounds -> R.string.vowel_sounds_short
        RControlledVowels -> R.string.r_controlled_vowels_short
        ConsonantSounds -> R.string.consonant_sounds_short
    }
}