package com.bpmn.editor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bpmn.editor.R
import com.bpmn.editor.databinding.SchemeItemBinding
import com.bpmn.editor.model.Scheme


class SchemeAdapter(val listener: Listener) : RecyclerView.Adapter<SchemeAdapter.SchemeHolder>() {
    private val schemeList = ArrayList<Scheme>()

    interface Listener {
        fun onClickMove(scheme: Scheme)
        fun onClickDel(scheme: Scheme)
    }


    fun deleteAt(position: Int): Scheme {
        var scheme = schemeList[position]
        schemeList.removeAt(position)
        return scheme
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchemeHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.scheme_item, parent, false)
        return SchemeHolder(view)
    }

    override fun getItemCount(): Int {
        return schemeList.size
    }

    fun clear() {
        schemeList.clear()
    }

    override fun onBindViewHolder(holder: SchemeHolder, position: Int) {
        holder.bind(schemeList[position], listener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addScheme(scheme: Scheme) {
        schemeList.add(scheme)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun delete(scheme: Scheme) {
        schemeList.remove(scheme)
        notifyDataSetChanged()
    }
    class SchemeHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = SchemeItemBinding.bind(item)

        @SuppressLint("SetTextI18n")
        fun bind(scheme: Scheme, listener: SchemeAdapter.Listener) = with(binding) {
            schemeName.text = scheme.name
            schemeDate.text = scheme.date
            //binding.imageView2.setIma
            settingButton.setOnClickListener {
                listener.onClickDel(scheme)
            }
            schemeFrame.setOnClickListener {
                listener.onClickMove(scheme)
            }
        }
    }
}

