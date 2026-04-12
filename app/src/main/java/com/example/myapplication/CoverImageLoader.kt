package com.example.myapplication

import android.net.Uri
import android.widget.ImageView

fun ImageView.loadBookCover(coverUri: String?, coverRes: Int) {
    if (!coverUri.isNullOrBlank()) {
        val loaded = runCatching {
            setImageURI(Uri.parse(coverUri))
            drawable != null
        }.getOrDefault(false)

        if (loaded) return
    }

    setImageResource(coverRes)
}
