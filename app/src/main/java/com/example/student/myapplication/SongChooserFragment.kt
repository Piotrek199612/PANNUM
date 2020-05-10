package com.example.student.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.view.*
import com.google.android.material.navigation.NavigationView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import android.view.ContextMenu.ContextMenuInfo
import android.view.ContextMenu
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Toast
import kotlinx.android.synthetic.main.song_chooser_fragment.*

class SongChooserFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(MySongsViewModel::class.java)}
    private lateinit var application :MyApplication

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.song_chooser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = context?.let { SongAdapter(it) }
        songsGridView.adapter = adapter
        registerForContextMenu(songsGridView)

        viewModel.getAllSongs().observe(viewLifecycleOwner, Observer<List<SongEntity>> {
            adapter!!.clear()
            adapter.addAll(it)
        })

        val navigationView = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val playSongItem = navigationView.menu.findItem(R.id.nav_played_song)

        songsGridView.setOnItemClickListener { _, _, position, _ ->
            viewModel.songPlayed = true
            playSongItem.isVisible =  viewModel.songPlayed

            val song = adapter!!.getItem(position) as SongEntity
            viewModel.currentSong = song

            val args = bundleOf(
                "artist" to song.artist,
                "title" to song.title,
                "notes" to song.notes,
                "tacts" to song.tacts,
                "songResourceName" to song.songResourceName,
                "coverResourceName" to song.coverResourceName
            )
            viewModel.markPoint = 0
            viewModel.currentSong = song
            findNavController().navigate(R.id.nav_played_song, args)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.song_pressed_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val song = songsGridView.adapter.getItem(info.id.toInt()) as SongEntity
        var toastText =getString(R.string.cannot_remove_song_toast)
        if (viewModel.currentSong != song){
            viewModel.removeSong(song, context?.filesDir!!.absolutePath)
            toastText = getString(R.string.song_removed_toast)
        }
        val toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT)
        toast.show()
        return true
    }


}