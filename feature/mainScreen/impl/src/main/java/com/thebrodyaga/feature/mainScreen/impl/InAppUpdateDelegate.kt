package com.thebrodyaga.feature.mainScreen.impl

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlin.math.max
import kotlin.math.min

class InAppUpdateDelegate constructor(
    private val context: Context,
    private val fragment: Fragment,
    // progress in 0..100
    private val onDownloadingProgress: (progress: Int?) -> Unit,
    private val onDownloaded: (completeUpdate: () -> Unit) -> Unit,
) : DefaultLifecycleObserver {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)

    init {
        fragment.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener(context as Activity, onSuccess)
    }

    override fun onStart(owner: LifecycleOwner) {
        appUpdateManager.registerListener(updateListener)
    }

    override fun onStop(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(updateListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }

    private fun updateForImmediate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            immediateResultLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
        )
    }

    private fun updateForFlexible(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            flexibleResultLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
        )
    }

    private val immediateResultLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        when (result.resultCode) {
            RESULT_IN_APP_UPDATE_FAILED,
            Activity.RESULT_OK,
            Activity.RESULT_CANCELED -> Unit
        }
    }

    private val flexibleResultLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        when (result.resultCode) {
            RESULT_IN_APP_UPDATE_FAILED,
            Activity.RESULT_OK,
            Activity.RESULT_CANCELED -> Unit
        }
    }

    private val onSuccess = OnSuccessListener<AppUpdateInfo> { appUpdateInfo ->
        when (appUpdateInfo.updateAvailability()) {
            UpdateAvailability.UNKNOWN,
            UpdateAvailability.UPDATE_NOT_AVAILABLE -> Unit

            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> updateForImmediate(appUpdateInfo)
            UpdateAvailability.UPDATE_AVAILABLE -> when {
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> updateForFlexible(appUpdateInfo)
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> updateForImmediate(appUpdateInfo)
            }
        }
    }

    private val updateListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.PENDING,
            InstallStatus.INSTALLING,
            InstallStatus.INSTALLED,
            InstallStatus.FAILED,
            InstallStatus.CANCELED,
            InstallStatus.REQUIRES_UI_INTENT,
            InstallStatus.UNKNOWN -> onDownloadingProgress(null)

            InstallStatus.DOWNLOADED -> onDownloaded {
                onDownloadingProgress(null)
                appUpdateManager.completeUpdate()
            }

            InstallStatus.DOWNLOADING -> {
                val bytesDownloaded = state.bytesDownloaded().toFloat()
                val totalBytesToDownload = state.totalBytesToDownload().toFloat()
                val percent = max(0f, min(100f, ((bytesDownloaded / totalBytesToDownload) * 100f)))
                onDownloadingProgress(percent.toInt())
            }
        }
    }
}