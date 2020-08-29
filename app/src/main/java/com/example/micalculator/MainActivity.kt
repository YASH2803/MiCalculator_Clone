package com.example.micalculator

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.DragEvent
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MotionEventCompat.getActionMasked
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.abs


class MainActivity : AppCompatActivity(), View.OnDragListener {


    private var firstTime: Boolean = true // flag for removing the zero the answer field

    /**it will allow the output textview to only edit text until operators are not used
     * as soon as any operator comes on the screen the answer screen will stop updating
     */
    private var operatorAppended: Boolean = false

    /**
     * Equal Sign to be appended before the answer value in the answer textView it should be done only once
     */
    private var isEqualSignApplied: Boolean = false

    private var isEqualPressed: Boolean = false


    private var isViewUp = true

    private lateinit var gestureDetector: GestureDetector

    private var MIN_DISTANCE: Int = 5
//    /**
//     * For storing the input expression for history view
//     */
//    val historyInputList: ArrayList<String> = ArrayList()
//
//    /**
//     * For storing the output expression for history view
//     */
//    val historyOutputList: ArrayList<String> = ArrayList()

    private val historyList: ArrayList<HistoryData> = ArrayList()

    var downX: Float = 0F
    var downY: Float = 0F
    var upX: Float = 0F
    var upY: Float = 0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.constrainlayout)

        /** set the theme according to the theme of the system */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
//
//        val viewModel = ViewModelProviders.of(this).get(CalculatorViewModel::class.java)
//
//        binding.viewModel = viewModel
//
//        binding.setLifecycleOwner(this)


        answer.text = "0"
//        numbers
        one.setOnClickListener { appendOnInput("1", true) }
        two.setOnClickListener { appendOnInput("2", true) }
        three.setOnClickListener { appendOnInput("3", true) }
        four.setOnClickListener { appendOnInput("4", true) }
        five.setOnClickListener { appendOnInput("5", true) }
        six.setOnClickListener { appendOnInput("6", true) }
        seven.setOnClickListener { appendOnInput("7", true) }
        eight.setOnClickListener { appendOnInput("8", true) }
        nine.setOnClickListener { appendOnInput("9", true) }
        zero.setOnClickListener { appendOnInput("0", true) }
        dot.setOnClickListener { appendOnInput(".", true) }

        //operators
        addition.setOnClickListener { appendOnInput("+", false) }
        subtraction.setOnClickListener { appendOnInput("-", false) }
        multiplication.setOnClickListener { appendOnInput("x", false) }
        division.setOnClickListener { appendOnInput("/", false) }
        mod.setOnClickListener { appendOnInput("%", false) }

        //clear
        clear.setOnClickListener {
            clear()
        }

        //backspace
        backspace.setOnClickListener {
            backspace()
        }


        equals.setOnClickListener {
            calculate()
            try {
                val correctInput =
                    StringBuilder()    //for converting the x character to "*" for multiplication purpose
                for (char in input.text) {
                    if (char == 'x') {
                        correctInput.append("*")
                    } else {
                        correctInput.append(char.toString())
                    }
                }
                val getInput = ExpressionBuilder(correctInput.toString()).build()
                val getOutput = getInput.evaluate()
                val longOutput = getOutput.toLong()
                if (getOutput == longOutput.toDouble()) {
                    isEqualPressed = true
//                    invertTextSize(20f, 50f)
                    input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    answer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                    answer.text = longOutput.toString()
                    answer.text = ("=${answer.text}")
                    historyList.add(HistoryData(input.text.toString(), answer.text.toString()))  // adding data in data class
                    history_view.adapter?.notifyDataSetChanged()                          // to update RV
                    history_view.smoothScrollToPosition(historyList.size - 1)     // to keep the recyclerView pointing at bottom

                } else {
                    isEqualPressed = true
//                    invertTextSize(20f, 50f)
                    input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    answer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                    answer.text = getOutput.toString()
                    answer.text = ("=${answer.text}")
                    historyList.add(HistoryData(input.text.toString(), answer.text.toString()))
                    history_view.adapter?.notifyDataSetChanged()
                    history_view.smoothScrollToPosition(historyList.size - 1)

                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }


        history_view.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeUp() {
                if (!isViewUp) {
                    button_layout.slideUp()
                    buttons_state(true, View.VISIBLE)
                    isViewUp = !isViewUp
                }
            }

            override fun onSwipeDown() {
                if (isViewUp) {
                    button_layout.slideDown()
                    buttons_state(false, View.GONE)
                    isViewUp = !isViewUp
                }
            }
        })
        input.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeUp() {
                if (!isViewUp) {
                    button_layout.slideUp()
                    buttons_state(true,View.VISIBLE)
                    isViewUp = !isViewUp
                }
            }

            override fun onSwipeDown() {
                if (isViewUp) {
                    button_layout.slideDown()
                    buttons_state(false, View.GONE)
                    isViewUp = !isViewUp
                }
            }
        })
        answer.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeUp() {
                if (!isViewUp) {
                    button_layout.slideUp()
                    buttons_state(true, View.VISIBLE)
                    isViewUp = !isViewUp
                }
            }

            override fun onSwipeDown() {
                if (isViewUp) {
                    button_layout.slideDown()
                    buttons_state(false, View.GONE)
                    isViewUp = !isViewUp
                }
            }
        })

        val layoutManager = LinearLayoutManager(this)
        layoutManager.scrollToPosition(historyList.size - 1)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = false
        history_view.layoutManager = layoutManager
        history_view.adapter = HistoryDataAdapter(historyList)

//        val adapter = history_view.adapter as HistoryDataAdapter
//        adapter.submitList(historyList)


    }

    fun buttons_state(setView: Boolean, viewState: Int) {
        clear.isEnabled = setView
        one.isEnabled = setView
        two.isEnabled = setView
        three.isEnabled = setView
        four.isEnabled = setView
        five.isEnabled = setView
        six.isEnabled = setView
        seven.isEnabled = setView
        eight.isEnabled = setView
        nine.isEnabled = setView
        zero.isEnabled = setView
        addition.isEnabled = setView
        subtraction.isEnabled = setView
        multiplication.isEnabled = setView
        division.isEnabled = setView
        equals.isEnabled = setView
        mod.isEnabled = setView
        backspace.isEnabled = setView
        dot.isEnabled = setView

        clear.visibility = viewState
        one.visibility = viewState
        two.visibility = viewState
        three.visibility = viewState
        four.visibility = viewState
        five.visibility = viewState
        six.visibility = viewState
        seven.visibility = viewState
        eight.visibility = viewState
        nine.visibility = viewState
        zero.visibility = viewState
        addition.visibility = viewState
        subtraction.visibility = viewState
        multiplication.visibility = viewState
        division.visibility = viewState
        equals.visibility = viewState
        mod.visibility = viewState
        backspace.visibility = viewState
        dot.visibility = viewState


    }

    private fun appendOnInput(string: String, isDone: Boolean) {
        if (isDone) {
            if (firstTime) {
                answer.text = ""
                firstTime = false
            }
            input.append(string)


            if (!isEqualSignApplied) {
                answer.append("=$string")
                isEqualSignApplied = true
            } else if (!operatorAppended) {
                answer.append(string)
            }

        } else {

            //input.append(answer.text)
            input.append(string)
            operatorAppended = true
            //stopInputFromExceeding = 0
        }


    }

    private fun clear() {
        invertTextSize(50f, 35f)
        input.text = ""
        answer.text = "0"
        firstTime = true
        operatorAppended = false
        isEqualSignApplied = false
        isEqualPressed = false
    }

    private fun backspace() {
        if (!isEqualPressed) {
            val inputString = input.text
            val outputString = answer.text
            if (inputString.length > 1) {
                input.text = inputString.substring(0, inputString.length - 1)
                if (!inputString.last().isDigit()) input.text =
                    inputString.substring(0, inputString.length - 1)
                answer.text = outputString.substring(0, outputString.length - 1)

            } else {
                invertTextSize(50f, 35f)
                input.text = ""
                answer.text = "0"
                firstTime = true
                operatorAppended = false
                isEqualSignApplied = false
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun calculate() {

    }


    private fun invertTextSize(inputSize: Float, answerSize: Float) {
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, inputSize)
        answer.setTextSize(TypedValue.COMPLEX_UNIT_SP, answerSize)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        event?.setLocation(event.rawX, event.rawY)
//
//        val width: Int = parent_layout.layoutParams.width
//        val height: Int = button_layout.layoutParams.height
////        val action = MotionEvent.getActionMasked(event)
//        val min_distance = 5
//        var lastAction: Any
//        val location = button_layout.getLocationOnScreen()
//        val absX = location.x
//        val absY = location.y
//        when(event?.action) { // Check vertical touches
//            MotionEvent.ACTION_DOWN -> {
//
//                downX = button_layout.x - event.rawX
//                downY =  button_layout.y - event.rawY
//                lastAction = MotionEvent.ACTION_DOWN;
//                return true
//            }
//            MotionEvent.ACTION_MOVE -> {
//                upX = event.rawX + downX     //move coords
//                upY = event.rawY + downY
//
//                val deltaX: Float = downX - upX
//                Log.i("MainActivity", "$deltaX")
//                val deltaY: Float = downY - upY
//                Log.i("MainActivity", "$deltaY")
//                if((upX <= 0 || upX >= width) || (upY <= 0 || upY >= height)){
//                    lastAction = MotionEvent.ACTION_MOVE
//                }
//                button_layout.x = upX
//                button_layout.y = upY
//
//                lastAction = MotionEvent.ACTION_MOVE
//
//                //VERTICAL SCROLL
//                if(abs(deltaX) < abs(deltaY)){
//                    if (abs(deltaY) > min_distance) {
//                        // top or down
//                        if (deltaY < 0) {
////                            this.onTopToBottomSwipe();
//                            Log.i("MainActivity", "Swipe Down detected")
//                            button_layout.animate()
//                                .x(event.rawX + downX)
//                                .y(event.rawY + downY)
//                                .setDuration(0)
//                                .start();
//                            button_layout.setOnDragListener(this)
//                            return true
//                        }
//                        if (deltaY > 0) {
////                            this.onBottomToTopSwipe();
//                            Log.i("MainActivity", "Swipe Up detected")
//                            button_layout.setOnDragListener(this)
//                            return true
//                        }
//                    } else {
//                        //not long enough swipe...
//                        return false;
//                    }
//                }
//                return false;
//            }
//
//        }
        return false
    }

    private fun onBottomToTopSwipe() {
        button_layout.slideUp()
    }

    private fun onTopToBottomSwipe() {
        button_layout.slideDown()
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DRAG_LOCATION -> {

            }
        }
        return false
    }

}

fun View.getLocationOnScreen(): Point {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return Point(location[0], location[1])

    // use this to get coordinates

}


fun View.slideUp(duration: Int = 200) {
    visibility = View.VISIBLE
    isEnabled = true
    val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun View.slideDown(duration: Int = 200) {
    visibility = View.GONE
    isEnabled = false
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}







