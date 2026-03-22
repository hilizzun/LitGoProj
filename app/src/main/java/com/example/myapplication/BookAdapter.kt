package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    data class Book(
        val title: String,
        val author: String,
        val genre: String,
        val status: String,
        val progress: Int,
        val coverRes: Int
    )

    private val books = listOf(
        Book("Мастер и Маргарита", "Михаил Булгаков", "Роман", "Читаю", 63, R.drawable.cover_master),
        Book("1984", "Джордж Оруэлл", "Антиутопия", "В планах", 0, R.drawable.cover_1984)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(book: Book) {
            itemView.findViewById<ImageView>(R.id.coverImageView).setImageResource(book.coverRes)
            itemView.findViewById<TextView>(R.id.titleTextView).text = book.title
            itemView.findViewById<TextView>(R.id.authorTextView).text = book.author
            itemView.findViewById<TextView>(R.id.genreTextView).text = book.genre
            itemView.findViewById<TextView>(R.id.statusTextView).text = book.status
            itemView.findViewById<ProgressBar>(R.id.progressBar).progress = book.progress
        }
    }
}