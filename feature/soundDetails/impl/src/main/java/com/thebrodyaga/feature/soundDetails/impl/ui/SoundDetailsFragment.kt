package com.thebrodyaga.feature.soundDetails.impl.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.ad.api.adSmallLoadingDelegate
import com.thebrodyaga.ad.google.googleAdDelegate
import com.thebrodyaga.brandbook.component.data.dataViewOnlyLeftDelegate
import com.thebrodyaga.brandbook.component.data.dataViewRightPlayIconDelegate
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.soundDetails.impl.R
import com.thebrodyaga.feature.soundDetails.impl.databinding.FragmentDetailsSoundBinding
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsComponent
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.soundDetailsDescriptionDelegate
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.soundDetailsImageDelegate
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.soundDetailsVideoDelegate
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SoundDetailsFragment : ScreenFragment(R.layout.fragment_details_sound) {

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var soundsDetailsViewPool: SoundsDetailsViewPool
    private val binding by viewBinding(FragmentDetailsSoundBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundViewModel by viewModels { viewModelFactory }

    private val adapter = CommonAdapter(
        delegates = listOf(
            // header
            dataViewOnlyLeftDelegate(),
            dataViewRightPlayIconDelegate(inflateListener = { dataView ->
                dataView.rightSideView.setOnPlayIconClickAction { _, _ ->
                    dataView.callOnClick()
                }
                dataView.setOnClickAction { _, item ->
                    viewModel.onAudioItemClick(item)
                }
            }),
        )
    ) {
        row(googleAdDelegate())
        row(adSmallLoadingDelegate())
        row(soundDetailsImageDelegate())
        row(soundDetailsVideoDelegate { view, item ->
            view.setOnClickListener {
                viewModel.onVideoItemClick(item.videoUrl)
            }
        })
        row(soundDetailsDescriptionDelegate())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val transcription = arguments?.getString(EXTRA)
            ?: throw IllegalArgumentException("need put sound id")

        SoundDetailsComponent.factory(this, transcription).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.soundDetailsList.setRecycledViewPool(soundsDetailsViewPool)
        binding.soundDetailsList.layoutManager = LinearLayoutManager(context)
        binding.soundDetailsList.swapAdapter(adapter, true)
        binding.soundDetailsToolbar.setNavigationOnClickListener { onBackPressed() }
        viewModel.getState()
            .filterIsInstance<SoundState.Content>()
            .onEach { setData(it.list, it.soundDto) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    private fun setData(list: List<UiModel>, soundDto: AmericanSoundDto) {
        binding.soundDetailsToolbar.title = soundDto.name.plus(" ").plus("[${soundDto.transcription}]")
        adapter.items = (list)
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.soundDetailsAppbar.appleTopInsets(this)
                binding.soundDetailsList.appleBottomInsets(this)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        settingManager.onSoundShowed()
    }

    companion object {
        private const val EXTRA = "SoundFragmentExtra"
        fun newInstance(transcription: String): SoundDetailsFragment =
            SoundDetailsFragment().apply {
                arguments = Bundle().also { it.putString(EXTRA, transcription) }
            }
    }
}