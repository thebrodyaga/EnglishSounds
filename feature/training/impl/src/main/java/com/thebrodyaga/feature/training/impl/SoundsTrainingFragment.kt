package com.thebrodyaga.feature.training.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.core.uiUtils.insets.*
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayerState
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.training.impl.databinding.FragmentSoundsTrainingBinding
import com.thebrodyaga.feature.training.impl.databinding.FragmentWordBinding
import com.thebrodyaga.feature.training.impl.di.TrainingComponent
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

class SoundsTrainingFragment : ScreenFragment(R.layout.fragment_sounds_training) {

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var soundDetailsScreenFactory: SoundDetailsScreenFactory
    private val binding by viewBinding(FragmentSoundsTrainingBinding::bind)

    @Inject
    lateinit var videoScreenFactory: VideoScreenFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundsTrainingViewModel by viewModels { viewModelFactory }

    private var adapter: PageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        TrainingComponent.factory(this).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getState()
            .filterIsInstance<SoundsTrainingState.Content>()
            .onEach { setData(it.sounds) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.appbar.appleTopInsets(this)
                binding.trainingContentLayout.appleInsetPadding(this, bottom = this.bottom)
            }
        }
    }

    var playingJob: Job? = null
    // refactoring maybe later
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
        var currentWordAudio: File? = null
        binding.playIcon.setOnClickListener {
            currentWordAudio?.let { file ->
                playingJob?.cancel()
                playingJob = audioPlayer.playAudio(file)
                    .onEach {
                        when (it) {
                            is AudioPlayerState.Idle -> binding.playIcon.pauseToPlay()
                            is AudioPlayerState.Playing ->
                                if (it.audioFile.path != file.path) binding.playIcon.pauseToPlay()
                                else binding.playIcon.playToPause()
                        }
                    }
                    .flowWithLifecycle(lifecycle)
                    .launchIn(lifecycleScope)
            }
        }

        adapter = PageAdapter(this).also {
            it.setData(list)
            binding.viewPager.adapter = it
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentWordAudio = File(binding.viewPager.context.filesDir, list[position].audioPath)
                }
            })
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

    class WordFragment : ScreenFragment(R.layout.fragment_word) {
        private val binding by viewBinding(FragmentWordBinding::bind)

        override fun applyWindowInsets(rootView: View) = Unit

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
