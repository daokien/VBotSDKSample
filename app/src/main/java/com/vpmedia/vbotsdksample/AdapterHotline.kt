package com.vpmedia.vbotsdksample

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vpmedia.sdkvbot.domain.pojo.mo.Hotline
import com.vpmedia.vbotsdksample.databinding.ItemHotlineBinding


class AdapterHotline(private var project: MutableList<Hotline>) :
    RecyclerView.Adapter<AdapterHotline.ViewHolder>() {
    private var listener: AdapterContactListener? = null

    interface AdapterContactListener {
        fun onclickHotline(hotline: Hotline)
    }

    fun setListener(listener: AdapterContactListener) {
        this.listener = listener
    }

    fun setData(project: List<Hotline>) {
        this.project.clear()
        this.project.addAll(project)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHotlineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.bind(project[position])
            holder.itemView.setOnClickListener {
                listener!!.onclickHotline(project[position])
            }
//            if (project.size == position + 1) {
//                holder.itemView.vView.visibility = View.GONE
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return project.size
    }


    inner class ViewHolder(var viewBinding: ItemHotlineBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: Hotline) = with(itemView) {
            try {
                viewBinding.tvNumberHotline.text = model.name
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}