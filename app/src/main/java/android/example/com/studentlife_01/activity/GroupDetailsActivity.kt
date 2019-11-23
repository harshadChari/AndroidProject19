package android.example.com.studentlife_01.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.com.studentlife_01.R
import android.example.com.studentlife_01.app.AppConfig
import android.example.com.studentlife_01.app.CustomVolleyRequest
import android.example.com.studentlife_01.helper.ChapterAdapter
import android.example.com.studentlife_01.helper.NoticeDialog
import android.example.com.studentlife_01.helper.SQLiteHandler
import android.example.com.studentlife_01.helper.UserDialog
import android.example.com.studentlife_01.model.Notice
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_notice.*
import kotlinx.android.synthetic.main.dialog_add_group.*
import net.gotev.uploadservice.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupDetailsActivity : AppCompatActivity(), NoticeDialog.NoticeDialogListener,
    UserDialog.UserDialogListener, UploadStatusDelegate {

    private val TAG: String = "AndroidUploadService"

    var imagePost:Boolean = false

    private lateinit var uploadReceiver: UploadServiceSingleBroadcastReceiver


    val noticeList: ArrayList<Notice> = ArrayList()
    lateinit var dialogBuilder: NoticeDialog
    lateinit var user_dialogBuilder: UserDialog
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var db: SQLiteHandler? = null

    lateinit var tv_grp_name: TextView

    lateinit var group_id: String

    lateinit var imageView_ref: ImageView


    //Image request code
    private val PICK_IMAGE_REQUEST = 1

    //storage permission code
    private val STORAGE_PERMISSION_CODE = 123

    //Bitmap to get image from gallery
    private var bitmap: Bitmap? = null

    //Uri to store the image uri
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uploadReceiver = UploadServiceSingleBroadcastReceiver(this)

        requestStoragePermission()

        tv_grp_name = findViewById(R.id.tv_grp_name)

        val bundle = getIntent().extras
        tv_grp_name.setText(bundle?.getCharSequence("name").toString())
        group_id = bundle?.getCharSequence("id").toString()


        // SQLite database handler
        db = SQLiteHandler(applicationContext, null)
//
        layoutManager = LinearLayoutManager(this)
        rvNoticeList.layoutManager = layoutManager
        rvNoticeList.adapter = ChapterAdapter(this, noticeList)


        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            newDialog()
        }

        val fabUser: View = findViewById(R.id.fabUser)
        fabUser.setOnClickListener { _ ->
            imagePost = false
            user_dialogBuilder.show(this.supportFragmentManager, "UserDialogFragment")
        }
        dialogBuilder = NoticeDialog()
        user_dialogBuilder = UserDialog()
        getNotices()


    }//onCreate

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
        val newNotice = Notice(title, content)
        if(imagePost)
            uploadMultipart(newNotice)
        else
         addNotice(newNotice)


    }
    override fun onUserDialogPositiveClick(dialog: UserDialog) {
        val email = user_dialogBuilder.et_email.text.toString()

        addUser(email)
    }

    override fun onUserDialogNegativeClick(dialog: DialogFragment) {

    }

    fun addUser(email: String) {
        // Tag used to cancel the request
        //val tag_string_req = "req_register"
        //creating volley string request
        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_ADD_USER,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "create Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if (!error) {
                        //---- Success
                        Toast.makeText(
                            applicationContext,
                            "User Added", Toast.LENGTH_LONG
                        ).show()

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
                    Toast.makeText(
                        applicationContext,
                        "VE" + volleyError.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("email", email!!)
                params.put("group_id", group_id)

                return params
            }
        }
        //adding request to queue
        // VolleySingleton.instance?.addToRequestQueue(stringRequest)
        // CustomVolleyRequest.customVolleyRequest?.getInstance(this)?.addToRequestQueue(stringRequest)

        val cvr = CustomVolleyRequest(this)
        cvr.addToRequestQueue(stringRequest)

    }

    fun addNotice(notice: Notice) {
        // Tag used to cancel the request
        //val tag_string_req = "req_register"
        //creating volley string request
        val stringRequest = object : StringRequest(
            Method.POST, AppConfig.URL_POST_NOTICE,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "create Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")
                    if (!error) {
                        //---- Success
                        notice.id = obj.getString("id")
                        noticeList.add(notice)
                        Log.d("myerrorTags", noticeList.toString())
                        rvNoticeList.adapter?.notifyDataSetChanged()
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
                    Toast.makeText(
                        applicationContext,
                        "VE" + volleyError.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val user_id = db?.getUserDetails()?.get("uid")
                val params = HashMap<String, String>()
                params.put("user_id", user_id!!)
                params.put("title", notice.title)
                params.put("content", notice.content)
                params.put("group_id", group_id)

                return params
            }
        }
        //adding request to queue
        // VolleySingleton.instance?.addToRequestQueue(stringRequest)
        // CustomVolleyRequest.customVolleyRequest?.getInstance(this)?.addToRequestQueue(stringRequest)

        val cvr = CustomVolleyRequest(this)
        cvr.addToRequestQueue(stringRequest)

    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User touched the dialog's negative button
        Toast.makeText(this, "negative", Toast.LENGTH_SHORT)
    }

    override fun getPermissions(dialog: NoticeDialog) {
        val imageView = dialogBuilder.imageView
        showFileChooser(imageView)
    }

    fun getNotices() {
        // Tag used to cancel the request
        //val tag_string_req = "req_notices"

        val stringRequest = object : StringRequest(
            Method.GET, AppConfig.URL_GET_NOTICES_BY_GROUP + group_id,
            Response.Listener<String> { response ->
                Log.d("myerrorTags", "NOTICE Response: " + response.toString());
                try {
                    val obj = JSONObject(response)
                    val error = obj.getBoolean("error")

                    if (error == false) {
                        Log.d("myerrorTags", obj.getJSONArray("notices").toString());
                        //---- Success
                        val notices = obj.getJSONArray("notices")
                        Log.d("myerrorTags", notices.toString())
                        for (i in 0 until notices.length()) {
                            val tmpObj = notices.getJSONObject(i)
                            val notice =
                                Notice(tmpObj.getString("title"), tmpObj.getString("content"))
                            notice.id = tmpObj.getString("id")
                            notice.document_path = tmpObj.getString("document_path")
                            noticeList.add(notice)
                            Log.d("myerrorTags", i.toString());
                        }
                        rvNoticeList.adapter?.notifyDataSetChanged()


                    } else {
                        Log.d("myerrorTags", "else");
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(
                        applicationContext,
                        "VE" + volleyError.message,
                        Toast.LENGTH_LONG
                    ).show()
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
        //CustomVolleyRequest.customVolleyRequest?.getInstance(this)?.addToRequestQueue(stringRequest)
        val cvr = CustomVolleyRequest(this)
        cvr.addToRequestQueue(stringRequest)
    }


    //method to get the file path from uri
    fun getPath(uri: Uri): String {
        var cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(document_id),
            null
        )
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }


    //method to show file chooser
    private fun showFileChooser(imageView: ImageView) {
        imageView_ref = imageView
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

                imageView_ref?.setImageBitmap(bitmap)
                imagePost = true;


            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /*
    * This is the method responsible for image upload
    * We need the full image path and the name for the image in this method
    * */
    fun uploadMultipart(notice: Notice) {
        //getting name for the image
        val name = "image"

        //getting the actual path of the image
        val path = getPath(filePath!!)

        //Uploading code
        try {
            val uploadId = UUID.randomUUID().toString()
            val user_id = db?.getUserDetails()?.get("uid")
            uploadReceiver.setUploadID(uploadId)

            //Creating a multi part request
            MultipartUploadRequest(this, uploadId, AppConfig.UPLOAD_URL)
                .addFileToUpload(path, "image") //Adding file
                .addParameter("user_id", user_id!!) //Adding text parameter to the request
                .addParameter("title", notice.title) //Adding text parameter to the request
                .addParameter("content", notice.content) //Adding text parameter to the request
                .addParameter("group_id", group_id) //Adding text parameter to the request
                .addParameter("access", "1") //Adding text parameter to the request
                .setNotificationConfig(UploadNotificationConfig())
                .setMaxRetries(2)
                .startUpload() //Starting the upload
            Toast.makeText(this, AppConfig.UPLOAD_URL, Toast.LENGTH_SHORT).show()
        } catch (exc: Exception) {
            Toast.makeText(this, exc.message, Toast.LENGTH_SHORT).show()
        }

    }


    //Requesting permission
    private fun requestStoragePermission() {
        Log.d("myerrortags", "requestiing")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
            return

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }


    //This method will be called when the user will tap on allow or deny
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(
                    this,
                    "Permission granted now you can read the storage",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onCancelled(context: Context?, uploadInfo: UploadInfo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProgress(context: Context?, uploadInfo: UploadInfo?) {
        // //To change body of created functions use File | Settings | File Templates.
    }



    override fun onError(
        context: Context?,
        uploadInfo: UploadInfo?,
        serverResponse: ServerResponse?,
        exception: java.lang.Exception?
    ) {

    }

    override fun onCompleted(
        context: Context?,
        uploadInfo: UploadInfo?,
        serverResponse: ServerResponse?
    ) {


        val obj = JSONObject(serverResponse?.getBodyAsString())
        Log.d("myerrortags", "response2:" + obj.getString("document_path"))
        val error = obj.getBoolean("error")

        if (!error) {

            //---- Success
            val id = obj.getString("id")
            val title = obj.getString("title")
            val content = obj.getString("content")
            val document_path = obj.getString("document_path")
            var notice = Notice(title, content)
            notice.id = id
            notice.document_path = document_path
            noticeList.add(notice)

            Log.d("myerrorTags", noticeList.toString())
            rvNoticeList.adapter?.notifyDataSetChanged()
        } else {
            // Error occurred in registration. Get the error
            // message
            val errorMsg = obj.getString("error_msg")
            Toast.makeText(
                applicationContext,
                errorMsg, Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        uploadReceiver.register(this)
    }

    override fun onPause() {
        super.onPause()
        uploadReceiver.unregister(this)
    }

}
