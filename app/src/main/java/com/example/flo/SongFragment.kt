package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentDetailBinding
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding : FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSongBinding.inflate(inflater, container, false)
        binding.songLalacLayout.setOnClickListener {
            Toast.makeText(activity, "LILAC - 아이유(IU)", Toast.LENGTH_SHORT).show()
        }

        binding.songFluLayout.setOnClickListener {
            Toast.makeText(activity, "Flu - 아이유(IU)", Toast.LENGTH_SHORT).show()
        }

        binding.songCoinLayout.setOnClickListener {
            Toast.makeText(activity, "Coin - 아이유(IU)", Toast.LENGTH_SHORT).show()
        }

        binding.songQhaqhaLayout.setOnClickListener {
            Toast.makeText(activity, "봄안녕봄 - 아이유(IU)", Toast.LENGTH_SHORT).show()
        }

        binding.songQhaqha2Layout.setOnClickListener {
            Toast.makeText(activity, "Celebrity - 아이유(IU)", Toast.LENGTH_SHORT).show()
        }

        binding.toggleOnIv.setOnClickListener {
            setCheckstatus(false)
        }

        binding.toggleOffIv.setOnClickListener {
            setCheckstatus(true)
        }

        return binding.root
    }

    fun setCheckstatus(isChecked : Boolean){
        if(isChecked){
            binding.toggleOnIv.visibility = View.VISIBLE
            binding.toggleOffIv.visibility = View.GONE
        }

        else{
            binding.toggleOnIv.visibility = View.GONE
            binding.toggleOffIv.visibility = View.VISIBLE
        }
    }
}