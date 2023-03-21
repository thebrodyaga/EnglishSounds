package com.thebrodyaga.feature.training.impl

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.training.impl.databinding.FragmentSoundsTrainingBinding
import com.thebrodyaga.feature.training.impl.databinding.FragmentWordBinding
import com.thebrodyaga.feature.training.impl.di.TrainingComponent
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SoundsTrainingFragment : ScreenFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_sounds_training

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var soundDetailsScreenFactory: SoundDetailsScreenFactory
    private val binding by viewBinding(FragmentSoundsTrainingBinding::bind)

    @Inject
    lateinit var videoScreenFactory: VideoScreenFactory
//    private lateinit var nativeAdLoader: CompositeAdLoader
//    private lateinit var item: ShortAdItem

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundsTrainingViewModel by viewModels { viewModelFactory }

    private var adapter: PageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        TrainingComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
        /*nativeAdLoader = CompositeAdLoader(
            requireContext(),
            lifecycle,
            NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE
        )
        item = ShortAdItem(AdTag.SOUND_TRAINING)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playIcon.setRecordVoice(audioPlayer)
        showFab(isShow = true, autoHide = false)
//        include_ad.setAd(item, nativeAdLoader)
        viewModel.getState()
            .filterIsInstance<SoundsTrainingState.Content>()
            .onEach { setData(it.sounds) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showFab(isShow = true, autoHide = true)
//        include_ad?.dispose()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        showFab(isShow = true, autoHide = hidden)
    }

    private fun showFab(isShow: Boolean?, autoHide: Boolean?) {
//        (activity as? AppActivity)?.toggleFabMic(isShow = isShow, autoHide = autoHide)
    }

    private fun setData(list: List<PracticeWordDto>) {
        binding.videoLibIcon.setOnClickListener {
            getAnyRouter().navigateTo(videoScreenFactory.allVideoScreen(VideoListType.MostCommonWords))
        }
        binding.infoIcon.setOnClickListener {
            adapter?.also {
                val practiceWordDto = it.list.getOrNull(binding.viewPager.currentItem)
                practiceWordDto?.apply { getAnyRouter().navigateTo(soundDetailsScreenFactory.soundDetailsScreen(this.sound)) }
            }
        }
        adapter = PageAdapter(this).also {
            it.setData(list)
            binding.viewPager.adapter = it
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.playIcon.audioFile =
                        File(binding.viewPager.context.filesDir, it.list[position].audioPath)
                }
            })
        }
    }

    private val onInfoClick = View.OnClickListener {
        adapter?.also {
            val practiceWordDto = it.list.getOrNull(binding.viewPager.currentItem)
            practiceWordDto?.apply { getAnyRouter().navigateTo(soundDetailsScreenFactory.soundDetailsScreen(this.sound)) }
        }
    }

    private class PageAdapter(fragment: ScreenFragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment =
            WordFragment.newInstance(list[position].name)

        var list = listOf<PracticeWordDto>()

        override fun getItemCount(): Int = list.size

        fun setData(list: List<PracticeWordDto>) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    class WordFragment : ScreenFragment() {
        override fun getLayoutId(): Int = R.layout.fragment_word
        private val binding by viewBinding(FragmentWordBinding::bind)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.word.text = arguments?.getString(WORD_KEY) ?: ""
        }

        companion object {
            private const val WORD_KEY = "WORD_KEY"
            fun newInstance(word: String): WordFragment = WordFragment()
                .apply { arguments = Bundle().also { it.putString(WORD_KEY, word) } }
        }
    }
}
