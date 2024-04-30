package com.yellow.scan.linear.mole.fifthqr.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.databinding.FragmentLayoutMoreBinding
import com.yellow.scan.linear.mole.fifthqr.databinding.FragmentLayoutScanBinding
import com.yellow.scan.linear.mole.fifthqr.ui.mid.BackgroundListAdapter
import com.yellow.scan.linear.mole.fifthqr.utils.AppData

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentLayoutMoreBinding
    lateinit var moreAdapter: MoreListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layout_more, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMoreAdapter()
        clickFun()
    }

    private fun clickFun() {
        binding.bgType = AppData.bg_type
        binding.tvPop.setOnClickListener {
            setListData(1)
        }
        binding.tvTechnology.setOnClickListener {
            setListData(2)
        }
        binding.tvCartoon.setOnClickListener {
            setListData(3)
        }

    }

    private fun setListData(type: Int) {
        AppData.bg_type = type
        binding.bgType = AppData.bg_type
        when (type) {
            1 -> {
                moreAdapter.data = AppData.pop_ic
            }

            2 -> {
                moreAdapter.data = AppData.te_ic
            }

            3 -> {
                moreAdapter.data = AppData.cartoon_ic
            }
        }
        moreAdapter.notifyDataSetChanged()
    }

    private fun setMoreAdapter() {
        moreAdapter = MoreListAdapter()
        binding.recyclerMore.layoutManager = GridLayoutManager(activity, 3)
        binding.recyclerMore.adapter = moreAdapter
        moreAdapter.setOnItemClickListener(object : MoreListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                AppData.list_pos = position + 1
            }
        })
    }
}

class MoreListAdapter : RecyclerView.Adapter<MoreListAdapter.ViewHolder>() {
    var data = AppData.getMoreIcDataList()
    var selectedItemPositionFun: Int = 0

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_bg, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgQrBg.setImageResource(data[position])
        holder.isSelected = position == selectedItemPositionFun
        holder.imgCheek.visibility = if (holder.isSelected) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (!holder.isSelected) {
                setSelectedItemPosition(holder.adapterPosition)
            }
        }
    }

    private fun setSelectedItemPosition(position: Int) {
        val oldPosition = selectedItemPositionFun
        selectedItemPositionFun = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedItemPositionFun)
        listener?.onItemClick(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgQrBg: ImageView = itemView.findViewById(R.id.img_qr_bg)
        val imgCheek: ImageView = itemView.findViewById(R.id.img_cheek)
        var isSelected: Boolean = false
    }


    override fun getItemCount(): Int {
        return data.size
    }

}
