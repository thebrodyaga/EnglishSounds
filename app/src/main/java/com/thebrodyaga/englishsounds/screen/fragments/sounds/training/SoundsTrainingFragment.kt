package com.thebrodyaga.englishsounds.screen.fragments.sounds.training

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import android.os.Bundle
import android.view.View
import com.thebrodyaga.data.sounds.api.model.PracticeWordDto
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.legacy.VideoListType
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_sounds_training.*
import kotlinx.android.synthetic.main.fragment_word.view.*

class SoundsTrainingFragment : BaseFragment(), SoundsTrainingView {
    override fun getLayoutId(): Int = R.layout.fragment_sounds_training

    @Inject
    @InjectPresenter
    lateinit var presenter: SoundsTrainingPresenter

    @Inject
    lateinit var audioPlayer: AudioPlayer
//    private lateinit var nativeAdLoader: CompositeAdLoader
//    private lateinit var item: ShortAdItem

    @ProvidePresenter
    fun providePresenter() = presenter

    private var adapter: PageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
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
        toolbar.setOnMenuItemClickListener(this)
        play_icon.setRecordVoice(audioPlayer)
        showFab(isShow = true, autoHide = false)
//        include_ad.setAd(item, nativeAdLoader)
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
        (activity as? AppActivity)?.toggleFabMic(isShow = isShow, autoHide = autoHide)
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
