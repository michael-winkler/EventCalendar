package com.nmd.eventCalendar.custom

import android.content.Context
import android.graphics.Canvas
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import com.google.android.material.textview.MaterialTextView

internal class VerticalMaterialTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialTextView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        val textPaint: TextPaint? = paint
        textPaint?.color = currentTextColor
        textPaint?.drawableState = drawableState
        canvas.withTranslation(0f, height.toFloat()) {
            rotate(-90f)
            translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
            layout.draw(this)
        }
    }
}