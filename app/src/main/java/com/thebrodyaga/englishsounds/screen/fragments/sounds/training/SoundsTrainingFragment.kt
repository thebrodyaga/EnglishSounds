package com.thebrodyaga.englishsounds.screen.fragments.sounds.training

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.domine.entities.data.AdTag
import com.thebrodyaga.englishsounds.domine.entities.data.PracticeWordDto
import com.thebrodyaga.englishsounds.domine.entities.ui.ShortAdItem
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
import com.thebrodyaga.englishsounds.tools.AudioPlayer
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_sounds_training.*
import kotlinx.android.synthetic.main.fragment_word.view.*
import kotlinx.android.synthetic.main.item_ad_vertical_short.view.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.File
import javax.inject.Inject

class SoundsTrainingFragment : BaseFragment(), SoundsTrainingView {
    override fun getLayoutId(): Int = R.layout.fragment_sounds_training

    @Inject
    @InjectPresenter
    lateinit var presenter: SoundsTrainingPresenter

    @Inject
    lateinit var audioPlayer: AudioPlayer
    private val constraintSet = ConstraintSet()
    private var disposable: Disposable? = null
    private lateinit var nativeAdLoader: CompositeAdLoader
    private lateinit var item: ShortAdItem

    @ProvidePresenter
    fun providePresenter() = presenter

    private var adapter: PageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        nativeAdLoader = CompositeAdLoader(
            requireContext(),
            lifecycle,
            NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE
        )
        item = ShortAdItem(AdTag.SOUND_TRAINING)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnMenuItemClickListener(this)
        play_icon.setRecordVoice(audioPlayer)
        showFab(isShow = true, autoHide = false)

        include_ad.ad_view.apply {
            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            mediaView = ad_media

            // Register the view used for each individual asset.
            headlineView = ad_headline
            callToActionView = ad_call_to_action
            priceView = ad_price
            starRatingView = ad_stars
            storeView = ad_google_play_icon
            advertiserView = ad_advertiser
        }

        fun setEmptyView(isEmpty: Boolean) {
            if (isEmpty)
                constraintSet.setVisibility(include_ad.ad_empty.id, ConstraintSet.VISIBLE)
            else constraintSet.setVisibility(include_ad.ad_empty.id, ConstraintSet.INVISIBLE)
        }

        fun populateNativeAdView(
            nativeAd: UnifiedNativeAd,
            adView: UnifiedNativeAdView
        ) {
            // Some assets are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.callToActionView as Button).text = nativeAd.callToAction

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.price == null) {
                constraintSet.setVisibility(adView.priceView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.priceView.id, ConstraintSet.VISIBLE)
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                constraintSet.setVisibility(adView.storeView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.storeView.id, ConstraintSet.VISIBLE)
                (adView.storeView as? TextView)?.text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.VISIBLE)
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            }
            if (nativeAd.advertiser == null) {
                constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.VISIBLE)
                (adView.advertiserView as TextView).text = nativeAd.advertiser
            }
            // Assign native ad object to the native view.
            adView.setNativeAd(nativeAd)
        }

        disposable?.dispose()
        disposable = nativeAdLoader.getLoader(item.adTag, customTag = item.customTag)
            .adsObservable
            .subscribe { adBox ->
                constraintSet.clone(include_ad.ad_container)
                adBox.ad?.let {
                    setEmptyView(false)
                    if (it.mediaContent != null) {
                        constraintSet.setVisibility(
                            include_ad.ad_view.mediaView.id,
                            ConstraintSet.VISIBLE
                        )
                    } else constraintSet.setVisibility(
                        include_ad.ad_view.mediaView.id,
                        ConstraintSet.GONE
                    )
                    populateNativeAdView(it, include_ad.ad_view)
                } ?: kotlin.run {
                    setEmptyView(true)
                }
                TransitionManager.beginDelayedTransition(include_ad.ad_container)
                constraintSet.applyTo(include_ad.ad_container)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showFab(isShow = true, autoHide = true)
        disposable?.dispose()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        showFab(isShow = true, autoHide = hidden)
    }


    private fun showFab(isShow: Boolean?, autoHide: Boolean?) {
        (activity as? AppActivity)?.toggleFabMic(isShow = isShow, autoHide = autoHide)
    }

    override fun setData(list: List<PracticeWordDto>) {
        video_lib_icon.setOnClickListener {
            getAnyRouter().navigateTo(Screens.AllVideoScreen(VideoListType.MostCommonWords))
        }
        info_icon.setOnClickListener {
            adapter?.also {
                val practiceWordDto = it.list.getOrNull(view_pager.currentItem)
                practiceWordDto?.apply { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(this.sound)) }
            }
        }
        adapter = PageAdapter(this).also {
            it.setData(list)
            view_pager.adapter = it
            view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    play_icon.audioFile =
                        File(view_pager.context.filesDir, it.list[position].audioPath)

                }
            })
        }
    }

    private val onInfoClick = View.OnClickListener {
        adapter?.also {
            val practiceWordDto = it.list.getOrNull(view_pager.currentItem)
            practiceWordDto?.apply { getAnyRouter().navigateTo(Screens.SoundsDetailsScreen(this.sound)) }
        }
    }

    private class PageAdapter(fragment: BaseFragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment =
            WordFragment.newInstance(list[position].name)

        var list = listOf<PracticeWordDto>()

        override fun getItemCount(): Int = list.size

        fun setData(list: List<PracticeWordDto>) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    class WordFragment : BaseFragment() {
        override fun getLayoutId(): Int = R.layout.fragment_word

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            view.word.text = arguments?.getString(WORD_KEY) ?: ""
        }


        companion object {
            private const val WORD_KEY = "WORD_KEY"
            fun newInstance(word: String): WordFragment = WordFragment()
                .apply { arguments = Bundle().also { it.putString(WORD_KEY, word) } }

        }
    }
}
