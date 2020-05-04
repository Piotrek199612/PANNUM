package com.example.student.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*

class AddSongFragment : Fragment() {

    companion object {

        fun newInstance(): AddSongFragment {
            return AddSongFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_song_fragment, container, false)

    }

}