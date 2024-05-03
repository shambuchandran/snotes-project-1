package com.example.snotes.alladaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snotes.R

class AudioAdapter(var context: Context):RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private lateinit var audioPaths: List<String>
    private lateinit var audioDurations: List<String>
    private lateinit var audioFilenames: List<String>


    class ViewHolder(audioview: View) : RecyclerView.ViewHolder(audioview) {
        val tvAudioDuration: TextView = audioview.findViewById(R.id.tvaudioduration)
        val tvAudioFilename: TextView = audioview.findViewById(R.id.tvaudiofilename)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val audioitemview =
            LayoutInflater.from(parent.context).inflate(R.layout.audioitem, parent, false)
        return ViewHolder(audioitemview)

    }

    override fun getItemCount(): Int {
        return audioPaths.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("AudioAdapter", "Binding view holder at position $position")
        Log.d("AudioAdapter", "Paths size: ${audioPaths.size}, Durations size: ${audioDurations.size}, Filenames size: ${audioFilenames.size}")
        if (position < audioPaths.size && position < audioDurations.size && position < audioFilenames.size) {
            val oneaudio = audioPaths[position]
            val oneAudioFilename = audioFilenames[position]
            val oneAudioDuration = audioDurations[position]
            Log.d("AudioAdapter", "Audio Path: $oneaudio, Filename: $oneAudioFilename, Duration: $oneAudioDuration")
            holder.tvAudioFilename.text = oneAudioFilename
            holder.tvAudioDuration.text = oneAudioDuration
        }
    }

    fun setAudioPaths(paths: List<String>, durations: List<String>, filenames: List<String>) {
        if (paths.size == durations.size && paths.size == filenames.size) {
            Log.d("AudioAdapter", "Setting audio paths")
            audioPaths = paths.toMutableList()
            audioDurations = durations.toMutableList()
            audioFilenames = filenames.toMutableList()
            Log.d("AudioAdapter", "Audio paths size: ${audioPaths.size}")
            Log.d("AudioAdapter", "Audio durations size: ${audioDurations.size}")
            Log.d("AudioAdapter", "Audio filenames size: ${audioFilenames.size}")
            notifyDataSetChanged()
        } else {

            throw RuntimeException("Lists must be the same size")
        }
    }
}

