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


class ImageAdapter(var context: Context):RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private var imagePaths: List<String> = listOf()
    class ViewHolder(imageview: View):RecyclerView.ViewHolder(imageview) {
        val imagevieweach:ImageView= imageview.findViewById(R.id.rcimageview)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageitemview=LayoutInflater.from(parent.context).inflate(R.layout.imageitem,parent,false)
        return ViewHolder(imageitemview)

    }

    override fun getItemCount(): Int {
     return imagePaths.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oneimage=imagePaths[position]
        Glide.with(context).load(oneimage).into(holder.imagevieweach)

    }
    fun setImagePaths(paths: List<String>) {
        imagePaths = paths
        notifyDataSetChanged()
    }
    init {
        val notesDao = Notedatabase.getDatabase(context).notesdao()
        notesDao.getAllImagePaths().observe(context as LifecycleOwner, Observer { paths ->
            setImagePaths(paths)
        })
    }

}