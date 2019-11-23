package android.example.com.studentlife_01.activity

import android.content.Intent
import android.example.com.studentlife_01.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.CustomVolleyRequest
import android.example.com.studentlife_01.app.VolleySingleton
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import android.example.com.studentlife_01.helper.SessionManager
import android.example.com.studentlife_01.helper.SQLiteHandler
import android.example.com.studentlife_01.model.User


class LoginActivity : AppCompatActivity() {
    //private val TAG = RegisterActivity::class.java!!.getSimpleName()
    private lateinit var btnLogin: Button
    private  lateinit var btnLinkToRegister: Button
    private  lateinit var inputEmail: EditText
    private  lateinit var inputPassword: EditText

    private var session: SessionManager? = null
    private var db: SQLiteHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.example.com.studentlife_01.R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin) as Button
        btnLinkToRegister = findViewById(R.id.btnLinkToRegisterScreen) as Button
        inputEmail = findViewById(R.id.email) as EditText
        inputPassword = findViewById(R.id.password) as EditText

        // Session manager
        session = SessionManager(applicationContext)

        // SQLite database handler
        db = SQLiteHandler(applicationContext,null)

        // Check if user is already logged in or not
        if (session?.isLoggedIn()==true) {
            Toast.makeText(getApplicationContext(),
                "Alrready Logged In", Toast.LENGTH_LONG)
                .show();
            // User is already logged in. Take him to main activity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()


        }


        // Login button Click Event
        btnLogin.setOnClickListener{
            val email = inputEmail.getText().toString().trim { it <= ' ' }
            val password = inputPassword.getText().toString().trim { it <= ' ' }

            // Check for empty data in the form
            if (!email.isEmpty() && !password.isEmpty()) {
                // login user
                checkLogin(email, password);
            } else {
                // Prompt user to enter credentials
                Toast.makeText(getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
            }

        }
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener{
            val i = Intent(
                applicationContext,
                RegisterActivity::class.java
            )
            startActivity(i)
            finish()
        }
    }

    private fun checkLogin(email:String, password:String){
// Tag used to cancel the request
        val tag_string_req = "req_login"
        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_LOGIN,
            Response.Listener<String> { response ->
                Log.d("SL", "Login Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if(!error){
                        //----Login Success

                        session?.setLogin(true)

                        // Now store the user in SQLite

                        val uid = obj.getString("uid")

                        val user = obj.getJSONObject("user")
                        val name = user.getString("name")
                        val email = user.getString("email")
                        val created_at = user
                            .getString("created_at")

                        val muser = User(name,email,uid,created_at)

                        // Inserting row in users table
                        db?.addUser(muser)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
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
                params.put("email", email)
                params.put("password", password)
                return params
            }
        }

        //adding request to queue
        //VolleySingleton.instance?.addToRequestQueue(stringRequest)
        val cvr = CustomVolleyRequest(this)
        cvr.addToRequestQueue(stringRequest)
        //CustomVolleyRequest.customVolleyRequest?.getInstance(this)?.addToRequestQueue(stringRequest)

    }//--checkLogin






}