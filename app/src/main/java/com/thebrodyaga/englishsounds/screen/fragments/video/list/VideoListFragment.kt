package com.thebrodyaga.englishsounds.screen.fragments.video.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.domine.entities.data.SoundType
import com.thebrodyaga.englishsounds.domine.entities.ui.*
import com.thebrodyaga.englishsounds.domine.interactors.AllVideoInteractor
import com.thebrodyaga.englishsounds.screen.adapters.VideoListAdapter
import com.thebrodyaga.englishsounds.screen.appbarBottomPadding
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
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

    private val adapter = VideoListAdapter()

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
    }

    override fun getLayoutId(): Int = R.layout.fragment_video_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.appbarBottomPadding(true)
    }

    override fun setList(list: List<VideoItem>) {
        adapter.setData(list)
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
    fun setList(list: List<VideoItem>)
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
                        VideoListType.All -> true
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.setList(it)
                }, { Timber.e(it) })
        )
    }
}