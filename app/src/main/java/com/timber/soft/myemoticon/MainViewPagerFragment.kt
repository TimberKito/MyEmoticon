package com.timber.soft.myemoticon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.timber.soft.myemoticon.model.ChildDataModel
import com.timber.soft.myemoticon.model.RootDataModel

class MainViewPagerFragment(private val rootModel: RootDataModel) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val recyclerViewList: RecyclerView = view.findViewById(R.id.recycler_list)
        recyclerViewList.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        val pagerAdapter = MainHomeCardAdapter(requireContext(),
            rootModel,
            object : MainHomeCardAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, childModel: ChildDataModel) {
//                    val intent = Intent(requireContext(), DetailsActivity::class.java)
//                    intent.putExtra("KEY_EXTRA", dataModel)
//                    startActivity(intent)
                    Log.d("onClick", "item has been click!")
                }
            })

        recyclerViewList.adapter = pagerAdapter
        return view
    }
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

    interface OnItemClickListener {
        fun onItemClick(position: Int, childModel: ChildDataModel)
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

        val cardImgAdapter = CardImgAdapter(context, childModel.previewList, childModel.count)

        holder.recyclerPreview.adapter = cardImgAdapter

        holder.rootCard.setOnClickListener() {
            listener.onItemClick(position, childModel)
        }
    }
}

class CardImgAdapter(
    private val context: Context,
    private val urlList: List<String>,
    private val imgCount: Int

) : RecyclerView.Adapter<CardImgAdapter.ImgViewHolder>() {

    inner class ImgViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {

    }

    override fun getItemCount(): Int {

    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {

    }

}