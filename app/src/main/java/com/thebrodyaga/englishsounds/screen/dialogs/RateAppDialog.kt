package com.thebrodyaga.englishsounds.screen.dialogs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.tools.SettingManager
import com.thebrodyaga.englishsounds.utils.AppAnalytics
import kotlinx.android.synthetic.main.fragment_rate_app_dialog.*
import javax.inject.Inject


class RateAppDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var settingManager: SettingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getTheme(): Int = R.style.Widget_AppTheme_BottomSheet

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_rate_app_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rate_btn.setOnClickListener {
            when (app_rating_bar.selectedStar) {
                1, 2, 3 -> {
                    settingManager.onRateLessThenFour()
                    sendAnalyticsEvent(app_rating_bar.selectedStar)
                    dismiss()
                }
                null -> app_rating_bar.setOnError()
                else -> {
                    settingManager.onRated()
                    sendAnalyticsEvent(app_rating_bar.selectedStar)
                    showPlayMarket(it.context)
                    dismiss()
                }
            }
        }
        rate_later_btn.setOnClickListener {
            settingManager.onLaterRate()
            sendAnalyticsEvent(null)
            dismiss()
        }
    }

    private fun sendAnalyticsEvent(rate: Int?) {
        val bundle = Bundle()
        bundle.putString(AppAnalytics.PARAM_RATE, rate?.toString() ?: "later")
        (activity as? AppActivity)?.firebaseAnalytics?.logEvent(AppAnalytics.EVENT_RATE_APP, bundle)
    }

    private fun showPlayMarket(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
        }

    }

    companion object {
        const val TAG = "RateAppDialogTag"
    }
}