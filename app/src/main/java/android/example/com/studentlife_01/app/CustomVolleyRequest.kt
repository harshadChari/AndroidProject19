package android.example.com.studentlife_01.app

import android.app.Application
import com.android.volley.toolbox.ImageLoader
import android.content.Context
import android.example.com.studentlife_01.BuildConfig
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.toolbox.HurlStack
import net.gotev.uploadservice.UploadService


class CustomVolleyRequest(): Application() {
    companion object {
         var customVolleyRequest: CustomVolleyRequest? = null
        private val TAG = CustomVolleyRequest::class.java.simpleName
    }

    constructor(context:Context):this(){
        this.context = context
        this.requestQueue = getRequestQueue()
        imageLoader = ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)

                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    private lateinit var context: Context
    private var requestQueue: RequestQueue? = null
    private lateinit var imageLoader: ImageLoader


    override fun onCreate() {
        super.onCreate()
        CustomVolleyRequest.customVolleyRequest = this

        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID
        // Or, you can define it manually.
        UploadService.NAMESPACE = "android.example.com.studentlife_01"
    }


    @Synchronized
    fun getInstance(context: Context): CustomVolleyRequest {
        if (customVolleyRequest == null) {
            customVolleyRequest = CustomVolleyRequest(context)
        }
        return customVolleyRequest!!
    }

    fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            val cache = DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024)
            val network = BasicNetwork(HurlStack())
            requestQueue = RequestQueue(cache, network)
            requestQueue!!.start()

        }
        return requestQueue!!
    }

    fun getImageLoader(): ImageLoader {
        return imageLoader
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = CustomVolleyRequest.TAG
        requestQueue?.add(request)
    }

}