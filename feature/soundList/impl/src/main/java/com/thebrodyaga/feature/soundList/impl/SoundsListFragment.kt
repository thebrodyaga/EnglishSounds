package com.thebrodyaga.feature.soundList.impl

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.ad.api.AdType
import com.thebrodyaga.ad.api.AppAd
import com.thebrodyaga.ad.api.SingleAdLoader
import com.thebrodyaga.ad.api.adSmallLoadingDelegate
import com.thebrodyaga.ad.google.googleAdDelegate
import com.thebrodyaga.base.navigation.impl.transition.sharedElementBox
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.component.sound.SoundCardUiModel
import com.thebrodyaga.brandbook.component.sound.SoundCardView
import com.thebrodyaga.brandbook.component.sound.soundCardDelegate
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.calculateNoOfColumns
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundList.impl.databinding.FragmentSoundsListBinding
import com.thebrodyaga.feature.soundList.impl.di.SoundListComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SoundsListFragment : ScreenFragment(R.layout.fragment_sounds_list) {

    private val binding by viewBinding(FragmentSoundsListBinding::bind)

    @Inject
    lateinit var detailsScreenFactory: SoundDetailsScreenFactory

    @Inject
    lateinit var viewPool: SoundsListViewPool

    @Inject
    lateinit var adLoader: SingleAdLoader

    @Inject
    lateinit var mapper: SoundListMapper

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SoundsListViewModel by viewModels { viewModelFactory }

    private val maxColumns: Int by lazy { calculateNoOfColumns(requireContext(), R.dimen.card_sound_width) }
    private var spanSizeLookup = SpanSizeLookup()
    private var adapter = CommonAdapter(
        delegates = listOf(dataViewCommonDelegate()),
    ) {
        row(googleAdDelegate())
        row(adSmallLoadingDelegate())
        row(soundCardDelegate {
            onBind { holder, payloads ->
                val view = holder.view
                val item = holder.item
                val shaderView = view
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
        val adFlow = adLoader.getAd(lifecycle, adType = AdType.SOUND_LIST, context = requireContext())
        viewModel.getState()
            .filterIsInstance<SoundsListState.Content>()
            .combine(adFlow) { state, ad ->
                mapper.map(state.sounds, ad, AppAd.Empty)
            }
            .flowOn(Dispatchers.IO)
            .onEach { adapter.items = it }
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
        routerProvider.anyRouter.navigateTo(
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

    private inner class SpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (adapter.getItemViewType(position)) {
                SoundCardView.VIEW_TYPE -> 1
                else -> maxColumns
            }
        }
    }
}
