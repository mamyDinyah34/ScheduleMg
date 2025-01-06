package com.mamydinyah.schedulemg.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ArrayAdapter

class TaskAdapter(context: Context, tasks: List<Task>) :
    ArrayAdapter<Task>(context, 0, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            android.R.layout.simple_list_item_2, parent, false
        )

        val task = getItem(position)
        val titleView = view.findViewById<TextView>(android.R.id.text1)
        val descView = view.findViewById<TextView>(android.R.id.text2)

        titleView.text = task?.title ?: "Sans titre"
        descView.text = task?.description ?: "Sans description"

        return view
    }
}
