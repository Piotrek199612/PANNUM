package com.example.kjankiewicz.android_03c01


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class MyFragmentMain : Fragment() {

    internal var buttonOne: Button? = null
    internal var buttonTwo: Button? = null
    internal var buttonThree: Button? = null

    internal var mListener: ButtonPressListener? = null

    interface ButtonPressListener {
        fun onButtonPressed(button: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_fragment_main, container, false);

        /* DONE: Przypisz do atrybutów klasy Button odpowiednie elementy z definicji rozkładu,
           która w powyższej instrukcji została załadowana. Skorzystaj z poniższego fragmentu kodu
           button = view.findViewById(identyfikator_przycisku) as Button
         */
        buttonOne = view.findViewById(R.id.button_one)
        buttonTwo = view.findViewById(R.id.button_two)
        buttonThree = view.findViewById(R.id.button_three)


        /* DONE: Przypisz do poszczególnych przycisków instancje odbiornika (listenera), który zareaguje
           na wybranie przycisku. Efekt powinien polegać na wywołaniu metody onButtonPressed
           w ramach obiektu przypisanego do atrybutu mListener.
           W zależności od przycisku przekaż do metody odpowiedni parametr (1,2,3).
           Skorzystaj z poniższego przykładowego fragmentu kodu

           button.setOnClickListener { . . . }
         */
        buttonOne?.setOnClickListener {
            mListener?.onButtonPressed(1)
        }

        buttonTwo?.setOnClickListener {
            mListener?.onButtonPressed(2)
        }

        buttonThree?.setOnClickListener {
            mListener?.onButtonPressed(3)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        /* DONE: Usuń poniższy komentarz, aby do atrybutu mListener móc przypisać aktywność,
           która dołączyła bieżący fragment, weryfikując jednocześnie czy aktywność ta
           implementuje wymagany interfejs */
        if (context is ButtonPressListener)
            mListener = context
        else
            throw ClassCastException("$context must implement ButtonPressListener interface")

    }


}// Required empty public constructor
