package com.thebrodyaga.feature.soundDetails.impl.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.thebrodyaga.core.uiUtils.appbarBottomPadding
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.soundDetails.impl.R
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsComponent
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.fragment_sound.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class SoundFragment : BaseFragment(), SoundView {
    override fun getLayoutId(): Int = R.layout.fragment_sound

    @Inject
    @InjectPresenter
    lateinit var presenter: SoundPresenter
    @Inject
    lateinit var soundsRepository: SoundsRepository
    @Inject
    lateinit var audioPlayer: AudioPlayer
    @Inject
    lateinit var settingManager: SettingManager
    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    private lateinit var adapter: SoundDetailsAdapter

    @ProvidePresenter
    fun providePresenter() = presenter.also {
        it.transcription = arguments?.getString(EXTRA)
            ?: throw IllegalArgumentException("need put sound id")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SoundDetailsComponent.factory(findDependencies()).inject(this)
        adapter = SoundDetailsAdapter(getAnyRouter(), youtubeScreenFactory, audioPlayer, requireContext(), lifecycle)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        list.itemAnimator = SlideInUpAnimator().apply { addDuration = 300 }
        ViewCompat.setTransitionName(root_view, presenter.transcription)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        list.appbarBottomPadding()
    }

    @SuppressLint("InflateParams")
    override fun setData(list: List<Any>, soundDto: AmericanSoundDto) {
        toolbar_title.text = soundDto.name.plus(" ").plus("[${soundDto.transcription}]")
        root_view.post { adapter.setData(list) }
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