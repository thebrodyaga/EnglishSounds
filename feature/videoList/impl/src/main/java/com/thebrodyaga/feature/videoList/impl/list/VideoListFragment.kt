package com.thebrodyaga.feature.videoList.impl.list

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
import com.thebrodyaga.data.sounds.api.model.SoundType
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.app.BasePresenter
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentVideoListBinding
import com.thebrodyaga.feature.videoList.impl.di.VideoListComponent
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdItemDecorator
import com.thebrodyaga.legacy.AdvancedExercisesVideoListItem
import com.thebrodyaga.legacy.ContrastingSoundVideoListItem
import com.thebrodyaga.legacy.MostCommonWordsVideoListItem
import com.thebrodyaga.legacy.SoundVideoListItem
import com.thebrodyaga.legacy.VideoItemInList
import com.thebrodyaga.legacy.adapters.VideoListAdapter
import com.thebrodyaga.legacy.adapters.decorator.VideoListItemDecoration
import com.thebrodyaga.legacy.utils.CompositeAdLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class VideoListFragment : BaseFragment(), VideoListView {

    @Inject
    @InjectPresenter
    lateinit var presenter: VideoListPresenter

    @Inject
    lateinit var soundDetailsScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: VideoListViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: VideoListAdapter
    private lateinit var spanSizeLookup: SpanSizeLookup
    private val binding by viewBinding(FragmentVideoListBinding::bind)

    @ProvidePresenter
    fun providePresenter() = presenter.also {
        it.listType = VideoListType.valueOf(
            arguments?.getString(TYPE_EXTRA)
                ?: throw IllegalAccessError("need put type")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        VideoListComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
        adapter = VideoListAdapter(
            { getAnyRouter().navigateTo(soundDetailsScreenFactory.soundDetailsScreen(it)) },
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
        binding.list.addItemDecoration(
            VideoListItemDecoration(view.context.resources.getDimensionPixelOffset(R.dimen.base_offset_small))
        )
        binding.list.addItemDecoration(
            AdItemDecorator(
                view.context, RecyclerView.VERTICAL,
                R.dimen.ad_item_in_vertical_horizontal_offset
            )
        )
        binding.list.layoutManager = layoutManager
        binding.list.adapter = adapter
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
        launch {
            val flow = videoInteractor.getAllList()
                .flatMapLatest { it.asFlow() }
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
                .flatMapLatest { it.list.asFlow() }
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
                .flowOn(Dispatchers.IO)
                .onCompletion { it?.let { Timber.e(it) } }

            try {
                viewState.setList(flow.toList())
            } catch (e: Throwable) {
                Timber.e(e)
            }

        }
    }
}