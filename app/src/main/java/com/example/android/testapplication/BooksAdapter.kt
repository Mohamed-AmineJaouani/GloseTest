package com.example.android.testapplication

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class BooksAdapter

internal constructor(
    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private val mClickHandler: BooksAdapterOnClickHandler) : RecyclerView.Adapter<BooksAdapter.BooksAdapterViewHolder>() {

    /** Represents the book's data retrieved from the API, it is a 2D array within the title and the url
     * of each book
     */
    private var mBooksData: Array<Array<String>>? = null

    /**
     * The interface that receives onClick messages.
     */
    interface BooksAdapterOnClickHandler {
        fun onClick(bookClicked: String)
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     * can use this viewType integer to provide a different layout. See
     * [android.support.v7.widget.RecyclerView.Adapter.getItemViewType]
     * for more details.
     * @return A new BooksAdapterViewHolder that holds the View for each list item
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BooksAdapterViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.books_list_item
        val inflater = LayoutInflater.from(context)

        val shouldAttachToParentImmediately = false
        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        view.isFocusable = true

        return BooksAdapterViewHolder(view)
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data.
     * In this method, we update the contents of the ViewHolder to display the book
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     * contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: BooksAdapterViewHolder, position: Int) {
        holder.descriptionView.text = mBooksData!![position][0]
        ImageDownloader(holder).execute(mBooksData!![position][1])
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our books display
     */
    override fun getItemCount(): Int {
        return if (null == mBooksData) 0 else mBooksData!!.size
    }

    /**
     * This method is used to set the books data on a BooksAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new BooksAdapter to display it.
     *
     * @param booksData The new book data to be displayed.
     */
    internal fun setBooksData(booksData: Array<Array<String>>?) {
        mBooksData = booksData
        notifyDataSetChanged()
    }


    inner class BooksAdapterViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageBook: ImageView = itemView.findViewById(R.id.iv_book_image)
        internal val descriptionView: TextView = itemView.findViewById(R.id.tv_book_description)

        /**
         * Update the current bitmap by the downloaded bitmap in the ImageDownloader class
         *
         * @param image the image that was downloaded
         */
        internal fun updateBitmap(image: Bitmap) {
            this.imageBook.setImageBitmap(image)
        }

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        override fun onClick(v: View) {
            val adapterPosition = adapterPosition
            val bookDescription = mBooksData!![adapterPosition][0]
            mClickHandler.onClick(bookDescription)
        }
    }
}
