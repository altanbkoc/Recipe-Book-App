package com.altankoc.yemekkitab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.altankoc.yemekkitab.databinding.RecyclerRowBinding
import com.altankoc.yemekkitab.model.Yemek
import com.altankoc.yemekkitab.view.ListeFragmentDirections

class YemekAdapter(val yemekListesi: List<Yemek>): RecyclerView.Adapter<YemekAdapter.YemekHolder>() {

    class YemekHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {

        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return YemekHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {

        holder.binding.recyclerTextView.text = yemekListesi[position].isim
        holder.itemView.setOnClickListener {
            val action = ListeFragmentDirections.actionListeFragmentToDetayFragment(bilgi = yemekListesi[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}