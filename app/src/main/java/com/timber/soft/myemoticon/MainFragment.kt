package com.timber.soft.myemoticon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.timber.soft.myemoticon.model.ChildDataModel
import com.timber.soft.myemoticon.model.RootDataModel
import com.timber.soft.myemoticon.tools.AppVal

class MainViewPagerFragment(private val rootModel: RootDataModel) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val recyclerViewList: RecyclerView = view.findViewById(R.id.recycler_list)
        recyclerViewList.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        val pagerAdapter = MainHomeCardAdapter(
            requireContext(),
            rootModel,
            OnItemClickListenerImpl(requireContext())
            )

        recyclerViewList.adapter = pagerAdapter
        return view
    }

}

class OnItemClickListenerImpl(private val mcontext: Context) : OnItemClickListener {
    override fun onItemClick(position: Int, childModel: ChildDataModel) {
        val intent = Intent(mcontext, SetDetailsActivity::class.java)
        intent.putExtra(AppVal.KEY_EXTRA, childModel)
        startActivity(mcontext, intent, null)
        Log.d("onClick", "item has been click!")
    }

}

interface OnItemClickListener {
    fun onItemClick(position: Int, childModel: ChildDataModel)
}

class MainHomeCardAdapter(
    private val context: Context,
    private val model: RootDataModel,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MainHomeCardAdapter.PreViewHolder>() {
    private val childModels = model.childList

    inner class PreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconName: TextView = itemView.findViewById(R.id.icon_name)
        val rootCard: CardView = itemView.findViewById(R.id.item_home_car)
        val recyclerPreview: RecyclerView = itemView.findViewById(R.id.recycler_preview)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_home_card, parent, false)
        return PreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return childModels.size
    }

    override fun onBindViewHolder(holder: PreViewHolder, position: Int) {
        val childModel = childModels[position]
        holder.iconName.text = childModel.identifierName

        holder.recyclerPreview.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)

        val customList = mutableListOf<String>()
        customList.addAll(childModel.previewList)
        customList.add("xxx")

        val cardImgAdapter = CardImgAdapter(
            context,
            customList,
            childModel.count,
            childModel,
            OnItemClickListenerImpl(context)
            )

        holder.recyclerPreview.adapter = cardImgAdapter

        holder.rootCard.setOnClickListener() {
            listener.onItemClick(position, childModel)
        }
    }
}

class CardImgAdapter(
    private val context: Context,
    private val urlList: List<String>,
    private val imgCount: Int,
    private val childModel: ChildDataModel,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CardImgAdapter.ImgViewHolder>() {

    inner class ImgViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutRoot: RelativeLayout = itemView.findViewById(R.id.relayout_root)
        val preCardImg: ImageView = itemView.findViewById(R.id.pre_card_img)
        val preCardCount: TextView = itemView.findViewById(R.id.pre_car_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_home_card_img, parent, false)
        return ImgViewHolder(view)
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val preUrl: String = urlList[position]

        if (preUrl != "xxx"){
            try {
                Glide.with(context).load(preUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.preCardImg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (position == urlList.size - 1) {
            holder.preCardCount.visibility = View.VISIBLE
            holder.preCardCount.text = "+" + imgCount
        } else {
            holder.preCardCount.visibility = View.GONE
        }

        holder.layoutRoot.setOnClickListener() {
            listener.onItemClick(position, childModel)
        }
    }

}