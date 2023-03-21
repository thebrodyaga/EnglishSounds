package com.thebrodyaga.feature.soundList.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.component.sound.SoundCardUiModel
import com.thebrodyaga.brandbook.component.sound.soundCardDelegate
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
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
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundList.impl.databinding.FragmentSoundsListBinding
import com.thebrodyaga.feature.soundList.impl.di.SoundListComponent
import com.thebrodyaga.feature.videoList.api.VideoListType
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

class SoundsListFragment : ScreenFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_sounds_list

    private val binding by viewBinding(FragmentSoundsListBinding::bind)

    @Inject
    lateinit var detailsScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var routerProvider: RouterProvider

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundsListViewModel by viewModels { viewModelFactory }

    private val maxColumns: Int by lazy { calculateNoOfColumns(requireContext(), R.dimen.card_sound_width) }
    private var spanSizeLookup = SpanSizeLookup()
    private var adapter = CommonAdapter(
        soundCardDelegate(inflateListener = {
            it.setOnClickAction { _, item ->
                onSoundClick(item)
            }
        }),
        dataViewCommonDelegate(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        SoundListComponent.factory(this).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.list.layoutManager = GridLayoutManager(context, maxColumns)
            .also { it.spanSizeLookup = spanSizeLookup }
        binding.list.adapter = adapter
        binding.list.itemAnimator = null
        binding.list.addItemDecoration(
            AdItemDecorator(
                context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )

        viewModel.getState()
            .filterIsInstance<SoundsListState.Content>()
            .onEach { adapter.items = it.sounds }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.appbar.appleTopInsets(this)
                binding.list.appleBottomInsets(this)
            }
        }
    }

    private fun onSoundClick(model: SoundCardUiModel) {
        val sound = model.payload as? AmericanSoundDto ?: return
        getAnyRouter().navigateTo(
            detailsScreenFactory.soundDetailsScreen(sound.transcription),
        )

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, sound.transcription)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, sound.name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sound")
        AnalyticsEngine.logEvent(
            FirebaseAnalytics.Event.SELECT_CONTENT,
            bundle
        )
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
        // todo
//        getAnyRouter().navigateTo(Screens.AllVideoScreen(showPage))
    }

    private inner class SpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            //getItemViewType равен индексу добавления в delegatesManager адаптера
            return when (adapter.getItemViewType(position)) {
                1 -> maxColumns
                else -> 1
            }
        }
    }
}
