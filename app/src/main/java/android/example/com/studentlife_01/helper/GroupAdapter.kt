package android.example.com.studentlife_01.helper

import android.content.Context
import android.content.Intent
import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.activity.GroupDetailsActivity
import android.example.com.studentlife_01.model.Group
import android.example.com.studentlife_01.model.Notice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_notice_item_layout.view.*
import kotlinx.android.synthetic.main.group_item_layout.view.*

class GroupAdapter( private val groupList: ArrayList<Group>,val context: Context) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.group_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.groupName.text = groupList.get(position).name
        holder.groupCreatedAt?.text = "Created At: " + groupList.get(position).created_at
        holder.itemView.setOnClickListener{
            val intent = Intent(context,GroupDetailsActivity::class.java)

            val bundle = Bundle()

            bundle.putString("id",groupList.get(position).id)
            bundle.putString("name",groupList.get(position).name)
            intent.putExtras(bundle)
           startActivity(context,intent,null)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupName = view.tv_GroupName
        val groupCreatedAt = view.tv_createdAt

    }
}