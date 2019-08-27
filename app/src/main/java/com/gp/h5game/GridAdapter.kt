package com.gp.h5game

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.layout_game.view.*

class GridAdapter(context: Context, private val gameList: List<Game>) : BaseAdapter() {
    var context: Context
    init {
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val gameView = LayoutInflater.from(context).inflate(R.layout.layout_game, null)
        gameView.image.setImageResource(gameList[position].resId)
        gameView.text.text = gameList[position].name
        return gameView
    }

    override fun getCount(): Int {
        return gameList.size
    }

    override fun getItem(position: Int): Any {
        return gameList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}