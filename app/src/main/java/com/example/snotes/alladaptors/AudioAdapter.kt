package com.example.snotes.alladaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snotes.OnItemClickListener
import com.example.snotes.R

data class AudioItem(val path: String, val duration: String, val filename: String)

class AudioAdapter(var context: Context, var listener: OnItemClickListener):RecyclerView.Adapter<AudioAdapter.ViewHolder>(){
private var audioItems: MutableList<AudioItem> = mutableListOf()

    inner class ViewHolder(audioview: View) : RecyclerView.ViewHolder(audioview),View.OnClickListener,View.OnLongClickListener  {
        val tvAudioDuration: TextView = audioview.findViewById(R.id.tvaudioduration)
        val tvAudioFilename: TextView = audioview.findViewById(R.id.tvaudiofilename)
        init {
            audioview.setOnClickListener(this)
            audioview.setOnLongClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            listener.onItemClickListener(position)
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            listener.onItemLongClickListener(position)
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val audioitemview =
            LayoutInflater.from(parent.context).inflate(R.layout.audioitem, parent, false)
        return ViewHolder(audioitemview)

    }

    override fun getItemCount(): Int {
       // return audioPaths.size
        return audioItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audioItem = audioItems[position]
        holder.tvAudioFilename.text = audioItem.filename
        holder.tvAudioDuration.text = audioItem.duration
    }

    fun setAudioPaths(audioItems: MutableList<AudioItem>){
        this.audioItems.addAll(audioItems)
        notifyDataSetChanged()
    }


}


