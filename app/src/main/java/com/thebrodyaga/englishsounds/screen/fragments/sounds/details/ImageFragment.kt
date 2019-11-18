package com.thebrodyaga.englishsounds.screen.fragments.sounds.details


import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.training.SoundsTrainingFragment
import kotlinx.android.synthetic.main.fragment_image.*
import java.io.File
import java.lang.IllegalArgumentException

class ImageFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_image

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoPath = arguments?.getString(EXTRA_KEY)
            ?: throw IllegalArgumentException("need put image")
        Glide.with(sound_image.context)
            .load(File(sound_image.context.filesDir, photoPath))
            .into(sound_image)
    }

    companion object {
        private const val EXTRA_KEY = "EXTRA_KEY"
        fun newInstance(image: String): ImageFragment = ImageFragment()
            .apply { arguments = Bundle().also { it.putString(EXTRA_KEY, image) } }

    }
}
