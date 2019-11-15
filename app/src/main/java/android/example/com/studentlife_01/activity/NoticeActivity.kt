package android.example.com.studentlife_01.activity

import android.content.Intent
import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.VolleySingleton
import android.example.com.studentlife_01.helper.ChapterAdapter
import android.example.com.studentlife_01.helper.NoticeDialog
import android.example.com.studentlife_01.helper.SQLiteHandler
import android.example.com.studentlife_01.model.Notice
import android.example.com.studentlife_01.model.User
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_notice.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NoticeActivity : AppCompatActivity(), NoticeDialog.NoticeDialogListener {

    val noticeList: ArrayList<Notice> = ArrayList()
    lateinit var dialogBuilder: NoticeDialog

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var db: SQLiteHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // SQLite database handler
        db = SQLiteHandler(applicationContext,null)
//        noticeList.add(
//        Notice(
//            "Android MVP Introduction",
//            "Mauris arcu ipsum, molestie ac porttitor at, faucibus sit amet sapien. Proin quam lacus, blandit nec ultricies et, commodo nec nunc."
//        )
//        )
//        noticeList.add(
//            Notice(
//                "Learn RxJava",
//                "Donec et mi viverra, lobortis neque sed, congue erat. Aliquam in malesuada nulla, et tristique sem. Fusce tincidunt erat libero,"
//            )
//        )
//        noticeList.add(
//            Notice(
//                "Advance Kotlin",
//                "sit arcu nulla mollis nibh, ut tincidunt nibh nunc in eros. Duis blandit consectetur eros non pellentesque. Curabitur in laoreet ipsum"
//            )
//        )
        layoutManager = LinearLayoutManager(this)
        rvChapterList.layoutManager = layoutManager
        rvChapterList.adapter = ChapterAdapter( noticeList)

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            newDialog()
        }

        dialogBuilder = NoticeDialog()
        getNotices()


    }

    fun newDialog() {


        dialogBuilder.show(this.supportFragmentManager, "NoticeDialogFragment")
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    override fun onDialogPositiveClick(dialog: NoticeDialog) {
        val title = dialogBuilder.et_title.text.toString()
        val content = dialogBuilder.et_content.text.toString()
        val newNotice = Notice(title,content)

        addNotice(newNotice)

    }

    fun addNotice(notice:Notice){
        // Tag used to cancel the request
        val tag_string_req = "req_register"
        //creating volley string request
        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_CREATE_NOTICE,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "create Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if(!error){
                        //---- Success
                        notice.id = obj.getString("id")
                        noticeList.add(notice)
                        Log.d("myerrorTags",noticeList.toString())
                        rvChapterList.adapter?.notifyDataSetChanged()
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        val errorMsg = obj.getString("error_msg")
                        Toast.makeText(
                            applicationContext,
                            errorMsg, Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, "VE"+volleyError.message, Toast.LENGTH_LONG).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val user_id =  db?.getUserDetails()?.get("uid")
                val params = HashMap<String, String>()
                params.put("user_id",user_id!!)
                params.put("title", notice.title)
                params.put("content", notice.content)
                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User touched the dialog's negative button
        Toast.makeText(this, "negative", Toast.LENGTH_SHORT)
    }

    fun getNotices(){
        // Tag used to cancel the request
        val tag_string_req = "req_notices"
        val user_id =  db?.getUserDetails()?.get("uid")
        val stringRequest = object : StringRequest(
            Method.GET, AppConfig.URL_GET_ALL_NOTICES + user_id,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "NOTICE Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")

                    if(error==false){
                        Log.d("myerrorTags", obj.getJSONArray("notices").toString());
                        //---- Success
                        val notices = obj.getJSONArray("notices")
                        Log.d("myerrorTags", notices.toString())
                        for(i in 0 until notices.length()){
                            val tmpObj = notices.getJSONObject(i)
                            val notice = Notice(tmpObj.getString("title"),tmpObj.getString("content"))
                            notice.id = tmpObj.getString("id")
                            noticeList.add(notice)
                            Log.d("myerrorTags", i.toString());
                        }
                        rvChapterList.adapter?.notifyDataSetChanged()


                    } else {
                        Log.d("myerrorTags", "else");
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, "VE"+volleyError.message, Toast.LENGTH_LONG).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
