package com.thebrodyaga.feature.videoList.impl.carousel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.ad.api.AdType
import com.thebrodyaga.ad.api.SingleAdLoader
import com.thebrodyaga.ad.api.adSmallLoadingDelegate
import com.thebrodyaga.ad.google.googleAdDelegate
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.launchWithLifecycle
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.databinding.FragmentVideoCarouselBinding
import com.thebrodyaga.feature.videoList.impl.di.VideoListComponent
import com.thebrodyaga.legacy.VideoListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class VideoCarouselFragment : ScreenFragment(R.layout.fragment_video_carousel) {

    private val videoCarouselViewPool = RecyclerView.RecycledViewPool()
    private val adapter by lazy {
        CommonAdapter(
            delegates = listOf(
                dataViewCommonDelegate(
                    bindListener = { view, _ ->
                        view.rightSideView.setOnTextButtonClickAction { _, _ ->
                            val videoListItem = view.item.payload as? VideoListItem
                                ?: return@setOnTextButtonClickAction
                            viewModel.onShowAllVideoClick(videoListItem)
                        }
                    }
                )
            )
        ) {
            row(googleAdDelegate())
            row(adSmallLoadingDelegate())
            row(videoCarouselDelegate(pool = videoCarouselViewPool) { binding, videoItem ->
                binding.carouselItemFirstSound.setOnClickAction { _, sound ->
                    viewModel.onSoundClick(sound)
                }
                binding.carouselItemSecondSound.setOnClickAction { _, sound ->
                    viewModel.onSoundClick(sound)
                }
                binding.carouselItemVideoView.setOnClickListener { _ ->
                    viewModel.onVideoClick(videoItem.videoId)
                }
            })
        }
    }

    @Inject
    lateinit var adLoader: SingleAdLoader

    @Inject
    lateinit var mapper: VideoCarouselMapper

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: VideoCarouselViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentVideoCarouselBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        VideoListComponent.factory(this, null).inject(this)
        super.onCreate(savedInstanceState)
        videoCarouselViewPool.setMaxRecycledViews(VIDEO_CAROUSEL_ITEM_VIEW_TYPE, 12)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        binding.videoCarouselList.swapAdapter(adapter, true)
        val recycledViewPool = binding.videoCarouselList.recycledViewPool
        recycledViewPool.setMaxRecycledViews(VIDEO_CAROUSEL_VIEW_TYPE, 6)
        adLoader.loadAd(lifecycle, adType = AdType.VIDEO_LIST, context = view.context)
        viewModel.getState()
            .filterIsInstance<ListOfVideoListsState.Content>()
            .combine(adLoader.flowAd()) { state, ad ->
                mapper.mapUi(state.list, ad)
            }
            .flowOn(Dispatchers.IO)
            .onEach { adapter.items = (it) }
            .launchWithLifecycle(viewLifecycleOwner.lifecycle)
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.videoCarouselAppbar.appleTopInsets(this)
                binding.videoCarouselList.appleBottomInsets(this)
            }
        }
    }
}