package com.example.snotes

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.snotes.alladaptors.MainAdapter
import com.example.snotes.database.Notedatabase
import com.example.snotes.database.Notesdata
import com.example.snotes.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var addnotebutton: ImageView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var notedatabase: Notedatabase
    private var notesList: MutableList<Notesdata> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addnotebutton=binding.btnaddnote
        addnotebutton.setOnClickListener {
            //Notedatabase.deleteDatabase(this)

            startActivity(Intent(this,AddNoteActivity::class.java))
        }
        notedatabase=Notedatabase.getDatabase(this)
        CoroutineScope(Dispatchers.Main).launch {
            (notedatabase.notesdao().getAllNotes()).observe(this@MainActivity) { notes ->
                notesList.addAll(notes)
                mainAdapter.notifyDataSetChanged()
            }
        }
        recyclerView = binding.rvmain
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mainAdapter= MainAdapter(notesList)
        recyclerView.adapter=mainAdapter
    }
}