package android.example.com.studentlife_01.helper

import android.content.Context
import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.CustomVolleyRequest
import android.example.com.studentlife_01.app.VolleySingleton
import android.example.com.studentlife_01.model.Notice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.fragment_notice_item_layout.view.*
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.toolbox.NetworkImageView




class ChapterAdapter(context: Context, private val noticeList: ArrayList<Notice>) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {
val context:Context = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.fragment_notice_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.noticeTitle?.text = noticeList.get(position).title
        holder.noticeContent?.text = noticeList.get(position).content

        val btnDelete = holder.itemView.findViewById<Button>(R.id.btn_delete)

        btnDelete.setOnClickListener {
           // Toast.makeText(holder, noticeList.get(position).title, Toast.LENGTH_LONG).show()
            deleteNotice(position)
            noticeList.removeAt(position)
            notifyItemRemoved(position)
        }

        //NetworkImageView
        val networkImageView = holder.itemView.findViewById<NetworkImageView>(R.id.volleyImageView)
        if(noticeList.get(position).document_path!="null")
            networkImageView.visibility = View.VISIBLE
        val cvr = CustomVolleyRequest(context)

        val imageLoader =  cvr.getImageLoader()


        //val imageLoader = CustomVolleyRequest.customVolleyRequest?.getInstance(context)?.getImageLoader()
        Log.d("myinfotags","path:" + noticeList.get(position).document_path)
        val il = ImageLoader.getImageListener(networkImageView,R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert)
        imageLoader.get(noticeList.get(position).document_path, object : ImageLoader.ImageListener {
            override fun onErrorResponse(error: VolleyError) {
                //an error ocurred
                Log.d("myerrortags","Image error: " + error.toString())
                networkImageView.setImageResource(android.R.drawable.ic_dialog_alert)
            }

            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                if (response.bitmap != null) {

                } else {

                }
            }
        })
        //imageLoader?.get(noticeList.get(position).document_path, il)

        //Setting the image url to load
       networkImageView.setImageUrl(noticeList.get(position).document_path,imageLoader)
    }

    fun deleteNotice(position:Int){
        // Tag used to cancel the request
        val tag_string_req = "req_notices"
        val notice_id =  noticeList[position].id
        Log.d("myerrorTags", "Notice id: "+notice_id);
        val stringRequest = object : StringRequest(
            Method.GET, AppConfig.URL_DELETE_NOTICE + notice_id,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "NOTICE Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")

                    if(error!=false){
                        Log.d("myerrorTags", obj.getJSONArray("notices").toString());
                        //---- Success

                        notifyDataSetChanged()
                    } else {
                        Log.d("myerrorTags", "else");
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {

                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("id",notice_id)

                return params
            }
        }

        //adding request to queue
      // VolleySingleton.instance?.addToRequestQueue(stringRequest)
        val cvr = CustomVolleyRequest(context)
        cvr.addToRequestQueue(stringRequest)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noticeTitle = view.tvNoticeTitle
        val noticeContent = view.tv_NoticeContent

    }


}