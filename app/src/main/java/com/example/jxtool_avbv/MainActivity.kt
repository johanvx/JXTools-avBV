/*
 * JXTool-avBV
 * By Johan Xie 2020
 */

package com.example.jxtool_avbv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transButton: Button = findViewById(R.id.trans_button)
        transButton.setOnClickListener { main() }
    }

    // Button clicked
    private fun main() {
        Toast.makeText(this, "Button clicked",
            Toast.LENGTH_SHORT).show()
        val resultText: TextView = findViewById(R.id.result_text)
        resultText.text = tranMain()
    }

    // base58
    private val table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF".toCharArray()
    private val place = arrayOf(11, 10, 3, 8, 4, 6)
    private val xorNumber = 177451812L
    private val addNumber = 8728348608L

    // BV-code template
    private val model = "BV1__4_1_7__"

    // URL prefix
    private val prefix = "https://www.bilibili.com/video/"

    // Check the validity of the input
    private fun isValid(input: String) : Boolean {
        if (input.indexOf("https") >= 0) {
            if (input.indexOf(prefix) < 0) {
                return false
            }
        }
        val avIndex = input.indexOf("av")
        if (avIndex >= 0) {
            val avTmp = input.substring(avIndex + 2)
            if (avTmp != avTmp.replace(Regex("[^0-9]+"), "_")) {
                return false
            }
        }
        val bvIndex = input.indexOf("BV")
        if (bvIndex >= 0) {
            val bvTmp = input.substring(bvIndex).toCharArray()
            for (i in place) {
                bvTmp[i] = '_'
            }
            if (bvTmp.joinToString(separator = "") != model) {
                return false
            }
        }
        if (avIndex < 0 && bvIndex < 0) {
            return false
        }
        return true
    }

    // Judge the type of the input
    private fun statusJudge(input: String) : String {
        var ret = "Not defined"
        if (input.indexOf("av") >= 0) {
            ret = "av"
        } else if (input.indexOf("BV") >= 0) {
            ret = "BV"
        }
        return ret
    }

    // Encode function
    private fun avToBv(input: Long) : String {
        val ret = model.toCharArray()
        val av = (input xor xorNumber) + addNumber
        for ((i, value) in place.withIndex()) {
            ret[value] = table[((av / 58.toDouble().pow(i).toLong()) % 58).toInt()]
        }
        return ret.joinToString(separator = "")
    }

    // Decode function
    private fun bvToAv(input: String) : Long {
        var ret = 0.toLong()
        val bv = input.toCharArray()
        for ((i, value) in place.withIndex()) {
            ret += table.indexOf(bv[value]).toLong() * 58.toDouble().pow(i).toLong()
        }
        ret = (ret - addNumber) xor xorNumber
        return ret
    }

    private fun tranMain() : String {
        val inputText: EditText = findViewById(R.id.input_text)
        val input = inputText.getText().toString()
        var output = "Input invalid"
        if (isValid(input)) {
            val inputStatus = statusJudge(input)
            if (inputStatus == "av") {
                val av = input.substring(input.indexOf("av") + 2).toLong()
                output = avToBv(av)
            } else if (inputStatus == "BV") {
                val BV = input.substring(input.indexOf("BV"))
                output = "av${bvToAv(BV).toString()}"
            }
            if (input.indexOf("https") >= 0) {
                output = "${prefix}${output}"
            }
        }
        return output
    }
}