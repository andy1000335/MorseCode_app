package com.example.morsecode

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import android.os.Build
import android.R.attr.start
import android.media.*


class MainActivity : AppCompatActivity() {

    val TIME_UNIT: Long = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.volumeControlStream = AudioManager.STREAM_MUSIC
    }

    @SuppressLint("DefaultLocale")
    fun send_text(view: View) {
        var morseCode = ""
        if (rg_methodChoice.checkedRadioButtonId==R.id.rb_text) {
            val originText = et_OriginText.editableText.toString().toUpperCase()
            val byteArray = originText.toByteArray()
            for (char in byteArray) {
                val ascii = char.toInt()
                var morse = ""
                var codeArray: Array<String>
                when (ascii) {
                    in 65..90 -> {
                        codeArray = resources.getStringArray(R.array.EnglishLetter)
                        morse = codeArray[ascii - 65]
                    }
                    in 48..57 -> {
                        codeArray = resources.getStringArray(R.array.LongNumber)
                        morse = codeArray[ascii - 48]
                    }
                    32 -> morse = " "
                    else -> {
                        codeArray = resources.getStringArray(R.array.Punctuation)
                        when (ascii) {
                            in 33..34 -> morse = codeArray[ascii - 33]
                            in 38..41 -> morse = codeArray[ascii - 35]
                            in 43..47 -> morse = codeArray[ascii - 36]
                            in 58..59 -> morse = codeArray[ascii - 46]
                            in 63..64 -> morse = codeArray[ascii - 47]
                            61 -> morse = codeArray[ascii - 47]
                            95 -> morse = codeArray[ascii - 78]
                            else -> {
                            }
                        }
                    }
                }
                morseCode += "$morse "
            }
        } else {
            val spinnerIndex = spinner.selectedItemPosition
            val codeArray = resources.getStringArray(R.array.UniformSymbol)
            morseCode = codeArray[spinnerIndex]
        }
        tv_MorseCode.text = morseCode

        Handler().postDelayed({
            if (morseCode != "") {
                val pattern: ArrayList<Long> = ArrayList()
                pattern.add(0)
                for (code in morseCode.toCharArray()) {
                    when (code) {
                        '-' -> {
                            pattern.add(TIME_UNIT * 2)
                            pattern.add(TIME_UNIT)
                        }
                        '·' -> {
                            pattern.add(TIME_UNIT)
                            pattern.add(TIME_UNIT)
                        }
                        ' ' -> {
                            pattern.add(0)
                            pattern.add(TIME_UNIT * 2)
                        }
                    }
                }
                pattern.add(0)

                when (view.id) {
                    R.id.bt_light -> {
                        val utils = FlashUtils(this)
                        for (index in 0 until pattern.size) {
                            if ((index%2 == 0) && (pattern[index+1]!=0L)) {
                                Thread.sleep(pattern[index])
                                utils.open()
                            } else {
                                Thread.sleep(pattern[index])
                                utils.close()
                            }
                        }

                        // 有閃爍問題
//                        for (time in pattern) {
//                            Thread.sleep(time)
//                            utils.converse()
//                        }
                    }
                    R.id.bt_vibration -> {
                        val vb = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vb.vibrate(pattern.toLongArray(), -1)
                    }
                    R.id.bt_beep -> {
                        val mediaPlayer = AudioPlayer(this)
                        for (time in pattern) {
                            Thread.sleep(time)
                            mediaPlayer.converse()
                        }
                    }
                }
            }
        }, 1)
    }


}
