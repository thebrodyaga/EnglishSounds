package com.thebrodyaga.feature.videoList.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.insets.systemAndImeInsetType
import com.thebrodyaga.core.uiUtils.insets.updateInsets
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentAllVideoBinding
import com.thebrodyaga.feature.videoList.impl.page.VideoListPageFragment
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
        val adapter = VideoPageAdapter()
        binding.pager.adapter = adapter
        binding.pager.setCurrentItem(pageList.indexOf(showPage), false)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = resources.getText(pageList[position].titleRes())
        }.attach()
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private inner class VideoPageAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = pageList.size

        override fun createFragment(position: Int): Fragment =
            VideoListPageFragment.newInstance(pageList[position])
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            val systemAndIme = insets.systemAndIme()
            val notConsumedInsets = binding.appbar.appleTopInsets(systemAndIme)
            insets.updateInsets {
                setInsets(systemAndImeInsetType, notConsumedInsets)
            }
        }
    }

    companion object {

        private const val TYPE_EXTRA = "TYPE_EXTRA"

        fun newInstance(showPage: VideoListType) = AllVideoFragment().apply {
            arguments = Bundle().also { it.putString(TYPE_EXTRA, showPage.name) }
        }
    }
}