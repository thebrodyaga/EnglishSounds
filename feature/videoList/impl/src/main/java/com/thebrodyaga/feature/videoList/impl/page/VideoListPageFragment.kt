package com.thebrodyaga.feature.videoList.impl.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.launchWithLifecycle
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.carousel.videoItemDelegate
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentPageVideoListBinding
import com.thebrodyaga.feature.videoList.impl.di.VideoListComponent
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.adapters.decorator.VideoListItemDecoration
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class VideoListPageFragment : ScreenFragment(R.layout.fragment_page_video_list) {

    @Inject
    lateinit var soundDetailsScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var youtubeScreenFactory: YoutubeScreenFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: VideoListViewModel by viewModels { viewModelFactory }

    private var adapter = CommonAdapter(
        delegates = listOf(
            videoItemDelegate { binding, item ->
                binding.youtubeVideoItemVideoView.setOnClickListener {
                    viewModel.onVideoClick(item.videoId)
                }
                binding.youtubeVideoItemFirstSound.setOnClickAction { _, sound ->
                    viewModel.onSoundClick(sound)
                }
                binding.youtubeVideoItemSecondSound.setOnClickAction { _, sound ->
                    viewModel.onSoundClick(sound)
                }
            }
        )
    )
    private val maxColumns: Int by lazy { calculateNoOfColumns(requireContext(), R.dimen.card_video_width) }
    private var spanSizeLookup = SpanSizeLookup()

    private val binding by viewBinding(FragmentPageVideoListBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        val listType =
            VideoListType.valueOf(arguments?.getString(TYPE_EXTRA) ?: throw IllegalAccessError("need put type"))
        VideoListComponent.factory(this, listType).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = GridLayoutManager(context, maxColumns)
            .also { it.spanSizeLookup = spanSizeLookup }
        binding.pageVideoList.addItemDecoration(
            VideoListItemDecoration(view.context.resources.getDimensionPixelOffset(R.dimen.base_offset_small))
        )
        binding.pageVideoList.layoutManager = layoutManager
        binding.pageVideoList.adapter = adapter
        viewModel.getState()
            .filterIsInstance<VideoListState.Content>()
            .onEach { adapter.items = (it.list) }
            .launchWithLifecycle(lifecycle)
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            val systemAndIme = insets.systemAndIme()
            binding.pageVideoList.appleBottomInsets(systemAndIme)
            insets
        }
    }

    private class SpanSizeLookup constructor() : GridLayoutManager.SpanSizeLookup() {
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

        fun newInstance(listType: VideoListType) = VideoListPageFragment().apply {
            arguments = Bundle().also { it.putString(TYPE_EXTRA, listType.name) }
        }
    }
}