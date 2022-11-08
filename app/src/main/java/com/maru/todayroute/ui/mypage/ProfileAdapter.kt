package com.maru.todayroute.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.maru.todayroute.R
import com.maru.todayroute.databinding.FragmentRouteBinding
import com.maru.todayroute.ui.calendar.CalendarFragmentDirections
import com.maru.todayroute.ui.calendar.Route
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent

class ProfileAdapter (private val profileList: ArrayList<Profiles>): RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coule_list, parent, false)

        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos: Int = adapterPosition
                val route: Profiles = profileList.get(curPos)

            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.image.setImageResource(R.drawable.brown)
        holder.name.text = coupleList.get(position).name
        holder.birth.text = coupleList.get(position).birth
        holder.gender.text = coupleList.get(position).gender
    }

    override fun getItemCount(): Int {
        return coupleList.size
    }


    class CustomViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val image = itemview.findViewById<ImageView>(R.id.iv_profile_iamge)
        val name = itemview.findViewById<TextView>(R.id.tv_profile_name)
        val birth = itemview.findViewById<TextView>(R.id.tv_profile_birth)
        val gender = itemview.findViewById<TextView>(R.id.tv_profile_gender)
    }
}