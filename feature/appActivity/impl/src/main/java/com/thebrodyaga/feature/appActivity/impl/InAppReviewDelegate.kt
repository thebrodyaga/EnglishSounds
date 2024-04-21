package com.thebrodyaga.feature.appActivity.impl

import androidx.lifecycle.DefaultLifecycleObserver
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import timber.log.Timber

class InAppReviewDelegate constructor(
    private val activity: AppActivity,
    private val onRateRequestShowed: () -> Unit,
) : DefaultLifecycleObserver {

    private val manager = ReviewManagerFactory.create(activity)
    private var reviewFlow: Task<ReviewInfo>? = null

    fun showRateDialog() {
        if (reviewFlow != null) return
        val reviewFlow = manager.requestReviewFlow()
        this.reviewFlow = reviewFlow
        reviewFlow.addOnCompleteListener(activity, requestReviewListener)
    }

    private val requestReviewListener = OnCompleteListener { requestReviewTask: Task<ReviewInfo> ->
        if (requestReviewTask.isSuccessful) {
            val flow = manager.launchReviewFlow(activity, requestReviewTask.result)
            flow.addOnCompleteListener(activity) { launchReviewTask ->
                if (launchReviewTask.isSuccessful) {
                    onRateRequestShowed()
                } else {
                    @ReviewErrorCode
                    val errorCode = (launchReviewTask.exception as? ReviewException)?.errorCode
                    val errorMessage = launchReviewTask.exception?.message
                    Timber.e("reviewFlow errorCode = $errorCode errorMessage = $errorMessage")
                }
                reviewFlow = null
            }
        } else {
            @ReviewErrorCode
            val errorCode = (requestReviewTask.exception as? ReviewException)?.errorCode
            val errorMessage = requestReviewTask.exception?.message
            Timber.e("reviewFlow errorCode = $errorCode errorMessage = $errorMessage")
            reviewFlow = null
        }
    }
}