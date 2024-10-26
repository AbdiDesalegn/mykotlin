package com.abdi.mykotlin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var textViewHistory: TextView
    private lateinit var textViewResult: TextView
    private val input = StringBuilder()  // Keeps track of the input expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind UI components
        textViewHistory = findViewById(R.id.textViewHistory)
        textViewResult = findViewById(R.id.textViewResult)

        // Set button listeners
        setButtonListeners()
    }

    private fun setButtonListeners() {
        val buttons = listOf(
            R.id.btnAC, R.id.btnDEL, R.id.btnDivide, R.id.btnMulti,
            R.id.btnMinus, R.id.btnPlus, R.id.btnEqual,
            R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour,
            R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine, R.id.btnDot
        )

        buttons.forEach { id ->
            findViewById<View>(id).setOnClickListener(this)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAC -> clearInput()
            R.id.btnDEL -> deleteLastChar()
            R.id.btnEqual -> calculateResult()
            else -> appendToInput(view)
        }
    }

    // Append the pressed button's value to the input
    private fun appendToInput(view: View) {
        val button = view as androidx.appcompat.widget.AppCompatButton
        val value = button.text.toString()

        input.append(value)
        textViewHistory.text = input.toString()
    }

    // Clear the input when AC button is pressed
    private fun clearInput() {
        input.clear()
        textViewHistory.text = ""
        textViewResult.text = ""
    }

    // Delete the last character of the input
    private fun deleteLastChar() {
        if (input.isNotEmpty()) {
            input.deleteCharAt(input.length - 1)
            textViewHistory.text = input.toString()
        }
    }

    // Perform the calculation when "=" is pressed
    private fun calculateResult() {
        try {
            val result = evaluateExpression(input.toString())
            textViewResult.text = result.toString()
        } catch (e: Exception) {
            textViewResult.text = "Error"
        }
    }

    // Basic evaluation of the input string using Kotlin
    private fun evaluateExpression(expression: String): Double {
        val tokens = tokenizeExpression(expression)
        return parseExpression(tokens)
    }

    // Tokenize the expression into numbers and operators
    private fun tokenizeExpression(expression: String): List<String> {
        val regex = Regex("([0-9]+\\.?[0-9]*)|([-+*/])")
        return regex.findAll(expression).map { it.value }.toList()
    }

    // Parse the tokenized expression and perform basic arithmetic operations
    private fun parseExpression(tokens: List<String>): Double {
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<String>()

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token.toDoubleOrNull() != null) {
                numbers.add(token.toDouble())
            } else if (token in "+-*/") {
                operators.add(token)
            }
            i++
        }

        // Apply multiplication and division first
        var index = 0
        while (index < operators.size) {
            if (operators[index] == "*" || operators[index] == "/") {
                val result = if (operators[index] == "*") {
                    numbers[index] * numbers[index + 1]
                } else {
                    numbers[index] / numbers[index + 1]
                }
                numbers[index] = result
                numbers.removeAt(index + 1)
                operators.removeAt(index)
            } else {
                index++
            }
        }

        // Apply addition and subtraction
        index = 0
        while (index < operators.size) {
            if (operators[index] == "+" || operators[index] == "-") {
                val result = if (operators[index] == "+") {
                    numbers[index] + numbers[index + 1]
                } else {
                    numbers[index] - numbers[index + 1]
                }
                numbers[index] = result
                numbers.removeAt(index + 1)
                operators.removeAt(index)
            }
        }

        return numbers[0]
    }
}
