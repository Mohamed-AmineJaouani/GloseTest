package com.example.android.testapplication


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.InputStream
import java.net.URL

/**
 * AsyncTask wich download the image using the url passed in param
 */
class ImageDownloader(private val parentActivity: BooksAdapter.BooksAdapterViewHolder) : AsyncTask<String, String, Bitmap>() {
    /**
     * Background function which download the image
     *
     * @param args contains the image url to download
     * @return the Bitmap object of the downloaded image
     */
    override fun doInBackground(vararg args: String): Bitmap? {
        try {
            return BitmapFactory.decodeStream(URL(args[0]).content as InputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Called when the background task is done, and call the updateBitmap function
     * @param image the downloaded bitmap image
     */
    override fun onPostExecute(image: Bitmap?) {
        if (image != null) {
            parentActivity.updateBitmap(image)
        }
    }
}
