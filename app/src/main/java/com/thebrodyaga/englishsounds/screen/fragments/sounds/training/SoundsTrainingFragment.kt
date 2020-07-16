package com.thebrodyaga.englishsounds.screen.fragments.sounds.training

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.domine.entities.data.PracticeWordDto
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
import com.thebrodyaga.englishsounds.tools.AudioPlayer
import kotlinx.android.synthetic.main.fragment_sounds_training.*
import kotlinx.android.synthetic.main.fragment_word.view.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import javax.inject.Inject

class SoundsTrainingFragment : BaseFragment(), SoundsTrainingView {
    override fun getLayoutId(): Int = R.layout.fragment_sounds_training

    @Inject
    @InjectPresenter
    lateinit var presenter: SoundsTrainingPresenter

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @ProvidePresenter
    fun providePresenter() = presenter

    private var adapter: PageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnMenuItemClickListener(this)
        play_icon.setRecordVoice(audioPlayer)
        view.post { (activity as? AppActivity)?.toggleFabMic(true) }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            (activity as? AppActivity)?.toggleFabMic(true)
    }

    override fun setData(list: List<PracticeWordDto>) {
        video_lib_icon.setOnClickListener {
            getAnyRouter().navigateTo(Screens.AllVideoScreen(VideoListType.MostCommonWords))
        }
        info_icon.setOnClickListener {
            adapter?.also {
                val practiceWordDto = it.list.getOrNull(view_pager.currentItem)
                practiceWordDto?.apply { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(this.sound)) }
            }
        }
        adapter = PageAdapter(this).also {
            it.setData(list)
            view_pager.adapter = it
            view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    play_icon.audioFile =
                        File(view_pager.context.filesDir, it.list[position].audioPath)

                }
            })
        }
    }

    private val onInfoClick = View.OnClickListener {
        adapter?.also {
            val practiceWordDto = it.list.getOrNull(view_pager.currentItem)
            practiceWordDto?.apply { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(this.sound)) }
        }
    }

    private class PageAdapter(fragment: BaseFragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment =
            WordFragment.newInstance(list[position].name)

        var list = listOf<PracticeWordDto>()

        override fun getItemCount(): Int = list.size

        fun setData(list: List<PracticeWordDto>) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    class WordFragment : BaseFragment() {
        override fun getLayoutId(): Int = R.layout.fragment_word

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            view.word.text = arguments?.getString(WORD_KEY) ?: ""
        }


        companion object {
            private const val WORD_KEY = "WORD_KEY"
            fun newInstance(word: String): WordFragment = WordFragment()
                .apply { arguments = Bundle().also { it.putString(WORD_KEY, word) } }

        }
    }
}
