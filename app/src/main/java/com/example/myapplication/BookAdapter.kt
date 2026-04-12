package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    var onBookClick: ((Book) -> Unit)? = null

    private var books = listOf(
        Book("1","Мастер и Маргарита", "Михаил Булгаков", "Роман", "Читаю", 63, R.drawable.cover_master),
        Book("2","1984", "Джордж Оруэлл", "Антиутопия", "В планах", 0, R.drawable.cover_1984),
        Book("3","Преступление и наказание", "Фёдор Достоевский", "Роман", "Прочитано", 100, R.drawable.cover_master)
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
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookClick?.invoke(books[position])
                }
            }
        }
        fun bind(book: Book) {
            itemView.findViewById<ImageView>(R.id.coverImageView).setImageResource(book.coverRes)
            itemView.findViewById<TextView>(R.id.titleTextView).text = book.title
            itemView.findViewById<TextView>(R.id.authorTextView).text = book.author
            itemView.findViewById<TextView>(R.id.genreTextView).text = book.genre
            itemView.findViewById<TextView>(R.id.statusTextView).text = book.status

            // Исправление: используем правильный id прогресс-бара
            val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_container)
            progressBar.max = 100
            progressBar.progress = book.progress

            // Добавляем отображение процента
            val percentTextView = itemView.findViewById<TextView>(R.id.progressPercentTextView)
            percentTextView.text = "${book.progress}%"
        }
    }

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}