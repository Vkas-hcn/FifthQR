package com.yellow.scan.linear.mole.fifthqr.model
import android.app.Activity
import android.provider.MediaStore
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class ResultViewModel:ViewModel() {
    fun copyTextToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun searchInBrowser(context: Context, query: String) {
        val searchUrl = "https://www.google.com/search?q=${Uri.encode(query)}"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
        context.startActivity(browserIntent)
    }




}
