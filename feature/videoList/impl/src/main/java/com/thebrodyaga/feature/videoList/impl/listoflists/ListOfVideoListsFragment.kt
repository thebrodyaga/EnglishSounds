package com.thebrodyaga.feature.videoList.impl.listoflists

import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.SoundsListItem
import com.thebrodyaga.legacy.VideoListItem
import com.thebrodyaga.legacy.AdItemDecorator
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentListOfVideoListsBinding
import com.thebrodyaga.feature.videoList.impl.di.VideoListComponent
import com.thebrodyaga.feature.videoList.impl.list.VideoListState
import com.thebrodyaga.feature.videoList.impl.list.VideoListViewModel
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.adapters.SoundsAdapter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ListOfVideoListsFragment : BaseFragment() {

    private lateinit var adapter: SoundsAdapter

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @Inject
    lateinit var soundScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var videoScreenFactory: VideoScreenFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ListOfVideoListsViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentListOfVideoListsBinding::bind)

    override fun getLayoutId(): Int = R.layout.fragment_list_of_video_lists

    override fun onCreate(savedInstanceState: Bundle?) {
        VideoListComponent.factory(findDependencies(), null).inject(this)
        super.onCreate(savedInstanceState)
        adapter = SoundsAdapter(
            viewModel.positionList,
            { soundDto, sharedElements -> onSoundClick(soundDto, sharedElements) },
            { getAnyRouter().navigateTo(soundScreenFactory.soundDetailsScreen(it)) },
            { onShowAllVideoClick(it) },
            lifecycle,
            requireContext(),
            youtubeScreenFactory, getAnyRouter()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        binding.list.itemAnimator = null
        binding.list.addItemDecoration(
            AdItemDecorator(
                context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )
        binding.toolbar.setOnMenuItemClickListener(this)
        viewModel.getState()
            .filterIsInstance<ListOfVideoListsState.Content>()
            .onEach { adapter.setData(it.list) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
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
        getAnyRouter().navigateTo(videoScreenFactory.allVideoScreen(showPage))
    }

    private fun onSoundClick(item: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) {
        getAnyRouter().navigateTo(
            soundScreenFactory.soundDetailsScreen(item.transcription),
        )

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.transcription)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sound")
        AnalyticsEngine.logEvent(
            FirebaseAnalytics.Event.SELECT_CONTENT,
            bundle
        )
    }
}