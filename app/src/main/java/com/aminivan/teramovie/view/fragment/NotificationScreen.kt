package com.aminivan.teramovie.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aminivan.teramovie.R
import com.aminivan.teramovie.databinding.FragmentNotificationScreenBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationScreen : BottomSheetDialogFragment() {

    private var _binding: FragmentNotificationScreenBinding?          = null
    private val binding get()                                         = _binding!!
    private lateinit var listener: OnItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            listener.onItemClick()
            dismiss()
        }
    }

    interface OnItemClickListener {
        fun onItemClick()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}