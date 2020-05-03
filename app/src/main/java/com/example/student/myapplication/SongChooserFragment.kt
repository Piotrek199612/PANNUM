package com.example.student.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.view.*
import com.google.android.material.navigation.NavigationView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.NavHostFragment




class SongChooserFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(MySongsViewModel::class.java)}

    companion object {

        fun newInstance(): SongChooserFragment {
            return SongChooserFragment()
        }
    }
var dataAdded = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.song_chooser_fragment, container, false)
        val adapter = context?.let { SongAdapter(it) }

        val gridView = view.findViewById<GridView>(R.id.songsGridView)
        gridView.adapter = adapter

        viewModel.getAllSongs().observe(viewLifecycleOwner, Observer<List<SongEntity>> {
            adapter!!.clear()
            adapter.addAll(it)
        })


        val button = view.findViewById<Button>(R.id.chooserButton)
        button.setOnClickListener{
            dataAdded = if (dataAdded) {
                viewModel.addDefaultData()
                false
            } else{
                viewModel.deteleAllSongs()
                true
            }
        }

       /* adapter!!.addItemClickListener { song ->



        }*/
        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)
        playSongItem.isEnabled = true

        gridView.setOnItemClickListener { parent, view, position, id ->
            playSongItem.isEnabled = true
            playSongItem.isVisible = true

            val song = adapter!!.getItem(position) as SongEntity

            val args = bundleOf(
                "artist" to song.artist,
                "title" to song.title,
                "songResourceName" to song.songResourceName
            )
            findNavController().navigate(R.id.nav_played_song, args)
        }

        return view
    }

}