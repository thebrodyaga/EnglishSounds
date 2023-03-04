package com.thebrodyaga.feature.soundList.impl

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.base.app.BaseFragment
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
import com.thebrodyaga.legacy.adapters.SoundsAdapter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class SoundsListFragment : BaseFragment(), SoundsListView {

    override fun getLayoutId(): Int = R.layout.fragment_sounds_list

    @Inject
    @InjectPresenter
    lateinit var presenter: SoundsListPresenter
    private val binding by viewBinding(FragmentSoundsListBinding::bind)

    @Inject
    lateinit var detailsScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @ProvidePresenter
    fun providePresenter() = presenter

    private lateinit var adapter: SoundsAdapter
    private lateinit var spanSizeLookup: SpanSizeLookup

    override fun onCreate(savedInstanceState: Bundle?) {
        SoundListComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
        adapter = SoundsAdapter(
            presenter.positionList,
            { soundDto, sharedElements -> onSoundClick(soundDto, sharedElements) },
            { getAnyRouter().navigateTo(detailsScreenFactory.soundDetailsScreen(it)) },
            { onShowAllVideoClick(it) },
            lifecycle,
            requireContext(),
            youtubeScreenFactory,
            getAnyRouter(),
        )
        spanSizeLookup =
            SpanSizeLookup(
                adapter,
                calculateNoOfColumns(requireContext(), R.dimen.card_sound_width)
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.list.layoutManager = GridLayoutManager(context, spanSizeLookup.maxColumns)
            .also { it.spanSizeLookup = spanSizeLookup }
        binding.list.adapter = adapter
        binding.list.itemAnimator = null
        binding.list.addItemDecoration(
            AdItemDecorator(
                context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )
        binding.toolbar.setOnMenuItemClickListener(this)
    }

    override fun setListData(sounds: List<Any>) {
        adapter.setData(sounds)
    }

    private fun onSoundClick(item: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) {
        getAnyRouter().navigateTo(
            detailsScreenFactory.soundDetailsScreen(item.transcription),
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

    private class SpanSizeLookup constructor(
        private val adapter: SoundsAdapter,
        val maxColumns: Int
    ) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            //getItemViewType равен индексу добавления в delegatesManager адаптера
            return when (adapter.getItemViewType(position)) {
                0, 1, 2, 3 -> maxColumns
                else -> 1
            }
        }
    }
}
