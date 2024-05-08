package com.yellow.scan.linear.mole.fifthqr.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yellow.scan.linear.mole.fifthqr.R
import com.yellow.scan.linear.mole.fifthqr.base.App
import com.yellow.scan.linear.mole.fifthqr.base.QrAdLoad
import com.yellow.scan.linear.mole.fifthqr.databinding.FragmentLayoutCreateBinding
import com.yellow.scan.linear.mole.fifthqr.databinding.FragmentLayoutScanBinding
import com.yellow.scan.linear.mole.fifthqr.ui.end.EndActivity
import com.yellow.scan.linear.mole.fifthqr.ui.main.MainActivity
import com.yellow.scan.linear.mole.fifthqr.ui.mid.MidActivity
import com.yellow.scan.linear.mole.fifthqr.utils.AppData
import com.yellow.scan.linear.mole.fifthqr.utils.NetHelp

class CreateFragment : Fragment() {
    private lateinit var binding: FragmentLayoutCreateBinding
    private lateinit var adapter: CreteListAdapter
    private var qrText = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_layout_create, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditListener()
        QrAdLoad.loadOf(AppData.QR_BACK_MAIN)
        QrAdLoad.loadOf(AppData.QR_CLICK_CREATE)
        NetHelp.postPotNet(App.getAppContext(), "scan2")
        adapter = CreteListAdapter()
        binding.rvCrete.adapter = adapter
        binding.rvCrete.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter.setOnItemClickListener(object : CreteListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                binding.creteType = position + 1
            }
        })
        binding.include5.tvWep.setOnClickListener {
            binding.include5.passwordType = 1
        }
        binding.include5.tvWpa.setOnClickListener {
            binding.include5.passwordType = 2
        }
        binding.include5.tvNone.setOnClickListener {
            binding.include5.passwordType = 3
        }
        binding.tvCreate.setOnClickListener {
            if (setCreteButton()) {
                (activity as MainActivity).showCreteAdFun()
            } else {
                Toast.makeText(activity, "Please fill in the information", Toast.LENGTH_SHORT)
                    .show()
            }
            NetHelp.postPotNet(App.getAppContext(), "scan17","qr",getQrType())
        }
    }

    fun jumToMidActivity() {
        val intent = Intent(activity, MidActivity::class.java)
        getQrTextData()
        intent.putExtra("mid_qr", qrText)
        startActivity(intent)
    }
    fun getQrType(): String {
        return when(binding?.creteType!!){
            1 -> "Text"
            2 -> "URL"
            3 -> "Location"
            4 -> "Email"
            5 -> "Wifi"
            else -> "Text"

        }
    }
    private fun getQrTextData() {
        when (binding?.creteType!!) {
            1 -> {
                binding.include1.etContent1.apply {
                    qrText = text.toString()
                }
            }

            2 -> {
                binding.include2.etContentUrl.apply {
                    qrText = text.toString()
                }
            }

            3 -> {
                binding.include3.apply {
                    qrText =
                        "${etContentCountry.text}\n${etContentCity.text}\n${etContentLongitude.text}\n${etContentLatitude.text}"
                }
            }

            4 -> {
                binding.include4.apply {
                    qrText = "${etContentEmail.text}\n${etContent.text}"

                }
            }

            5 -> {
                binding.include5.apply {
                    val password = when (passwordType) {
                        1 -> "Encryption：WEP"
                        2 -> "Encryption：WPA/WPA2"
                        else -> "Encryption：NONE"
                    }
                    qrText = "${etContentNetworkName.text}\n${etContentPassword.text}\n$password"

                }
            }

            else -> {
                qrText = ""
            }
        }

    }

    private fun setCreteButton(): Boolean {
        when (binding?.creteType!!) {
            1 -> {
                binding.include1.etContent1.apply {
                    return text.toString().isNotEmpty()
                }
            }

            2 -> {
                binding.include2.etContentUrl.apply {
                    return text.toString().isNotEmpty()
                }
            }

            3 -> {
                binding.include3.apply {
                    return etContentCountry.text.toString()
                        .isNotEmpty() && etContentCity.text.toString()
                        .isNotEmpty() && etContentLongitude.text.toString()
                        .isNotEmpty() && etContentLatitude.text.toString().isNotEmpty()
                }
            }

            4 -> {
                binding.include4.apply {
                    return etContentEmail.text.toString()
                        .isNotEmpty() && etContent.text.toString()
                        .isNotEmpty()
                }
            }

            5 -> {
                binding.include5.apply {
                    return etContentNetworkName.text.toString()
                        .isNotEmpty() && etContentPassword.text.toString()
                        .isNotEmpty()
                }
            }

            else -> {
                return false
            }
        }
    }

    private fun setEditListener() {
        binding.creteType = 1
        binding.include5.passwordType = 1
    }
}

class CreteListAdapter : RecyclerView.Adapter<CreteListAdapter.ViewHolder>() {
    private var data = AppData.getQrCreteData()
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
        val view = inflater.inflate(R.layout.item_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItem.text = data[position]
        holder.isSelected = position == selectedItemPositionFun
        holder.llItem.background = if (holder.isSelected) {
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_item_check)
        } else {
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_item_dis)
        }
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
        val tvItem: TextView = itemView.findViewById(R.id.tv_text)
        val llItem: LinearLayout = itemView.findViewById(R.id.ll_item)
        var isSelected: Boolean = false

    }


    override fun getItemCount(): Int {
        return data.size
    }

}