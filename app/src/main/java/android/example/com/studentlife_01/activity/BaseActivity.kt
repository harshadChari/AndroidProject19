package android.example.com.studentlife_01.activity

import android.Manifest
import android.example.com.studentlife_01.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.net.Uri
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.provider.MediaStore
import android.widget.Toast
import net.gotev.uploadservice.UploadNotificationConfig
import net.gotev.uploadservice.MultipartUploadRequest
import android.example.com.studentlife_01.app.AppConfig
import java.util.*
import android.content.Intent
import android.app.Activity
import java.io.IOException
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat




class BaseActivity : AppCompatActivity() {

    //Declaring views
    private var buttonChoose: Button? = null
    private var buttonUpload: Button? = null
    private var imageView: ImageView? = null
    private var editText: EditText? = null

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
        setContentView(R.layout.activity_base)


        //Requesting storage permission
        requestStoragePermission()

        //Initializing views
        buttonChoose = findViewById(R.id.buttonChoose) as Button
        buttonUpload =  findViewById(R.id.buttonUpload)  as Button
        imageView =  findViewById(R.id.imageView)  as ImageView
        editText =  findViewById(R.id.editTextName)  as EditText

        //Setting clicklistener
        buttonChoose?.setOnClickListener{
            showFileChooser();
        }
        buttonUpload?.setOnClickListener{
            uploadMultipart();
        }

    }
    /*
    * This is the method responsible for image upload
    * We need the full image path and the name for the image in this method
    * */
    fun uploadMultipart() {
        //getting name for the image
        val name = editText?.getText().toString().trim { it <= ' ' }

        //getting the actual path of the image
        val path = getPath(filePath!!)

        //Uploading code
        try {
            val uploadId = UUID.randomUUID().toString()

            //Creating a multi part request
            MultipartUploadRequest(this, uploadId, AppConfig.UPLOAD_URL)
                .addFileToUpload(path, "image") //Adding file
                .addParameter("name", name) //Adding text parameter to the request
                .setNotificationConfig(UploadNotificationConfig())
                .setMaxRetries(2)
                .startUpload() //Starting the upload
            Toast.makeText(this, AppConfig.UPLOAD_URL, Toast.LENGTH_SHORT).show()
        } catch (exc: Exception) {
            Toast.makeText(this, exc.message, Toast.LENGTH_SHORT).show()
        }

    }

    //method to show file chooser
    private fun showFileChooser() {
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
                imageView?.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
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

    //Requesting permission
    private fun requestStoragePermission() {
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


}
