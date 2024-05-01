package com.example.snotes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class waveFormView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint =Paint()
    private  var amplitudes =ArrayList<Float>()
    private var spikes =ArrayList<RectF>()
    private var radious =6f
    private  var width=9f
    private var d=6f
    private var screenwidth=0f
    private var screenheight=400f
    private var maxSpikes=0
    init {
        paint.color =Color.rgb(51,51,51)
        screenwidth =resources.displayMetrics.widthPixels.toFloat()
        maxSpikes=(screenwidth/(width+d)).toInt()
    }
    fun addAmplitude(amp :Float){
        var normalvalue=Math.min(amp.toInt()/70,350).toFloat()
        amplitudes.add(normalvalue)
        spikes.clear()
        var ampslast=amplitudes.takeLast(maxSpikes)
        for (i in ampslast.indices){
            var left =screenwidth-i*(width+d)
            var top =screenheight/2 - ampslast[i]/2
            var right =left+width
            var bottom =top + ampslast[i]
            spikes.add(RectF(left,top,right,bottom))
        }
        invalidate()
    }
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spikes.forEach{
            canvas.drawRoundRect(it,radious,radious,paint)
        }
//        canvas.drawRoundRect(RectF(20f,30f,20+30f,30+60f),6f,6f,paint)
//        canvas.drawRoundRect(RectF(60f,60f,60+30f,60+360f),6f,6f,paint)
    }
}