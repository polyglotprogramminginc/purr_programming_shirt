package com.purrprogramming.purrprogrammingshirt

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {
    lateinit var mainRecycler: RecyclerView
    lateinit var mainLayoutManager: RecyclerView.LayoutManager
    var dataSet: Array<Int> = arrayOf(R.mipmap.flag_of_south_africa, R.mipmap.icon_ruby_2f9d22cf,  R.mipmap.purr_programming)


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView : View = inflater!!.inflate(R.layout.fragment_main, container, false)

        mainRecycler = rootView.findViewById(R.id.main_recycler) as RecyclerView
        mainRecycler.setHasFixedSize(true)
        mainLayoutManager = LinearLayoutManager(activity)
        mainRecycler.layoutManager = mainLayoutManager

        mainRecycler.adapter = MainActivityAdapter(dataSet, activity)
        return rootView
    }
}
