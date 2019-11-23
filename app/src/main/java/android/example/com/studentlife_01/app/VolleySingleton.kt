package android.example.com.studentlife_01.app


import android.app.Application
import android.example.com.studentlife_01.BuildConfig
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import net.gotev.uploadservice.UploadService

class VolleySingleton: Application()  {
    override fun onCreate() {
        super.onCreate()
        instance = this

        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID
        // Or, you can define it manually.
        UploadService.NAMESPACE = "android.example.com.studentlife_01"
    }

    val requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                return Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }

    companion object {
        private val TAG = VolleySingleton::class.java.simpleName
        @get:Synchronized var instance: VolleySingleton? = null
            private set
    }
}