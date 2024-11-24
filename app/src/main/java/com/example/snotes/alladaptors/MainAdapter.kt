package com.example.snotes.alladaptors

import android.net.Uri
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.recyclerview.widget.RecyclerView
import com.example.snotes.R
import com.example.snotes.database.Notesdata

class MainAdapter(private val notesList:MutableList<Notesdata>):RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        private val tvShowDateMain: TextView = itemView.findViewById(R.id.tv_show_date_main)
        private val tvAlarmMain: TextView = itemView.findViewById(R.id.tv_alarm_main)
        private val viewFlipper: ViewFlipper = itemView.findViewById(R.id.image_available)
        private val audioView:TextView = itemView.findViewById(R.id.audio_available)
        private val noteTitle:TextView=itemView.findViewById(R.id.tv_title_main)
        private val noteContent: TextView = itemView.findViewById(R.id.tv_note_content_main)
        fun bind(note:Notesdata){
            tvShowDateMain.text=note.date
            if (note.alarm ==""){
                tvAlarmMain.text=""
            }else{
                tvAlarmMain.visibility=View.VISIBLE
                tvAlarmMain.text=note.alarm
            }
            noteTitle.text=note.title
            noteContent.text=note.text
            if (note.imagePaths.isNotEmpty()){
                viewFlipper.visibility=View.VISIBLE
                viewFlipper.removeAllViews()
                note.imagePaths.forEach{imagePath ->
                    val imageView= ImageView(itemView.context)
                    imageView.layoutParams=ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    imageView.scaleType=ImageView.ScaleType.CENTER_CROP
                    //imageView.setImageResource(R.drawable.add_photo)
                    viewFlipper.addView(imageView)
                    viewFlipper.startFlipping()
                    android.os.Handler(Looper.getMainLooper()).postDelayed({
                        imageView.setImageURI(Uri.parse(imagePath))
                    },1000)
                }
            }else{
                viewFlipper.visibility=View.GONE
            }
            if (note.audioPaths.isNotEmpty()) {
                audioView.visibility = View.VISIBLE
            } else {
                audioView.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mainItemView=LayoutInflater.from(parent.context).inflate(R.layout.rveachitem,parent,false)
        return ViewHolder(mainItemView)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note)

    }
}