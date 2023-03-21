package com.thebrodyaga.feature.videoList.impl.list

import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentVideoListBinding
import com.thebrodyaga.feature.videoList.impl.di.VideoListComponent
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdItemDecorator
import com.thebrodyaga.legacy.adapters.VideoListAdapter
import com.thebrodyaga.legacy.adapters.decorator.VideoListItemDecoration
import com.thebrodyaga.legacy.utils.CompositeAdLoader
import javax.inject.Inject
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VideoListFragment : ScreenFragment(R.layout.fragment_video_list) {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        val listType = VideoListType.valueOf(arguments?.getString(TYPE_EXTRA) ?: throw IllegalAccessError("need put type"))
        VideoListComponent.factory(findDependencies(), listType).inject(this)
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
        viewModel.getState()
            .filterIsInstance<VideoListState.Content>()
            .onEach { adapter.setData(it.list) }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
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