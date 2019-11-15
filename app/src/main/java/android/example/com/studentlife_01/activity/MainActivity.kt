package android.example.com.studentlife_01.activity

import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.helper.SessionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import android.example.com.studentlife_01.helper.SQLiteHandler


class MainActivity : AppCompatActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnNotices: Button


    private var session: SessionManager? = null
    private var db: SQLiteHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//

        txtName =findViewById(R.id.name)
        txtEmail = findViewById(R.id.email)
        btnLogout =  findViewById(R.id.btnLogout)
        btnNotices =  findViewById(R.id.btnNotices)


        // Session manager
        session = SessionManager(applicationContext)
        // SQLite database handler
        db = SQLiteHandler(applicationContext,null)

        // Check if user is already logged in or not
        if (session?.isLoggedIn()==false) {

            logoutUser()
        }

        // Fetching user details from sqlite
        val user = db?.getUserDetails()

        val name = user?.get("name")
        val email = user?.get("email")

        // Displaying the user details on the screen
        txtName.text = name
        txtEmail.text = email

         //Logout button click event
        btnLogout.setOnClickListener{
            logoutUser()
        }

        //Logout button click event
        btnNotices.setOnClickListener{
            val intent = Intent(this, NoticeActivity::class.java)
            startActivity(intent)

        }
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private fun logoutUser() {
        session?.setLogin(false)

        db?.deleteUsers()

        // Launching the login activity
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
