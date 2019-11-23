package android.example.com.studentlife_01.activity

import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.CustomVolleyRequest
import android.example.com.studentlife_01.app.VolleySingleton
import android.example.com.studentlife_01.helper.GroupAdapter
import android.example.com.studentlife_01.helper.GroupDialog
import android.example.com.studentlife_01.helper.NoticeDialog
import android.example.com.studentlife_01.helper.SQLiteHandler
import android.example.com.studentlife_01.model.Group
import android.example.com.studentlife_01.model.Notice
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
import kotlinx.android.synthetic.main.activity_notice_groups.*
import org.json.JSONException
import org.json.JSONObject

class NoticeGroupsActivity : AppCompatActivity(), GroupDialog.GroupDialogListener {
    val groupList: ArrayList<Group> = ArrayList()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var db: SQLiteHandler? = null
    lateinit var dialogBuilder: GroupDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_groups)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // SQLite database handler
        db = SQLiteHandler(applicationContext,null)

        layoutManager = LinearLayoutManager(this)
        rv_groupList.layoutManager = layoutManager
        rv_groupList.adapter = GroupAdapter( groupList,this)

        dialogBuilder = GroupDialog()

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            dialogBuilder.show(this.supportFragmentManager, "NoticeDialogFragment")
        }

        getGroups()
    }




    fun getGroups(){
        // Tag used to cancel the request
        val tag_string_req = "req_groups"
        val user_id =  db?.getUserDetails()?.get("uid")
        val stringRequest = object : StringRequest(
            Method.GET, AppConfig.URL_GET_GROUPS_BY_USER + user_id,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "NOTICE Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")

                    if(error==false){
                        Log.d("myerrorTags", "1: "+obj.getJSONArray("groups").toString());
                        //---- Success
                        val groups = obj.getJSONArray("groups")
                        Log.d("myerrorTags", "2:"+groups.toString())


                        for(i in 0 until groups.length()){
                            Log.d("myerrorTags", "3:"+groups.length())
                            val tmpObj = groups.getJSONObject(i)
                            Log.d("myerrorTags", "4:"+tmpObj.toString())
                            val group = Group(tmpObj.getString("name"))
                            group.user_id = tmpObj.getString("user_id")
                            group.id = tmpObj.getString("group_id")
                            group.admin_state = tmpObj.getString("admin_state")
                            group.created_at = tmpObj.getString("created_at")
                            groupList.add(group)

                        }
                        rv_groupList.adapter?.notifyDataSetChanged()


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
                    Log.d("myerrortags",volleyError.toString())
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                return params
            }
        }
        //adding request to queue
        //VolleySingleton.instance?.addToRequestQueue(stringRequest)
        val cvr = CustomVolleyRequest(this)
        cvr.addToRequestQueue(stringRequest)
        //CustomVolleyRequest.customVolleyRequest?.getInstance(this)?.addToRequestQueue(stringRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDialogPositiveClick(dialog: GroupDialog) {
        val name = dialogBuilder.et_name.text.toString()

        val newGroup = Group(name)

        addGroup(newGroup)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User touched the dialog's negative button
        Toast.makeText(this, "negative", Toast.LENGTH_SHORT)
    }



    fun addGroup(group:Group){
        // Tag used to cancel the request
        val tag_string_req = "req_register"
        //creating volley string request
        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_CREATE_GROUP,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "create Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if(!error){
                        //---- Success
                        group.id = obj.getString("id")
                        groupList.add(group)
                        Log.d("myerrorTags",groupList.toString())
                        rv_groupList.adapter?.notifyDataSetChanged()
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
                params.put("name", group.name)
                return params
            }
        }
        //adding request to queue
        //VolleySingleton.instance?.addToRequestQueue(stringRequest)
        val cvr = CustomVolleyRequest(this)
        cvr.addToRequestQueue(stringRequest)
        //CustomVolleyRequest.customVolleyRequest?.getInstance(this)?.addToRequestQueue(stringRequest)

    }

}
