package com.thebrodyaga.feature.soundDetails.impl.ui

import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.core.uiUtils.appbarBottomPadding
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.soundDetails.impl.R
import com.thebrodyaga.feature.soundDetails.impl.databinding.FragmentSoundBinding
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsComponent
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SoundFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_sound

    @Inject
    lateinit var soundsRepository: SoundsRepository
    @Inject
    lateinit var audioPlayer: AudioPlayer
    @Inject
    lateinit var settingManager: SettingManager
    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory
    private val binding by viewBinding(FragmentSoundBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: SoundDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val transcription = arguments?.getString(EXTRA)
            ?: throw IllegalArgumentException("need put sound id")

        SoundDetailsComponent.factory(findDependencies(), transcription).inject(this)
        adapter = SoundDetailsAdapter(getAnyRouter(), youtubeScreenFactory, audioPlayer, requireContext(), lifecycle)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        binding.list.itemAnimator = SlideInUpAnimator().apply { addDuration = 300 }
        ViewCompat.setTransitionName(binding.rootView, viewModel.transcription)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.list.appbarBottomPadding()
        viewModel.getState()
            .filterIsInstance<SoundState.Content>()
            .onEach { setData(it.list, it.soundDto) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    fun setData(list: List<Any>, soundDto: AmericanSoundDto) {
        binding.toolbarTitle.text = soundDto.name.plus(" ").plus("[${soundDto.transcription}]")
        binding.rootView.post { adapter.setData(list) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        settingManager.onSoundShowed()
    }

    companion object {
        private const val EXTRA = "SoundFragmentExtra"
        fun newInstance(transcription: String): SoundFragment =
            SoundFragment().apply {
                arguments = Bundle().also { it.putString(EXTRA, transcription) }
            }
    }
}