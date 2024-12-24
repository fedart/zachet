package com.example.circle

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var container: ConstraintLayout
    private lateinit var targetRect: FrameLayout
    private lateinit var messageText: TextView

    private val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)
    private val circles = mutableListOf<View>()
    private var targetColor: Int = Color.TRANSPARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container = findViewById(R.id.container)
        targetRect = findViewById(R.id.target_rect)
        messageText = findViewById(R.id.message_text)

        container.post {
            generateCircles()
            setRandomTargetColor()
        }
    }

    private fun generateCircles() {
        for (color in colors) {
            val circle = View(this).apply {
                layoutParams = FrameLayout.LayoutParams(150, 150)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                x = Random.nextFloat() * (container.width - 150)
                y = Random.nextFloat() * (container.height - 150)
                setOnTouchListener(CircleTouchListener(this))
            }
            circles.add(circle)
            container.addView(circle)
        }
    }

    private fun setRandomTargetColor() {
        val randomCircle = circles.randomOrNull()
        if (randomCircle != null) {
            targetColor = (randomCircle.background as? GradientDrawable)?.color?.defaultColor ?: Color.TRANSPARENT
            targetRect.setBackgroundColor(targetColor)
        } else {
            targetRect.setBackgroundColor(Color.TRANSPARENT)
            messageText.text = "Игра завершена!"
        }
    }

    inner class CircleTouchListener(private val circle: View) : View.OnTouchListener {
        private var offsetX = 0f
        private var offsetY = 0f

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    offsetX = event.x
                    offsetY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    circle.x = event.rawX - offsetX
                    circle.y = event.rawY - offsetY
                }
                MotionEvent.ACTION_UP -> {
                    if (isCircleInTarget(circle)) {
                        circles.remove(circle)
                        container.removeView(circle)
                        setRandomTargetColor()
                    }
                }
            }
            return true
        }

        private fun isCircleInTarget(circle: View): Boolean {
            val rectX = targetRect.x
            val rectY = targetRect.y
            val rectWidth = targetRect.width
            val rectHeight = targetRect.height

            val circleCenterX = circle.x + circle.width / 2
            val circleCenterY = circle.y + circle.height / 2

            return circleCenterX in rectX..(rectX + rectWidth) &&
                    circleCenterY in rectY..(rectY + rectHeight) &&
                    (circle.background as? GradientDrawable)?.color?.defaultColor == targetColor
        }
    }
}
