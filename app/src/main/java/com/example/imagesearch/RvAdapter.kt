package com.example.imagesearch

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.imagesearch.databinding.RvItemBinding

class RvAdapter(private val items: List<RvModel>) : RecyclerView.Adapter<RvAdapter.MyViewHolder>() {

    interface ItemClick {
        fun onClick(view : View, position : Int)
    }
    var itemClick : ItemClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.thumbnail)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        holder.siteNameTextView.text = item.sitename
        holder.dateTextView.text = item.datetime

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }

        if (position % 2 == 0) {
            holder.itemView.setPadding(
                holder.itemView.paddingLeft,
                holder.itemView.paddingTop,
                0,
                holder.itemView.paddingBottom
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(private val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.itemImage
        val siteNameTextView = binding.itemName
        val dateTextView = binding.itemDate
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClick?.onClick(it, position)
                }
            }
        }
    }
}
class ItemSpacingDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spacing
        outRect.right = spacing
        outRect.top = spacing
        if (parent.getChildLayoutPosition(view) % 2 == 0) { outRect.right = 0 }
    }
}