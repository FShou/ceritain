package com.fshou.ceritain.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.fshou.ceritain.R
import com.google.android.material.button.MaterialButton

class Button {

    class Primary : MaterialButton {
        private var bgEnable: Int = 0
        private var bgDisable: Int = 0
        private var txtColor: Int = 0


        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
            init()
        }

        constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributes,
            defStyleAttr
        ) {
            init()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val type = resources.getFont(R.font.plus_jakarta_sans_medium)
            if (isEnabled) setBackgroundColor(bgEnable) else setBackgroundColor(bgDisable)
            setTextColor(txtColor)
            cornerRadius = 14
            textSize = 14f
            typeface = type
        }

        private fun init() {
            bgEnable = ContextCompat.getColor(context, R.color.emerald)
            bgDisable = ContextCompat.getColor(context, R.color.emerald_300)
            txtColor = ContextCompat.getColor(context, R.color.white)
        }
    }

    class Secondary : MaterialButton {
        private var bgColor: Int = 0
        private var txtColor: Int = 0


        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
            init()
        }

        constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributes,
            defStyleAttr
        ) {
            init()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val type = resources.getFont(R.font.plus_jakarta_sans_medium)
            setBackgroundColor(bgColor)
            setTextColor(txtColor)
            cornerRadius = 14
            textSize = 14f
            typeface = type
        }

        private fun init() {
            bgColor = ContextCompat.getColor(context, R.color.grey)
            txtColor = ContextCompat.getColor(context, R.color.black)
        }
    }

    class Ghost : MaterialButton {
        private var bgEnable: Int = 0
        private var txtColor: Int = 0

        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
            init()
        }

        constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributes,
            defStyleAttr
        ) {
            init()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val type = resources.getFont(R.font.plus_jakarta_sans_medium)
            setBackgroundColor(bgEnable)
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
            setTextColor(txtColor)
            cornerRadius = 14
            textSize = 14f
            typeface = type
        }

        private fun init() {
            bgEnable = ContextCompat.getColor(context, R.color.transparent)
            txtColor = ContextCompat.getColor(context, R.color.emerald)
        }
    }
}