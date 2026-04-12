package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_BOOKS (
                $BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $BOOK_TITLE TEXT NOT NULL,
                $BOOK_AUTHOR TEXT NOT NULL,
                $BOOK_GENRE TEXT NOT NULL,
                $BOOK_STATUS TEXT NOT NULL,
                $BOOK_PROGRESS INTEGER NOT NULL,
                $BOOK_COVER_RES INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_NOTES (
                $NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $NOTE_DATE TEXT NOT NULL,
                $NOTE_BOOK_TITLE TEXT NOT NULL,
                $NOTE_PAGES TEXT NOT NULL,
                $NOTE_COVER_RES INTEGER NOT NULL
            )
            """.trimIndent()
        )

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }

    fun createBook(book: Book): Long {
        val values = ContentValues().apply {
            put(BOOK_TITLE, book.title)
            put(BOOK_AUTHOR, book.author)
            put(BOOK_GENRE, book.genre)
            put(BOOK_STATUS, book.status)
            put(BOOK_PROGRESS, book.progress)
            put(BOOK_COVER_RES, book.coverRes)
        }
        return writableDatabase.insert(TABLE_BOOKS, null, values)
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = readableDatabase.query(
            TABLE_BOOKS,
            null,
            null,
            null,
            null,
            null,
            "$BOOK_ID DESC"
        )

        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(BOOK_ID)
            val titleIndex = it.getColumnIndexOrThrow(BOOK_TITLE)
            val authorIndex = it.getColumnIndexOrThrow(BOOK_AUTHOR)
            val genreIndex = it.getColumnIndexOrThrow(BOOK_GENRE)
            val statusIndex = it.getColumnIndexOrThrow(BOOK_STATUS)
            val progressIndex = it.getColumnIndexOrThrow(BOOK_PROGRESS)
            val coverIndex = it.getColumnIndexOrThrow(BOOK_COVER_RES)

            while (it.moveToNext()) {
                books.add(
                    Book(
                        id = it.getLong(idIndex).toString(),
                        title = it.getString(titleIndex),
                        author = it.getString(authorIndex),
                        genre = it.getString(genreIndex),
                        status = it.getString(statusIndex),
                        progress = it.getInt(progressIndex),
                        coverRes = it.getInt(coverIndex)
                    )
                )
            }
        }
        return books
    }

    fun getBookById(id: String): Book? {
        val bookId = id.toLongOrNull() ?: return null
        val cursor = readableDatabase.query(
            TABLE_BOOKS,
            null,
            "$BOOK_ID = ?",
            arrayOf(bookId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (!it.moveToFirst()) return null
            return Book(
                id = it.getLong(it.getColumnIndexOrThrow(BOOK_ID)).toString(),
                title = it.getString(it.getColumnIndexOrThrow(BOOK_TITLE)),
                author = it.getString(it.getColumnIndexOrThrow(BOOK_AUTHOR)),
                genre = it.getString(it.getColumnIndexOrThrow(BOOK_GENRE)),
                status = it.getString(it.getColumnIndexOrThrow(BOOK_STATUS)),
                progress = it.getInt(it.getColumnIndexOrThrow(BOOK_PROGRESS)),
                coverRes = it.getInt(it.getColumnIndexOrThrow(BOOK_COVER_RES))
            )
        }
    }

    fun findBookByTitle(title: String): Book? {
        val cursor = readableDatabase.query(
            TABLE_BOOKS,
            null,
            "$BOOK_TITLE = ? COLLATE NOCASE",
            arrayOf(title.trim()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            if (!it.moveToFirst()) return null
            return Book(
                id = it.getLong(it.getColumnIndexOrThrow(BOOK_ID)).toString(),
                title = it.getString(it.getColumnIndexOrThrow(BOOK_TITLE)),
                author = it.getString(it.getColumnIndexOrThrow(BOOK_AUTHOR)),
                genre = it.getString(it.getColumnIndexOrThrow(BOOK_GENRE)),
                status = it.getString(it.getColumnIndexOrThrow(BOOK_STATUS)),
                progress = it.getInt(it.getColumnIndexOrThrow(BOOK_PROGRESS)),
                coverRes = it.getInt(it.getColumnIndexOrThrow(BOOK_COVER_RES))
            )
        }
    }

    fun updateBook(book: Book): Int {
        val id = book.id.toLongOrNull() ?: return 0
        val values = ContentValues().apply {
            put(BOOK_TITLE, book.title)
            put(BOOK_AUTHOR, book.author)
            put(BOOK_GENRE, book.genre)
            put(BOOK_STATUS, book.status)
            put(BOOK_PROGRESS, book.progress)
            put(BOOK_COVER_RES, book.coverRes)
        }
        return writableDatabase.update(
            TABLE_BOOKS,
            values,
            "$BOOK_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteBook(id: String): Int {
        val bookId = id.toLongOrNull() ?: return 0
        return writableDatabase.delete(
            TABLE_BOOKS,
            "$BOOK_ID = ?",
            arrayOf(bookId.toString())
        )
    }

    fun createNote(note: Note): Long {
        val values = ContentValues().apply {
            put(NOTE_DATE, note.date)
            put(NOTE_BOOK_TITLE, note.bookTitle)
            put(NOTE_PAGES, note.pages)
            put(NOTE_COVER_RES, note.coverRes)
        }
        return writableDatabase.insert(TABLE_NOTES, null, values)
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val cursor = readableDatabase.query(
            TABLE_NOTES,
            null,
            null,
            null,
            null,
            null,
            "$NOTE_ID DESC"
        )

        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(NOTE_ID)
            val dateIndex = it.getColumnIndexOrThrow(NOTE_DATE)
            val bookTitleIndex = it.getColumnIndexOrThrow(NOTE_BOOK_TITLE)
            val pagesIndex = it.getColumnIndexOrThrow(NOTE_PAGES)
            val coverIndex = it.getColumnIndexOrThrow(NOTE_COVER_RES)

            while (it.moveToNext()) {
                notes.add(
                    Note(
                        id = it.getLong(idIndex).toString(),
                        date = it.getString(dateIndex),
                        bookTitle = it.getString(bookTitleIndex),
                        pages = it.getString(pagesIndex),
                        coverRes = it.getInt(coverIndex)
                    )
                )
            }
        }
        return notes
    }

    fun updateNote(note: Note): Int {
        val noteId = note.id.toLongOrNull() ?: return 0
        val values = ContentValues().apply {
            put(NOTE_DATE, note.date)
            put(NOTE_BOOK_TITLE, note.bookTitle)
            put(NOTE_PAGES, note.pages)
            put(NOTE_COVER_RES, note.coverRes)
        }
        return writableDatabase.update(
            TABLE_NOTES,
            values,
            "$NOTE_ID = ?",
            arrayOf(noteId.toString())
        )
    }

    fun deleteNote(id: String): Int {
        val noteId = id.toLongOrNull() ?: return 0
        return writableDatabase.delete(
            TABLE_NOTES,
            "$NOTE_ID = ?",
            arrayOf(noteId.toString())
        )
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val books = listOf(
            Book("0", "Мастер и Маргарита", "Михаил Булгаков", "Роман", "Читаю", 63, R.drawable.cover_master),
            Book("0", "1984", "Джордж Оруэлл", "Антиутопия", "В планах", 0, R.drawable.cover_1984),
            Book("0", "Преступление и наказание", "Федор Достоевский", "Роман", "Прочитано", 100, R.drawable.cover_master)
        )

        books.forEach { book ->
            val values = ContentValues().apply {
                put(BOOK_TITLE, book.title)
                put(BOOK_AUTHOR, book.author)
                put(BOOK_GENRE, book.genre)
                put(BOOK_STATUS, book.status)
                put(BOOK_PROGRESS, book.progress)
                put(BOOK_COVER_RES, book.coverRes)
            }
            db.insert(TABLE_BOOKS, null, values)
        }
    }

    companion object {
        private const val DATABASE_NAME = "litgo.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_BOOKS = "books"
        private const val BOOK_ID = "id"
        private const val BOOK_TITLE = "title"
        private const val BOOK_AUTHOR = "author"
        private const val BOOK_GENRE = "genre"
        private const val BOOK_STATUS = "status"
        private const val BOOK_PROGRESS = "progress"
        private const val BOOK_COVER_RES = "cover_res"

        private const val TABLE_NOTES = "notes"
        private const val NOTE_ID = "id"
        private const val NOTE_DATE = "date"
        private const val NOTE_BOOK_TITLE = "book_title"
        private const val NOTE_PAGES = "pages"
        private const val NOTE_COVER_RES = "cover_res"

        @Volatile
        private var instance: AppDatabaseHelper? = null

        fun getInstance(context: Context): AppDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: AppDatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }
}
