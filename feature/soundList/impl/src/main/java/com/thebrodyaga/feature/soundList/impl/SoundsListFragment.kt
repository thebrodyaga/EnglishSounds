package com.thebrodyaga.feature.soundList.impl

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.base.navigation.impl.transition.sharedElementBox
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.component.sound.SoundCardUiModel
import com.thebrodyaga.brandbook.component.sound.SoundCardView
import com.thebrodyaga.brandbook.component.sound.soundCardDelegate
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
import com.thebrodyaga.core.uiUtils.insets.*
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundList.impl.databinding.FragmentSoundsListBinding
import com.thebrodyaga.feature.soundList.impl.di.SoundListComponent
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SoundsListFragment : ScreenFragment(R.layout.fragment_sounds_list) {

    private val binding by viewBinding(FragmentSoundsListBinding::bind)

    @Inject
    lateinit var detailsScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @Inject
    lateinit var viewPool: SoundsListViewPool

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundsListViewModel by viewModels { viewModelFactory }

    private val maxColumns: Int by lazy { calculateNoOfColumns(requireContext(), R.dimen.card_sound_width) }
    private var spanSizeLookup = SpanSizeLookup()
    private var adapter = CommonAdapter(
        delegates = listOf(dataViewCommonDelegate()),
    ) {
        row(soundCardDelegate {
            onBind { holder, payloads ->
                val view = holder.view
                val item = holder.item
                val shaderView = view.contentLayout
                view.setOnClickAction { _, _ ->
                    onSoundClick(item, shaderView)
                }
                ViewCompat.setTransitionName(
                    shaderView,
                    (item.transcription as TextViewUiModel.Raw).text.toString()
                )
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SoundListComponent.factory(this).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.list.setRecycledViewPool(viewPool)
        binding.list.layoutManager = GridLayoutManager(context, maxColumns)
            .also { it.spanSizeLookup = spanSizeLookup }
        binding.list.swapAdapter(adapter, true)
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

    private fun onSoundClick(
        model: SoundCardUiModel,
        view: View,
    ) {
        val sound = model.payload as? AmericanSoundDto ?: return
        getAnyRouter().navigateTo(
            detailsScreenFactory.soundDetailsScreen(sound.transcription, view.sharedElementBox),
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
            return when (adapter.getItemViewType(position)) {
                SoundCardView.VIEW_TYPE -> 1
                else -> maxColumns
            }
        }
    }
}
