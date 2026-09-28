package com.juancarlos.relacionautos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.juancarlos.relacionautos.data.Vehicle
import com.juancarlos.relacionautos.databinding.ItemVehicleBinding

class VehicleAdapter :
    ListAdapter<Vehicle, VehicleAdapter.Holder>(Diff) {

    object Diff : DiffUtil.ItemCallback<Vehicle>() {
        override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle) = oldItem == newItem
    }

    class Holder(val binding: ItemVehicleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemVehicleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.binding.txtChasis.text = item.chassis
        holder.binding.txtModelo.text = item.model
        holder.binding.txtMecanico.text = "Mecánico: ${item.mechanic}"
    }
}
