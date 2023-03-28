package com.thebrodyaga.feature.videoList.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentAllVideoBinding
import com.thebrodyaga.feature.videoList.impl.list.VideoListFragment
import com.thebrodyaga.legacy.titleRes

class AllVideoFragment : ScreenFragment(R.layout.fragment_all_video) {

    private val showPage: VideoListType by lazy {
        VideoListType.valueOf(
            arguments?.getString(TYPE_EXTRA) ?: throw IllegalAccessException()
        )
    }

    private val pageList = listOf(
        VideoListType.ContrastingSounds, VideoListType.MostCommonWords,
        VideoListType.AdvancedExercises, VideoListType.VowelSounds,
        VideoListType.RControlledVowels, VideoListType.ConsonantSounds
    )
    private val binding by viewBinding(FragmentAllVideoBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayout.setupWithViewPager(binding.pager)
        binding.pager.adapter = VideoPageAdapter()
        binding.pager.setCurrentItem(pageList.indexOf(showPage), false)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
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

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            val systemAndIme = insets.systemAndIme()
            binding.appbar.appleTopInsets(systemAndIme)
            insets
        }
    }

    companion object {

        private const val TYPE_EXTRA = "TYPE_EXTRA"

        fun newInstance(showPage: VideoListType) = AllVideoFragment().apply {
            arguments = Bundle().also { it.putString(TYPE_EXTRA, showPage.name) }
        }
    }
}