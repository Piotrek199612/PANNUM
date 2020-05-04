package com.example.student.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.view.*
import com.google.android.material.navigation.NavigationView
import android.widget.GridView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import android.widget.AdapterView
import android.view.ContextMenu.ContextMenuInfo
import android.view.ContextMenu
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Toast
import androidx.core.view.get
import kotlinx.android.synthetic.main.my_song_row.view.*
import kotlinx.android.synthetic.main.song_chooser_fragment.*


class SongChooserFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(MySongsViewModel::class.java)}

    companion object {

        fun newInstance(): SongChooserFragment {
            return SongChooserFragment()
        }
    }
var dataAdded = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.song_chooser_fragment, container, false)
        val adapter = context?.let { SongAdapter(it) }

        val gridView = view.findViewById<GridView>(R.id.songsGridView)
        gridView.adapter = adapter
        registerForContextMenu(gridView)

        viewModel.getAllSongs().observe(viewLifecycleOwner, Observer<List<SongEntity>> {
            adapter!!.clear()
            adapter.addAll(it)
        })


        val button = view.findViewById<Button>(R.id.chooserButton)
        button.setOnClickListener{
            dataAdded = if (dataAdded) {
                viewModel.addDefaultData(context!!.assets)
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
        //playSongItem.isEnabled = true

        gridView.setOnItemClickListener { _, view, position, _ ->
            //playSongItem.isEnabled = true
            playSongItem.isVisible = true

            val song = adapter!!.getItem(position) as SongEntity

            val args = bundleOf(
                "artist" to song.artist,
                "title" to song.title,
                "notes" to song.notes,
                "tacts" to song.tacts,
                "songResourceName" to song.songResourceName,
                "coverResourceName" to song.coverResourceName
            )
            findNavController().navigate(R.id.nav_played_song, args)
        }

        return view
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.song_pressed_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val song = songsGridView.adapter.getItem(info.id.toInt()) as SongEntity
        viewModel.removeSong(song)
        val toast = Toast.makeText(context, "Song succesfully removed", Toast.LENGTH_SHORT)
        toast.show()
        return true
    }


}