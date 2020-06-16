package com.thebrodyaga.englishsounds.screen.fragments.sounds.list


import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType
import com.thebrodyaga.englishsounds.domine.entities.ui.*
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.navigation.TransitionBox
import com.thebrodyaga.englishsounds.screen.adapters.SoundsAdapter
import com.thebrodyaga.englishsounds.screen.appbarBottomPadding
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
import kotlinx.android.synthetic.main.fragment_sounds_list.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class SoundsListFragment : BaseFragment(), SoundsListView {

    override fun getLayoutId(): Int = R.layout.fragment_sounds_list

    @Inject
    @InjectPresenter
    lateinit var presenter: SoundsListPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    private lateinit var adapter: SoundsAdapter
    private lateinit var spanSizeLookup: SpanSizeLookup

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        adapter = SoundsAdapter(presenter.positionList,
            { soundDto, sharedElements -> onSoundClick(soundDto, sharedElements) },
            { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(it)) },
            { onShowAllVideoClick(it) },
            lifecycle
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
        list.layoutManager = GridLayoutManager(context, spanSizeLookup.maxColumns)
            .also { it.spanSizeLookup = spanSizeLookup }
        list.adapter = adapter
        list.itemAnimator = null
        list.appbarBottomPadding(true)
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun setListData(sounds: List<SoundsListItem>) {
        adapter.setData(sounds)
    }

    private fun onSoundClick(item: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) {
        getAnyRouter().navigateToWithTransition(
            Screens.SoundsDetailsScreen(item.transcription),
            TransitionBox(
                null,
                null,
                null
            ),
            *sharedElements
        )

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.transcription)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sound")
        (activity as? AppActivity)?.firebaseAnalytics?.logEvent(
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
        getAnyRouter().navigateTo(Screens.AllVideoScreen(showPage))
    }

    private class SpanSizeLookup constructor(
        private val adapter: SoundsAdapter,
        val maxColumns: Int
    ) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            //getItemViewType равен индексу добавления в delegatesManager адаптера
            return when (adapter.getItemViewType(position)) {
                0, 1 -> maxColumns
                else -> 1
            }
        }
    }

    companion object {
        fun calculateNoOfColumns(
            context: Context,
            @DimenRes columnWidthRes: Int
        ): Int {
            val displayMetrics = context.resources.displayMetrics
            val screenWidthPx = displayMetrics.widthPixels / displayMetrics.density
            val columnWidthPx =
                context.resources.getDimension(columnWidthRes) / displayMetrics.density
            return (screenWidthPx / columnWidthPx + 0.5).toInt()// +0.5 for correct rounding to int.
        }
    }
}
