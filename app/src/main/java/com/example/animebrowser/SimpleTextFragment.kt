package com.example.animebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class SimpleTextFragment(private val text: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textView = TextView(requireContext())
        textView.text = text
        textView.textSize = 24f
        textView.setTextColor(android.graphics.Color.BLACK)
        textView.gravity = android.view.Gravity.CENTER
        return textView
    }
}