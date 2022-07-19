package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentBannerBinding
import com.example.flo.databinding.FragmentPannelBinding
import com.google.android.material.tabs.TabLayoutMediator

class PannelFragment(val imgRes : Int) : Fragment() {

    lateinit var binding : FragmentPannelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPannelBinding.inflate(inflater, container, false)

        binding.pannelImageIv.setImageResource(imgRes)
        return binding.root
    }
}