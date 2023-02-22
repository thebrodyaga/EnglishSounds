package com.thebrodyaga.feature.youtube.impl.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.thebrodyaga.core.navigation.impl.cicerone.ActivityScreen
import com.thebrodyaga.englishsounds.analytics.AnalyticsEngine
import com.thebrodyaga.englishsounds.analytics.AppAnalytics
import com.thebrodyaga.feature.youtube.api.PlayVideoExtra
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.feature.youtube.impl.YoutubePlayerActivity
import javax.inject.Inject

class YoutubeScreenFactoryImpl @Inject constructor() : YoutubeScreenFactory {
    override fun youtubeScreen(playVideoExtra: PlayVideoExtra) = object :ActivityScreen {
        override fun createIntent(context: Context): Intent {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, playVideoExtra.videoId)
            bundle.putString(AppAnalytics.PARAM_VIDEO_NAME, playVideoExtra.videoName)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video")
            AnalyticsEngine.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            val intent = Intent(context, YoutubePlayerActivity::class.java).apply {
                putExtra(YoutubePlayerActivity.VIDEO_ID_EXTRA, playVideoExtra)
            }
            return intent
        }
    }
}