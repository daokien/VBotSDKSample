package com.vpmedia.vbotsdksample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.vpmedia.sdkvbot.domain.pojo.mo.Hotline
import com.vpmedia.vbotsdksample.databinding.FragmentChooseHotlineBinding

class ChooseHotline : SuperBottomSheetFragment(), AdapterHotline.AdapterContactListener {

    private lateinit var binding: FragmentChooseHotlineBinding
    private lateinit var adapterHotline: AdapterHotline
    private lateinit var mListener: ListenerBottomSheet
    private lateinit var lHotline: List<Hotline>

    interface ListenerBottomSheet {
        fun onClickHotline(hotline: Hotline)
    }

    fun setListener(listener: ListenerBottomSheet, listHotLine: List<Hotline>) {
        this.mListener = listener
        lHotline = listHotLine
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        binding = FragmentChooseHotlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        adapterHotline.setData(lHotline)
    }

    private fun initAdapter() {
        adapterHotline = AdapterHotline(mutableListOf())
        adapterHotline.setListener(this)
        binding.rvChooseHotline.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterHotline
        }
    }

    override fun onclickHotline(hotline: Hotline) {
        mListener.onClickHotline(hotline)
        this.dismiss()
    }

}