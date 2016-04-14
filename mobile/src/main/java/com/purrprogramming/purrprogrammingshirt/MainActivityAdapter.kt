package com.purrprogramming.purrprogrammingshirt

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Lance Gleason of Polyglot Programming LLC. on 4/12/16.
 * http://www.polyglotprogramminginc.com
 * https://github.com/lgleasain
 * Twitter: @lgleasain
 */
class MainActivityAdapter// Provide a suitable constructor (depends on the kind of dataset)
(private val mDataset: Array<Int>, private var mActivity: Activity) : RecyclerView.Adapter<MainActivityAdapter.ViewHolder>() {
    val SERVICE_NOTIFICATION = "com.polyglotprogramminginc.purrprogramming.SERVICE_NOTIFICATION"
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var mImageView: ImageView
        var mTextView: TextView
        var mCardView: CardView

        init {
            mTextView = v.findViewById(R.id.info_text) as TextView
            mImageView = v.findViewById(R.id.imageView) as ImageView
            mCardView = v.findViewById(R.id.card_view) as CardView
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                           viewType: Int): MainActivityAdapter.ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.main_activity_card, parent, false)
        // set the view's size, margins, paddings and layout parameters
        val vh = ViewHolder(v)
        return vh
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.text = "hello"
        //holder.mImageView.setImageResource(R.mipmap.flag_of_south_africa)
        //holder.mTextView.text = mDataset[position]
        holder.mImageView.setImageResource(mDataset[position])
        holder.mCardView.setOnClickListener {
            var command: String = "purr"
            if(mDataset[position] == R.mipmap.flag_of_south_africa) {
                command = "south_africa"
            }else if(mDataset[position] == R.mipmap.icon_ruby_2f9d22cf){
                command = "ruby"
            }
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", command)
            mActivity.sendBroadcast(i)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    //val itemCount: Int
    //override     get() = mDataset.size

    override fun getItemCount(): Int {
        return mDataset.size
    }
}
