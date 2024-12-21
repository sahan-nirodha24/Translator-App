package com.example.translatorapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {

    private lateinit var sourceEditText: EditText
    private lateinit var translateButton: Button
    private lateinit var targetTextView: TextView
    private lateinit var languageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceEditText = findViewById(R.id.sourceEditText)
        translateButton = findViewById(R.id.translateButton)
        targetTextView = findViewById(R.id.targetTextView)
        languageSpinner = findViewById(R.id.languageSpinner)

        val languages = mapOf(
            "Spanish" to TranslateLanguage.SPANISH,
            "French" to TranslateLanguage.FRENCH,
            "German" to TranslateLanguage.GERMAN,
            "Chinese" to TranslateLanguage.CHINESE,
            "Japanese" to TranslateLanguage.JAPANESE,
            "Hindi" to TranslateLanguage.HINDI,
            "Arabic" to TranslateLanguage.ARABIC,
        )

        languageSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, languages.keys.toList()
        )

        // Add a text change listener to clear the targetTextView when the user starts typing
        sourceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text change
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the target text when user starts typing
                targetTextView.text = ""
            }

            override fun afterTextChanged(editable: Editable?) {
                // No action needed after text change
            }
        })

        translateButton.setOnClickListener {
            val sourceText = sourceEditText.text.toString()
            val selectedLanguage = languageSpinner.selectedItem.toString()

            if (sourceText.isNotEmpty() && languages.containsKey(selectedLanguage)) {
                translateText(sourceText, languages[selectedLanguage]!!)
            } else {
                Toast.makeText(this, "Please enter text and select a language", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun translateText(text: String, targetLanguage: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()

        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        targetTextView.text = translatedText
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Translation failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Model download failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
