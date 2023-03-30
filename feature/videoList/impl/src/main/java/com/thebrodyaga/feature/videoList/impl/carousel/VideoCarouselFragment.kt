package com.thebrodyaga.feature.videoList.impl.carousel

import android.os.Bundle
import android.util.SparseIntArray
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentVideoCarouselBinding
import com.thebrodyaga.feature.videoList.impl.di.VideoListComponent
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdItemDecorator
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoListItem
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class VideoCarouselFragment : ScreenFragment(R.layout.fragment_video_carousel) {

    private val adapter = CommonAdapter(
        delegates = listOf(
            videoCarouselDelegate(
                listPosition = SparseIntArray(),
                shadowRecyclerHeight = { binding.videoCarouselShadowList.height }
            ),
            dataViewCommonDelegate()
        )
    )

    private val shadowList = listOf<UiModel>(VideoCarouselItemUiModel("", ""))
    private val shadowAdapter = CommonAdapter(
        delegates = listOf(videoCarouselItemDelegate())
    )

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @Inject
    lateinit var soundScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var videoScreenFactory: VideoScreenFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ListOfVideoListsViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentVideoCarouselBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        VideoListComponent.factory(this, null).inject(this)
        super.onCreate(savedInstanceState)
        /*adapter = SoundsAdapter(
            viewModel.positionList,
            { soundDto, sharedElements -> onSoundClick(soundDto, sharedElements) },
            { getAnyRouter().navigateTo(soundScreenFactory.soundDetailsScreen(it)) },
            { onShowAllVideoClick(it) },
            lifecycle,
            requireContext(),
            youtubeScreenFactory, getAnyRouter()
        )*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.videoCarouselShadowList.adapter = shadowAdapter
        shadowAdapter.items = shadowList
        binding.videoCarouselList.adapter = adapter
        binding.videoCarouselList.itemAnimator = null
        binding.videoCarouselList.addItemDecoration(
            AdItemDecorator(
                context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )
        viewModel.getState()
            .filterIsInstance<ListOfVideoListsState.Content>()
            .onEach { adapter.items = (it.list) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.videoCarouselAppbar.appleTopInsets(this)
                binding.videoCarouselList.appleBottomInsets(this)
            }
        }
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