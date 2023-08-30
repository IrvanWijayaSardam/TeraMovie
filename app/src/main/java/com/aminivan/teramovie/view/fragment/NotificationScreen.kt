package com.aminivan.teramovie.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.aminivan.teramovie.R
import com.aminivan.teramovie.databinding.FragmentNotificationScreenBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationScreen : BottomSheetDialogFragment() {

    private var _binding: FragmentNotificationScreenBinding?          = null
    private val binding get()                                         = _binding!!
    private lateinit var listener: OnItemClickListener
    private var dismissListener: (() -> Unit)? = null

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
        lifecycleScope.launch {
            coroutineScope {
                launch {
                    while (true) {
                        delay(10000)
                        dismiss()
                    }
                }
            }
        }

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

    fun setDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }
}