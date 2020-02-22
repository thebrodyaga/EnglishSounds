package com.thebrodyaga.englishsounds.screen.fragments.sounds.details


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsDetailsListItem
import com.thebrodyaga.englishsounds.repository.SoundsRepository
import com.thebrodyaga.englishsounds.screen.adapters.SoundDetailsAdapter
import com.thebrodyaga.englishsounds.screen.appbarBottomPadding
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.dialogs.RateAppDialog
import com.thebrodyaga.englishsounds.screen.getVideoAndDescription
import com.thebrodyaga.englishsounds.tools.AudioPlayer
import com.thebrodyaga.englishsounds.tools.SettingManager
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.fragment_sound.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
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
        adapter = SoundDetailsAdapter(audioPlayer)
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
    override fun setData(list: List<SoundsDetailsListItem>, soundDto: AmericanSoundDto) {
        val context = toolbar_title.context
        toolbar_title.text = soundDto.name.plus(" ").plus("[${soundDto.transcription}]")
        // https://github.com/PierfrancescoSoffritti/android-youtube-player/issues/461#issuecomment-550050683
        val contextForYoutubeView =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context
            else context.applicationContext
        val youTubePlayerView =
            LayoutInflater.from(contextForYoutubeView).inflate(R.layout.view_youtube_player, null)
                    as YouTubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        val videoId = context.getVideoAndDescription().first[soundDto.transcription]
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                presenter.videoSecond = second
            }

            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                videoId?.also {
                    if (presenter.videoSecond > 0)
                        youTubePlayer.loadVideo(it, presenter.videoSecond)
                    else youTubePlayer.cueVideo(it, presenter.videoSecond)
                }
            }
        })
        Glide.with(sound_image.context)
            .load(File(sound_image.context.filesDir, soundDto.photoPath))
            .into(sound_image)
        root_view.post { adapter.setData(list, youTubePlayerView) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        (activity as? AppActivity)?.also {
            it.onSoundScreenClose()
            settingManager.onSoundShowed()
        }
    }

    private class PageAdapter constructor(
        fragment: BaseFragment,
        val image: String,
        val video: String
    ) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> VideoFragment.newInstance(video)
                1 -> ImageFragment.newInstance(image)
                else -> throw IllegalArgumentException("что за хуйня?")
            }

        override fun getItemCount(): Int = 2
    }

    companion object {
        private const val EXTRA = "SoundFragmentExtra"
        fun newInstance(transcription: String): SoundFragment =
            SoundFragment().apply {
                arguments = Bundle()
                    .also { it.putString(EXTRA, transcription) }
            }
    }
}
