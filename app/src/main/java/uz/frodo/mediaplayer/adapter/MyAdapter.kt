package uz.frodo.mediaplayer.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import uz.frodo.mediaplayer.MediaPlayActivity
import uz.frodo.mediaplayer.MyMediaPlayer
import uz.frodo.mediaplayer.R
import uz.frodo.mediaplayer.databinding.ItemBinding
import uz.frodo.mediaplayer.models.Song
import java.lang.Exception

class MyAdapter(val context: Context,var list:ArrayList<Song>):RecyclerView.Adapter<MyAdapter.VH>() {
    class VH(val binding: ItemBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VH(b)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val song = list[position]
        holder.binding.itemArtistName.text = song.artist
        holder.binding.itemSongName.text = song.name
        if (song.image != null) {
            holder.binding.itemImage.setImageBitmap(song.image)
        } else {
            holder.binding.itemImage.setImageResource(R.drawable.img)
        }

        if (MyMediaPlayer.currentIndex == position){
            holder.binding.itemArtistName.setTextColor(context.getColor(R.color.main))
            holder.binding.itemSongName.setTextColor(context.getColor(R.color.main))
        }else{
            holder.binding.itemArtistName.setTextColor(context.getColor(R.color.black))
            holder.binding.itemSongName.setTextColor(context.getColor(R.color.black))
        }

        holder.binding.itemContainer.setOnClickListener {
            MyMediaPlayer.list = list
            if (MyMediaPlayer.currentIndex == position){
                context.startActivity(Intent(context,MediaPlayActivity::class.java).putExtra("again",true))
            }else{
                MyMediaPlayer.currentIndex = position
                context.startActivity(Intent(context,MediaPlayActivity::class.java).putExtra("again",false))
            }
        }
    }



    fun updateList(searchList:ArrayList<Song>){
        list = searchList
        notifyDataSetChanged()
    }
}