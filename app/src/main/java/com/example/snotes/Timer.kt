package com.example.snotes

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerChangeListener) {
    interface OnTimerChangeListener{
        fun onTimerChange(duration: String)
    }
    private var handler =Handler(Looper.getMainLooper())
    private lateinit var runnable:Runnable

    private var duration = 0L
    private var delay= 100L
    init {
        runnable= Runnable {
            duration += delay
            handler.postDelayed(runnable,delay)
            listener.onTimerChange(format())
        }
    }
     fun start(){
        handler.postDelayed(runnable,delay)
    }
     fun pause(){
        handler.removeCallbacks(runnable)
    }
     fun stop(){
        handler.removeCallbacks(runnable)
        duration=0L
    }
    fun format():String{
        val millis=(duration %1000)/10
        val seconds=(duration/1000)%60
        val minutes =(duration/(1000*60))%60
        val hours=(duration/(1000*60*60))
        var timeFormat =if(hours>0){ "%02d:%02d:%02d:%02d".format(hours,minutes,seconds,millis)}
        else{ "%02d:%02d:%02d".format(minutes,seconds,millis)}
            return timeFormat
    }

}