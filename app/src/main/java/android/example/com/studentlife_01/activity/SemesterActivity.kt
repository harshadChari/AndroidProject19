package android.example.com.studentlife_01.activity

import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.VolleySingleton
import android.example.com.studentlife_01.helper.SQLiteHandler
import android.example.com.studentlife_01.model.Semester
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_notice.*
import org.json.JSONException
import org.json.JSONObject

class SemesterActivity : AppCompatActivity() {

    val semesterList: ArrayList<Semester> = ArrayList()

    private lateinit var btnadd: Button
    private lateinit var btnview: Button
    private lateinit var sem: EditText
    private lateinit var start_date: EditText
    private lateinit var end_date: EditText

    private var db: SQLiteHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semester)

        btnadd = findViewById(R.id.btn_add) as Button
        btnview = findViewById(R.id.btn_view)as Button
        sem = findViewById(R.id.sem) as EditText
        start_date = findViewById(R.id.start_date) as EditText
        end_date = findViewById(R.id.end_date) as EditText


        val semstr:String=sem.text.toString()
        val sdate:String=start_date.text.toString()
        val edate:String=end_date.text.toString()
        val sem=Semester(semstr,sdate,edate)

        //set onClickListener
        btnadd.setOnClickListener { addSemester(sem)}

    }

    //adding a new record to database
    private fun addSemester(semester:Semester) {
        //getting the record values
        val sem = sem?.text.toString()
        val sdate = start_date?.text.toString()
        val edate = end_date?.text.toString()

        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_CREATE_SEMESTER,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "create Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if (!error) {
                        //---- Success
                        semester.id = obj.getString("id")
                        semesterList.add(semester)
                        Log.d("myerrorTags", semesterList.toString())
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
                    Toast.makeText(applicationContext, "VE" + volleyError.message, Toast.LENGTH_LONG).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val user_id = db?.getUserDetails()?.get("uid")
                val params = HashMap<String, String>()
                params.put("user_id", user_id!!)
                params.put("sem", semester.sem)
                params.put("start_date", semester.start_date)
                params.put("end_date", semester.end_date)

                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }


    fun getSemester(){

        val user_id =  db?.getUserDetails()?.get("uid")
        val stringRequest = object : StringRequest(
            Method.GET, AppConfig.URL_GET_ALL_SEMESTERS + user_id,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "NOTICE Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")

                    if(error==false){
                        Log.d("myerrorTags", obj.getJSONArray("semesters").toString());
                        //---- Success
                        val notices = obj.getJSONArray("semesters")
                        Log.d("myerrorTags", notices.toString())
                        for(i in 0 until notices.length()){
                            val tmpObj = notices.getJSONObject(i)
                            val semester = Semester(tmpObj.getString("sem"),tmpObj.getString("start_date"),tmpObj.getString("end_date"))
                            semester.id = tmpObj.getString("id")
                            semesterList.add(semester)
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






