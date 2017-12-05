package com.example.android.testapplication

import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), BooksAdapter.BooksAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Array<Array<String>>> {

    private var mRecyclerView: RecyclerView? = null
    private var mBooksAdapter: BooksAdapter? = null

    private var mErrorMessageDisplay: TextView? = null

    private var mLoadingIndicator: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_adapter)

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = findViewById<View>(R.id.recyclerview_books) as RecyclerView

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById<View>(R.id.tv_error_message_display) as TextView

        val recyclerViewOrientation = LinearLayoutManager.VERTICAL

        /*
         *  This value should be true if you want to reverse your layout. Generally, this is only
         *  true with horizontal lists that need to support a right-to-left layout.
         */
        val shouldReverseLayout = false
        val layoutManager = LinearLayoutManager(this, recyclerViewOrientation, shouldReverseLayout)
        mRecyclerView!!.layoutManager = layoutManager

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView!!.setHasFixedSize(true)

        /*
         * The BooksAdapter is responsible for linking our book data with the Views that
         * will end up displaying our book data.
         */
        mBooksAdapter = BooksAdapter(this)

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView!!.adapter = mBooksAdapter

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = findViewById<View>(R.id.pb_loading_indicator) as ProgressBar

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        supportLoaderManager.initLoader(BOOKS_LOADER_ID, Bundle.EMPTY, this@MainActivity)

        Log.d("MainActivity.class", "onCreate: registering preference changed listener")
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Array<Array<String>>> {

        return object : AsyncTaskLoader<Array<Array<String>>>(this) {

            /* This String array will hold and help cache our book data */
            internal var mBookData: Array<Array<String>>? = null

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            override fun onStartLoading() {
                if (mBookData != null) {
                    deliverResult(mBookData)
                } else {
                    mLoadingIndicator!!.visibility = View.VISIBLE
                    forceLoad()
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenBookMap in the background.
             *
             * @return Book data from OpenBookMap as an array of Strings.
             * null if an error occurs
             */
            override fun loadInBackground(): Array<Array<String>>? {
                val bookRequestUrl = NetworkUtils.buildUrl()
                try {
                    val jsonBookResponse = NetworkUtils
                            .getResponseFromHttpUrl(bookRequestUrl)

                    return OpenBooksJsonUtils
                            .getSimpleBookStringsFromJson(jsonBookResponse)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
            
            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            override fun deliverResult(data: Array<Array<String>>?) {
                mBookData = data
                super.deliverResult(data)
            }
        }
    }

    override fun onLoadFinished(loader: android.support.v4.content.Loader<Array<Array<String>>>, data: Array<Array<String>>?) {
        mLoadingIndicator!!.visibility = View.INVISIBLE
        if (data != null) {
            mBooksAdapter!!.setBooksData(data)
        }
        if (null == data) {
            showErrorMessage()
        } else {
            showBookDataView()
        }
    }

    override fun onLoaderReset(loader: android.support.v4.content.Loader<Array<Array<String>>>) {

    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private fun invalidateData() {
        mBooksAdapter!!.setBooksData(null)
    }

    override fun onClick(bookClicked: String) {
        Toast.makeText(this, "TODO: Open the book when we click on it", Toast.LENGTH_SHORT).show()
    }

    /**
     * This method will make the View for the book data visible and
     * hide the error message.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private fun showBookDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay!!.visibility = View.INVISIBLE
        /* Then, make sure the book data is visible */
        mRecyclerView!!.visibility = View.VISIBLE
    }

    /**
     * This method will make the error message visible and hide the book View.
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private fun showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView!!.visibility = View.INVISIBLE
        /* Then, show the error */
        mErrorMessageDisplay!!.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        val inflater = menuInflater
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.books, menu)
        /* Return true so that the menu is displayed in the Toolbar */
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_refresh) {
            invalidateData()
            supportLoaderManager.restartLoader(BOOKS_LOADER_ID, Bundle.EMPTY, this)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val BOOKS_LOADER_ID = 0
    }
}
