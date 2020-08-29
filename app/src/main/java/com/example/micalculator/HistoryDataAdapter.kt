package com.example.micalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryDataAdapter(private val HistoryList: ArrayList<HistoryData>):
        RecyclerView.Adapter<HistoryDataAdapter.ViewHolder>(){



    class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.history_item, parent, false)){

        val inputTextView: TextView = itemView.findViewById<View>(R.id.inputExpression) as TextView
        val answerTextView: TextView = itemView.findViewById<View>(R.id.outputExpression) as TextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context), parent)   //passed as parameter to the viewHolder object
    }

    override fun getItemCount(): Int {
        return HistoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyInput = HistoryList[position]
        holder.inputTextView.text = historyInput.inputExpression
        holder.answerTextView.text = historyInput.outputExpression

    }




}
//
//class HistoryDiffUtil: DiffUtil.ItemCallback<HistoryData>(){
//    override fun areItemsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
//        if(true){
//            oldItem.inputExpression === newItem.inputExpression
//            oldItem.outputExpression === newItem.outputExpression
//            return false
//        }else{
//            return true
//        }
//
//    }
//
//    override fun areContentsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
//        return oldItem == newItem
//    }
//
//}
