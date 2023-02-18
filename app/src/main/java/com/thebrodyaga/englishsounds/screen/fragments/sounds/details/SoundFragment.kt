package com.thebrodyaga.englishsounds.screen.fragments.sounds.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.thebrodyaga.data.sounds.api.SettingManager
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.screen.adapters.SoundDetailsAdapter
import com.thebrodyaga.englishsounds.screen.appbarBottomPadding
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
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

    private lateinit var adapter: SoundDetailsAdapter

    @ProvidePresenter
    fun providePresenter() = presenter.also {
        it.transcription = arguments?.getString(EXTRA)
            ?: throw IllegalArgumentException("need put sound id")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        adapter = SoundDetailsAdapter(audioPlayer, requireContext(), lifecycle)
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
        (activity as? AppActivity)?.toggleFabMic(true, autoHide = true)
    }

    @SuppressLint("InflateParams")
    override fun setData(list: List<Any>, soundDto: AmericanSoundDto) {
        toolbar_title.text = soundDto.name.plus(" ").plus("[${soundDto.transcription}]")
        root_view.post { adapter.setData(list) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        (activity as? AppActivity)?.also {
            it.onSoundScreenClose()
            settingManager.onSoundShowed()
        }
    }

    companion object {
        private const val EXTRA = "SoundFragmentExtra"
        fun newInstance(transcription: String): SoundFragment =
            SoundFragment().apply {
                arguments = Bundle().also { it.putString(EXTRA, transcription) }
            }
    }
}