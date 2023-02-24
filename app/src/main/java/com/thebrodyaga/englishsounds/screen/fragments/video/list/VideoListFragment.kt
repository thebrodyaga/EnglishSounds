package com.thebrodyaga.englishsounds.screen.fragments.video.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.domine.interactors.AllVideoInteractor
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.legacy.adapters.VideoListAdapter
import com.thebrodyaga.legacy.AdItemDecorator
import com.thebrodyaga.legacy.adapters.decorator.VideoListItemDecoration
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.app.BasePresenter
import com.thebrodyaga.feature.soundList.impl.SoundsListFragment.Companion.calculateNoOfColumns
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.utils.CompositeAdLoader
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoItemInList
import com.thebrodyaga.legacy.VideoListType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_video_list.*
import moxy.InjectViewState
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import timber.log.Timber
import javax.inject.Inject

class VideoListFragment : BaseFragment(), VideoListView {

    @Inject
    @InjectPresenter
    lateinit var presenter: VideoListPresenter

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    private lateinit var adapter: VideoListAdapter
    private lateinit var spanSizeLookup: SpanSizeLookup

    @ProvidePresenter
    fun providePresenter() = presenter.also {
        it.listType = VideoListType.valueOf(
            arguments?.getString(TYPE_EXTRA)
                ?: throw IllegalAccessError("need put type")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        adapter = VideoListAdapter(
            { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(it)) },
            CompositeAdLoader(requireContext(), lifecycle), RecyclerView.VERTICAL,
            youtubeScreenFactory, getAnyRouter()
        )
        spanSizeLookup =
            SpanSizeLookup(
                adapter,
                calculateNoOfColumns(requireContext(), R.dimen.card_video_width)
            )
    }

    override fun getLayoutId(): Int = R.layout.fragment_video_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = GridLayoutManager(context, spanSizeLookup.maxColumns)
            .also { it.spanSizeLookup = spanSizeLookup }
        list.addItemDecoration(
            VideoListItemDecoration(view.context.resources.getDimensionPixelOffset(R.dimen.base_offset_small))
        )
        list.addItemDecoration(
            AdItemDecorator(
                view.context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun setList(list: List<VideoItemInList>) {
        adapter.setData(list)
    }

    private class SpanSizeLookup constructor(
        private val adapter: VideoListAdapter,
        val maxColumns: Int
    ) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            //getItemViewType равен индексу добавления в delegatesManager адаптера
            return 1
            /*when (adapter.getItemViewType(position)) {
                0, 1 -> maxColumns
                else -> 1
            }*/
        }
    }

    companion object {

        private const val TYPE_EXTRA = "TYPE_EXTRA"

        fun newInstance(listType: VideoListType) = VideoListFragment().apply {
            arguments = Bundle().also { it.putString(TYPE_EXTRA, listType.name) }
        }
    }
}

interface VideoListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setList(list: List<VideoItemInList>)
}

@InjectViewState
class VideoListPresenter @Inject constructor(
    private val videoInteractor: AllVideoInteractor
) : BasePresenter<VideoListView>() {

    lateinit var listType: VideoListType

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        unSubscribeOnDestroy(
            videoInteractor.getAllList()
                .flatMapIterable { it }
                .filter {
                    when (listType) {
                        VideoListType.ContrastingSounds -> it is ContrastingSoundVideoListItem
                        VideoListType.MostCommonWords -> it is MostCommonWordsVideoListItem
                        VideoListType.AdvancedExercises -> it is AdvancedExercisesVideoListItem
                        VideoListType.VowelSounds -> it is SoundVideoListItem && it.soundType == SoundType.VOWEL_SOUNDS
                        VideoListType.RControlledVowels -> it is SoundVideoListItem && it.soundType == SoundType.R_CONTROLLED_VOWELS
                        VideoListType.ConsonantSounds -> it is SoundVideoListItem && it.soundType == SoundType.CONSONANT_SOUND
                    }
                }
                .flatMapIterable { it.list }
                .toList()
                /*.map {
                    val result = mutableListOf<VideoItemInList>()
                    it.forEachIndexed { index, item ->
                        when {
                            index == 2 && index != it.lastIndex ->
                                result.add(AdItem(AdTag.SOUND_VIDEO_LIST, listType.name))
                            *//*index != 0 && index % 6 == 0 && index != it.lastIndex ->
                                result.add(AdItem(AdTag.SOUND_VIDEO_LIST, listType.name))*//*
                        }
                        result.add(item)
                    }
                    result
                }*/
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setList(it)
                }, { Timber.e(it) })
        )
    }
}