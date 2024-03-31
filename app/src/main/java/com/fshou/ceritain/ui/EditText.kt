package com.fshou.ceritain.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.fshou.ceritain.R

class EditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var actionButtonIcon: Drawable
    private lateinit var bgEditText: Drawable
    private lateinit var type: Typeface
    private var textColor: Int = 0
    private var highlight: Int = 0
    private var hintText: Int = 0


    constructor(context: Context) : super(ContextThemeWrapper(context, R.style.EditTextStyle)) {
        init()
    }

    constructor(context: Context, attributes: AttributeSet) : super(
        ContextThemeWrapper(
            context,
            R.style.EditTextStyle
        ), attributes
    ) {
        init()
    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
        ContextThemeWrapper(context, R.style.EditTextStyle),
        attributes,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = bgEditText
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textCursorDrawable = null
        }
        textSize = 16f
        highlightColor = highlight
        setHintTextColor(hintText)
        typeface = type
        setTextColor(textColor)
        when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            -> {
                showClearButton()
            }

            else -> {}
        }
    }

    private fun init() {
        textColor = ContextCompat.getColor(context, R.color.black)
        highlight = ContextCompat.getColor(context, R.color.emerald_200)
        hintText = ContextCompat.getColor(context, R.color.grey_hint)
        bgEditText = ContextCompat.getDrawable(context, R.drawable.bg_edit_text) as Drawable

//        clearIcon = ContextCompat.getDrawable(context,R.drawable.close) as Drawable
//        if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD)
//            clearIcon = ContextCompat.getDrawable(context, R.drawable.logo) as Drawable
        type = resources.getFont(R.font.plus_jakarta_sans_medium)
        setActionIcon()

        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                println()
                when (inputType) {
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
                        error =
                            if (!s.isValidEmail()) "Invalid Email Address" else null
                        if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()

                    }

                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> {
                        if (!s.isValidPassword()) {
                            error = "Password must have 8 chars minimum"
                        }
                        if (s.isEmpty()) error = null
                    }
                }

            }

            override fun afterTextChanged(s: Editable) {


            }
        })

    }

    fun CharSequence.isValidEmail() =
        toString().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun CharSequence.isValidPassword() =
        toString().isNotEmpty() && this.length > 7


    private fun showClearButton() {
        setButtonDrawables(endOfTheText = actionButtonIcon)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (actionButtonIcon.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - actionButtonIcon.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        setActionIcon()
                        showClearButton()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        when (inputType) {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                               setActionIcon()
                                text?.clear()
                                hideClearButton()
                            }

                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                                hideClearButton()
                                inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                println("PASSWORD: $inputType")
                                println("PASSWORD: $actionButtonIcon")
                                setActionIcon()
                                println("PASSWORD After: $actionButtonIcon")
                                showClearButton()
                            }

                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> {
                                hideClearButton()
                                inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                println("VISIBLE: $inputType")
                                println("VISIBLE: $actionButtonIcon")
                                setActionIcon()
                                println("VISIBLE After: $actionButtonIcon")
                                showClearButton()
                            }

                            else -> {}
                        }
                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun setActionIcon() {
        actionButtonIcon = when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> ContextCompat.getDrawable(
                context,
                R.drawable.close
            ) as Drawable

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> ContextCompat.getDrawable(
                context,
                R.drawable.eye
            ) as Drawable

            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> ContextCompat.getDrawable(
                context,
                R.drawable.eye_off
            ) as Drawable

            else -> ContextCompat.getDrawable(
                context,
                R.drawable.close
            ) as Drawable
        }
    }

}