package android.example.com.studentlife_01.activity


import android.example.com.studentlife_01.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.VolleySingleton
import android.example.com.studentlife_01.helper.SQLiteHandler
import android.example.com.studentlife_01.helper.SessionManager
import android.example.com.studentlife_01.model.User
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var btnLinkToLogin: Button
    private lateinit var inputFullName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText

    private var session: SessionManager? = null
    private var db: SQLiteHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btnRegister) as Button
        btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen) as Button
        inputFullName = findViewById(R.id.name) as EditText
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
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()


        }


        btnRegister.setOnClickListener{
            val name = inputFullName.text.toString().trim { it <= ' ' }
            val email = inputEmail.text.toString().trim { it <= ' ' }
            val password = inputPassword.text.toString().trim { it <= ' ' }

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                registerUser(name, email, password)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please enter your details!", Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        btnLinkToLogin.setOnClickListener {
            val i = Intent(
                applicationContext,
                LoginActivity::class.java
            )
            startActivity(i)
            finish()
        }//--onclick




    }//--onCreate

    private fun registerUser(name:String, email:String ,password:String ) {
        // Tag used to cancel the request
        val tag_string_req = "req_register"
        //creating volley string request
        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_REGISTER,
            Response.Listener<String> { response ->
                Log.d("SL", "Register Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if(!error){
                        //----User Registration Success
                        // Now store the user in sqlite
                        val uid = obj.getString("uid")

                        val user = obj.getJSONObject("user")
                        val name = user.getString("name")
                        val email = user.getString("email")
                        val created_at = user
                            .getString("created_at")

                        val muser = User(name,email,uid,created_at)

                        // Inserting row in users table
                        db?.addUser(muser)


                        Toast.makeText(getApplicationContext(),
                            "User Registered", Toast.LENGTH_LONG)
                            .show()

                        // Launch login activity
                        val intent = Intent(
                            this@RegisterActivity,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                        finish()
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
                val params = HashMap<String, String>()
                params.put("name", name)
                params.put("email", email)
                params.put("password", password)
                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }//--registerUser
}
