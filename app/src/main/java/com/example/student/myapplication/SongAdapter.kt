package com.example.student.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class PracownikAdapter(context: Context): ArrayAdapter<SongEntity>(context, R.layout.my_song_row) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val song = getItem(position)

        var view = convertView
        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.my_song_row, parent, false)

//        TODO: odkomentuj poniższy kod po ukończeniu TODO w pliku my_pracownik_row
        val artistTv = view!!.findViewById<TextView>(R.id.artistText)
            artistTv.text = song!!.artist

        val titleTv = view.findViewById<TextView>(R.id.titleText)
            titleTv.text = song.title

        val yearTv = view.findViewById<TextView>(R.id.yearText)
        yearTv.text = song.year.toString()

        val playedTv = view.findViewById<TextView>(R.id.playedText)
        playedTv.text = song.played.toString()

        val coverView = view!!.findViewById<ImageView>(R.id.coverView)
        context.resources.getIdentifier(song.coverResourceName, "drawable", context.packageName)
        coverView.setImageResource(
            R.drawable.sevennationarmy)




        return view!!
    }
}