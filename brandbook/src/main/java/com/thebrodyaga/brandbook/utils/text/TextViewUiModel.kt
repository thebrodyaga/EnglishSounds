package com.thebrodyaga.brandbook.utils.text

import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.utils.drawable.DrawableUiModel
import com.thebrodyaga.brandbook.utils.drawable.bindBackground
import com.thebrodyaga.brandbook.utils.drawable.shapeDrawable
import com.thebrodyaga.brandbook.utils.skeleton.SkeletonDrawable
import com.thebrodyaga.brandbook.utils.skeleton.SkeletonUiModel
import com.thebrodyaga.brandbook.utils.skeleton.bindSkeleton
import com.thebrodyaga.core.uiUtils.common.getAttrStyle
import com.thebrodyaga.core.uiUtils.common.getColorStateList
import com.thebrodyaga.core.uiUtils.insets.InitialViewPadding
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.shape.shapeRoundedAll

sealed interface TextViewUiModel {

    data class Raw(
        val text: TextContainer,
        @AttrRes val textAppearance: Int? = null,
        @AttrRes val textColor: Int? = null,
        val textSize: TextViewSize? = null,
        val gravity: Int? = null,
        val badgeBackground: TextViewBackgroundModel? = null,
        val autoSizeConfiguration: TextViewAutoSizeConfiguration? = null,
        val maxLines: Int? = null,
    ) : TextViewUiModel

    data class Skeleton(
        val skeleton: SkeletonUiModel,
    ) : TextViewUiModel
}

data class TextViewSize(
    val textSize: Float,
    val typedValue: Int = TypedValue.COMPLEX_UNIT_SP
)

// Default minimum size for auto-sizing text in scaled pixels.
private const val DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12

// Default maximum size for auto-sizing text in scaled pixels.
private const val DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112

// Default value for the step size in pixels.
private const val DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1

data class TextViewAutoSizeConfiguration(
    val autoSizeMinTextSize: Int = DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP,
    val autoSizeMaxTextSize: Int = DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP,
    val autoSizeStepGranularity: Int = DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX,
    val typedValue: Int = TypedValue.COMPLEX_UNIT_SP,
    val autoSizeTextType: Int = TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM,
)

data class TextViewBackgroundModel(
    val background: DrawableUiModel = badgeRounded(),
    val padding: InitialViewPadding = badgePadding()
)

fun badgePadding(
    @Px left: Int = 8.px,
    @Px top: Int = 1.px,
    @Px right: Int = 8.px,
    @Px bottom: Int = 3.px,
): InitialViewPadding = InitialViewPadding(left, top, right, bottom)

fun badgeRounded(
    @Px cornerSize: Float = 32f.px,
    @AttrRes tint: Int = R.attr.colorTransparent,
): DrawableUiModel = DrawableUiModel(
    drawable = shapeDrawable(shapeRoundedAll(cornerSize)),
    tint = tint,
)

fun TextView.bindOrGone(model: TextViewUiModel?) {
    this.isVisible = model != null
    if (model != null) bind(model)
}

fun TextView.bindOrInvisible(model: TextViewUiModel?) {
    this.isInvisible = model == null
    if (model != null) bind(model)
}

fun TextView.bind(model: TextViewUiModel) {
    if (equalsNewTextUiModel(model)) return
    when (model) {
        is TextViewUiModel.Raw -> bind(model)
        is TextViewUiModel.Skeleton -> bindSkeleton(model)
    }
}

fun TextView.bind(model: TextViewUiModel.Raw) {
    val initialTextStyle = saveAndGetInitialTextStyle()
    model.textAppearance?.let { TextViewCompat.setTextAppearance(this, getAttrStyle(it)) }
        ?: kotlin.run {
            typeface = initialTextStyle.textAppearance.typeface
            letterSpacing = initialTextStyle.textAppearance.letterSpacing
        }
    model.textColor?.let { setTextColor(getColorStateList(it)) }
        ?: kotlin.run { setTextColor(initialTextStyle.textAppearance.textColors) }

    model.textSize?.let { setTextSize(it.typedValue, it.textSize) }
        ?: kotlin.run { setTextSize(TypedValue.COMPLEX_UNIT_PX, initialTextStyle.textAppearance.textSize) }
    gravity = model.gravity ?: initialTextStyle.gravity
    model.badgeBackground?.background?.let { bindBackground(it) }
        ?: kotlin.run {
            background = initialTextStyle.background
            backgroundTintList = initialTextStyle.backgroundTint
        }
    setHintTextColor(initialTextStyle.textAppearance.hintTextColors)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && foreground is SkeletonDrawable) {
        foreground = null
    }

    maxLines = model.maxLines ?: initialTextStyle.maxLines
    val autoSize = model.autoSizeConfiguration
    val initialAutoSize = initialTextStyle.textViewAutoSizeConfiguration
    val autoSizeTextType =
        (autoSize?.autoSizeTextType ?: initialAutoSize.autoSizeTextType)
    TextViewCompat.setAutoSizeTextTypeWithDefaults(this, autoSizeTextType)
    if (autoSizeTextType != TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE) {
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            this,
            autoSize?.autoSizeMinTextSize ?: initialAutoSize.autoSizeMinTextSize,
            autoSize?.autoSizeMaxTextSize ?: initialAutoSize.autoSizeMaxTextSize,
            autoSize?.autoSizeStepGranularity ?: initialAutoSize.autoSizeStepGranularity,
            autoSize?.typedValue ?: initialAutoSize.typedValue,
        )
    }

    updatePadding(
        left = model.badgeBackground?.padding?.left ?: 0,
        top = model.badgeBackground?.padding?.top ?: 0,
        right = model.badgeBackground?.padding?.right ?: 0,
        bottom = model.badgeBackground?.padding?.bottom ?: 0,
    )
    bind(model.text)
}

fun TextView.bindSkeleton(model: TextViewUiModel.Skeleton) {
    saveAndGetInitialTextStyle()
    val transparent = getColorStateList(R.attr.colorTransparent)
    setTextColor(transparent)
    setHintTextColor(transparent)
    text = ""
    bindSkeleton(model.skeleton)
}

private fun TextView.saveAndGetInitialTextStyle(): InitialTextStyle {
    val tagKey = R.id.initial_text_style_tag_id
    return getTag(tagKey) as? InitialTextStyle ?: let {
        val initialTextStyle = InitialTextStyle(this)
        setTag(tagKey, initialTextStyle)
        initialTextStyle
    }
}

private fun TextView.equalsNewTextUiModel(newModel: TextViewUiModel): Boolean {
    val previewModel = previewTextViewUiModel()
    val isEquals = previewModel != null &&
        newModel.hashCode() == previewModel.hashCode() &&
        previewModel == newModel
    if (!isEquals) {
        setTag(R.id.preview_text_ui_model_tag_id, newModel)
    }
    return isEquals
}

private fun TextView.previewTextViewUiModel(): TextViewUiModel? {
    val tagKey = R.id.preview_text_ui_model_tag_id
    return getTag(tagKey) as? TextViewUiModel
}

private data class InitialTextStyle(
    val textAppearance: InitialTextAppearanceStyle,
    val background: Drawable?,
    val backgroundTint: ColorStateList?,
    val gravity: Int,
    val textViewAutoSizeConfiguration: TextViewAutoSizeConfiguration,
    val maxLines: Int,
) {
    constructor(textView: TextView) : this(
        textAppearance = InitialTextAppearanceStyle(
            textSize = textView.textSize,
            letterSpacing = textView.letterSpacing,
            textColors = textView.textColors,
            typeface = textView.typeface,
            hintTextColors = textView.hintTextColors,
        ),
        background = textView.background,
        backgroundTint = textView.backgroundTintList,
        gravity = textView.gravity,
        maxLines = textView.maxLines,
        textViewAutoSizeConfiguration = TextViewAutoSizeConfiguration(
            autoSizeTextType = TextViewCompat.getAutoSizeTextType(textView),
            autoSizeMinTextSize = TextViewCompat.getAutoSizeMinTextSize(textView),
            autoSizeMaxTextSize = TextViewCompat.getAutoSizeMaxTextSize(textView),
            autoSizeStepGranularity = TextViewCompat.getAutoSizeStepGranularity(textView),
            typedValue = TypedValue.COMPLEX_UNIT_PX,
        ),
    )
}

private data class InitialTextAppearanceStyle(
    @Px val textSize: Float,
    val textColors: ColorStateList?,
    val hintTextColors: ColorStateList?,
    val letterSpacing: Float,
    val typeface: Typeface?,
)