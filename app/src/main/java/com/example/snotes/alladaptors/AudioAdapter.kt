package com.example.snotes.alladaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snotes.R
import com.example.snotes.database.Notedatabase

class AudioAdapter(var context: Context):RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private var audioPaths: List<String> = listOf()

    class ViewHolder(audioview: View):RecyclerView.ViewHolder(audioview) {
        val audiovieweach:ImageView=audioview.findViewById(R.id.rcaudioView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val audioitemview=LayoutInflater.from(parent.context).inflate(R.layout.audioitem, parent,false)
        return  ViewHolder(audioitemview)

    }

    override fun getItemCount(): Int {
       return audioPaths.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oneaudio=audioPaths[position]
        Glide.with(context).load(oneaudio).into(holder.audiovieweach)
    }
    fun setAudioPaths(paths: List<String>) {
        audioPaths = paths
        notifyDataSetChanged()
    }
    init {
        val notesDao = Notedatabase.getDatabase(context).notesdao()
        notesDao.getAllAudioPaths().observe(context as LifecycleOwner, Observer { paths ->
            setAudioPaths(paths)
        })
    }


}