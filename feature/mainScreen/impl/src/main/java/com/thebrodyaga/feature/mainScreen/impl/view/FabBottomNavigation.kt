package com.thebrodyaga.feature.mainScreen.impl.view

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.feature.mainScreen.impl.R

class FabBottomNavigation : BottomNavigationView {

    private val materialShapeDrawable: MaterialShapeDrawable = MaterialShapeDrawable()
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        val resources = resources
        val topEdgeTreatment =
            BottomAppBarTopEdgeTreatment(
                resources.getDimension(R.dimen.fab_cradle_margin),
                resources.getDimension(R.dimen.fab_cradle_rounded_corner_radius),
                resources.getDimension(R.dimen.ab_cradle_vertical_offset)
            )
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder()/*.setTopEdge(topEdgeTreatment)*/.build()
        topEdgeTreatment.fabDiameter = resources.getDimension(R.dimen.fab_mic_size)
        materialShapeDrawable.shapeAppearanceModel = shapeAppearanceModel
        materialShapeDrawable.shadowCompatibilityMode =
            MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
        materialShapeDrawable.paintStyle = Paint.Style.FILL
        val backgroundTint = ContextCompat.getColorStateList(context, android.R.color.transparent)
        DrawableCompat.setTintList(materialShapeDrawable, backgroundTint)
        ViewCompat.setBackground(this, materialShapeDrawable)
    }
}