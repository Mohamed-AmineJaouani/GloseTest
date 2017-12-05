package com.example.android.testapplication

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

object OpenBooksJsonUtils {
    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the book.
     *
     * @param bookJsonStr JSON response from server
     *
     * @return Array of Strings describing book data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    @Throws(JSONException::class)
    fun getSimpleBookStringsFromJson(bookJsonStr: String?): Array<Array<String>> {

        /* Book are in the modules array */
        val MODULES = "modules"

        /* Book information. */
        val BOOKS_LIST = "books"

        /* All book information */
        val BOOK_TITLE = "title"
        //val BOOK_SHORT_TITLE = "shortTitle"

        /* Book authors : slug and name */
        val BOOK_AUTHORS = "authors"
        val BOOK_AUTHOR_NAME = "name"

        //val BOOK_IMAGE = "image"
        val BOOK_LARGE_IMAGE = "largeImage"
        //val BOOK_MEDIUM_IMAGE = "mediumImage"


        /* String array to hold each book String */
        val parsedBookData: Array<Array<String>>?

        val bookJson = JSONObject(bookJsonStr)

        val modulesArray = bookJson.getJSONArray(MODULES)
        Log.d("yooooo", modulesArray.toString())

        val moduleBook = modulesArray.getJSONObject(1)
        Log.d("yooooo", moduleBook.toString())


        /* Get the JSON object representing the book */
        val bookArray = moduleBook.getJSONArray(BOOKS_LIST)
        Log.d("yooooo", bookArray.toString())

        parsedBookData = Array(bookArray.length()) { Array<String>(2, {"";""}) }
        for (i in 0 until bookArray.length()) {

            /* These are the values that will be collected */
            val bookAuthor: String
            val bookTitle: String
            val imageViewString: String
            val book = bookArray.getJSONObject(i)

            /*
             * Description is in a child array called "book", which is 1 element long.
             * That element also contains a book code.
             */
            val bookAuthorArray = book.getJSONArray(BOOK_AUTHORS)
            bookAuthor = if (bookAuthorArray.length() != 0)
                bookAuthorArray.getJSONObject(0).getString(BOOK_AUTHOR_NAME)
            else
                ""

            bookTitle = book.getString(BOOK_TITLE)
            imageViewString = book.getString(BOOK_LARGE_IMAGE)

            parsedBookData[i][0] = bookTitle + " - " + bookAuthor
            parsedBookData[i][1] = imageViewString
        }
        return parsedBookData
    }
}
