package com.example.android.testapplication

import android.net.Uri
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object NetworkUtils {
    private val TAG = "MainActivity.class"

    val BOOKS_URL = "https://api.glose.com/v1/booklists/free-books?_version=20150601"

    /**
     * Builds the URL used to talk to the books server.
     *
     * @return The URL to use to query the books server.
     */
    fun buildUrl(): URL? {
        val builtUri = Uri.parse(BOOKS_URL).buildUpon()
                .build()

        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        Log.v(TAG, "Built URI " + url!!)

        return url
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL?): String? {
        val urlConnection = url!!.openConnection() as HttpURLConnection
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}
