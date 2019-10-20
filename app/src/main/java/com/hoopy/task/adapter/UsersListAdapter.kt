package com.hoopy.task.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoopy.task.R
import com.hoopy.task.activity.FetchUserDetailsActivity
import com.hoopy.task.remote.response.User

/**
 * Created by kartikeysrivastava on 2019-10-20
 */
class UsersListAdapter(val context: Context) :RecyclerView.Adapter<UsersListAdapter.ViewHolder>(){
    var users: List<User> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val userView = inflater.inflate(R.layout.user_list_item, parent, false)

        // Return a new holder instance
        return ViewHolder(userView)    }

    override fun getItemCount(): Int {
       return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.userNameText.text = user.username
        holder.emailText.text = user.email
        holder.contactText.text = user.contact
        holder.profileName.text = user.name
        Glide.with(context)
            .load(user.image_url)
            .into(holder.profileImage)
        holder.editInfoButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                (context as FetchUserDetailsActivity).onItemClick(position)
            }
        })


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var profileImage: ImageView
        var profileName: TextView
        var emailText: TextView
        var contactText:TextView
        var userNameText:TextView
        var editInfoButton:ImageView
        init {
        profileImage = itemView.findViewById(R.id.profile_image)
            profileName = itemView.findViewById(R.id.profileNameText)
            contactText = itemView.findViewById(R.id.profileContactText)
            emailText = itemView.findViewById(R.id.profileEmailText)
            userNameText = itemView.findViewById(R.id.profileUserNameText)
            editInfoButton = itemView.findViewById(R.id.editInfoButtpm)
        }// Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
    }
}