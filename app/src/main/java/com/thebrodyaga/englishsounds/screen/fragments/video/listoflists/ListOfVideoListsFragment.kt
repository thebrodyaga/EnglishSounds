package com.thebrodyaga.englishsounds.screen.fragments.video.listoflists

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType
import com.thebrodyaga.englishsounds.domine.entities.ui.*
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.navigation.TransitionBox
import com.thebrodyaga.englishsounds.screen.adapters.SoundsAdapter
import com.thebrodyaga.englishsounds.screen.adapters.decorator.AdItemDecorator
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import kotlinx.android.synthetic.main.fragment_all_video.toolbar
import kotlinx.android.synthetic.main.fragment_list_of_video_lists.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class ListOfVideoListsFragment : BaseFragment(), ListOfVideoListsView {

    private lateinit var adapter: SoundsAdapter

    @Inject
    @InjectPresenter
    lateinit var presenter: ListOfVideoListsPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun getLayoutId(): Int = R.layout.fragment_list_of_video_lists

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        adapter = SoundsAdapter(
            presenter.positionList,
            { soundDto, sharedElements -> onSoundClick(soundDto, sharedElements) },
            { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(it)) },
            { onShowAllVideoClick(it) },
            lifecycle,
            requireContext()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        list.itemAnimator = null
        list.addItemDecoration(
            AdItemDecorator(
                context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun setListData(videos: List<SoundsListItem>) {
        adapter.setData(videos)
    }

    private fun onShowAllVideoClick(videoListItem: VideoListItem) {
        val showPage: VideoListType = when (videoListItem) {
            is ContrastingSoundVideoListItem -> VideoListType.ContrastingSounds
            is MostCommonWordsVideoListItem -> VideoListType.MostCommonWords
            is AdvancedExercisesVideoListItem -> VideoListType.AdvancedExercises
            is SoundVideoListItem -> when (videoListItem.soundType) {
                SoundType.CONSONANT_SOUND -> VideoListType.ConsonantSounds
                SoundType.R_CONTROLLED_VOWELS -> VideoListType.RControlledVowels
                SoundType.VOWEL_SOUNDS -> VideoListType.VowelSounds
            }
        }
        getAnyRouter().navigateTo(Screens.AllVideoScreen(showPage))
    }

    private fun onSoundClick(item: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) {
        getAnyRouter().navigateToWithTransition(
            Screens.SoundsDetailsScreen(item.transcription),
            TransitionBox(
                null,
                null,
                null
            ),
            *sharedElements
        )

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.transcription)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sound")
        (activity as? AppActivity)?.firebaseAnalytics?.logEvent(
            FirebaseAnalytics.Event.SELECT_CONTENT,
            bundle
        )
    }
}