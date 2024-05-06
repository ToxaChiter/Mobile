package com.example.lab_rab3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookYearTextView;
    private TextView bookLanguageTextView;
    private TextView bookRatingTextView;
    private TextView bookGenreTextView;
    private ImageView bookImageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_detail);

        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        bookYearTextView = findViewById(R.id.bookYearTextView);
        bookLanguageTextView = findViewById(R.id.bookLanguageTextView);
        bookRatingTextView = findViewById(R.id.bookRatingTextView);
        bookGenreTextView = findViewById(R.id.bookGenreTextView);
        bookImageView = findViewById(R.id.bookImageView);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("book")) {
            HashMap<String, String> book = (HashMap<String, String>) intent.getSerializableExtra("book");
            displayBookDetails(book);
        }
    }

    private void displayBookDetails(HashMap<String, String> book) {
        bookTitleTextView.setText(book.get("title"));


        String authorText = "Автор:  " + book.get("author");
        SpannableString spannableString = new SpannableString(authorText);
        int startIndex = 0;
        int endIndex = startIndex + 6; // Индекс, начиная с которого нужно применять стиль и размер шрифта
        spannableString.setSpan(new AbsoluteSizeSpan(20, true), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookAuthorTextView.setText(spannableString);

        String yearText = "Дата публикации:  " + book.get("year");
        SpannableString spannableString1 = new SpannableString(yearText);
        int startIndex1 = 0;
        int endIndex1 = startIndex + 16; // Индекс, начиная с которого нужно применять стиль и размер шрифта
        spannableString1.setSpan(new AbsoluteSizeSpan(20, true), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(new StyleSpan(Typeface.BOLD), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookYearTextView.setText(spannableString1);


        String languageText = "Язык перевода:  " + book.get("language");
        SpannableString spannableString2 = new SpannableString(languageText);
        int startIndex2 = 0;
        int endIndex2 = startIndex1 + 14; // Индекс, начиная с которого нужно применять стиль и размер шрифта
        spannableString2.setSpan(new AbsoluteSizeSpan(20, true), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString2.setSpan(new StyleSpan(Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookLanguageTextView.setText(spannableString2);


        String genreText = "Жанр:  " + book.get("genre");
        SpannableString spannableString3 = new SpannableString(genreText);
        int startIndex3 = 0;
        int endIndex3 = startIndex + 5; // Индекс, начиная с которого нужно применять стиль и размер шрифта
        spannableString3.setSpan(new AbsoluteSizeSpan(20, true), startIndex3, endIndex3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString3.setSpan(new StyleSpan(Typeface.BOLD), startIndex3, endIndex3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookGenreTextView.setText(spannableString3);


        String ratingText = "Рейтинг читателей:  " + book.get("rating");
        SpannableString spannableString4 = new SpannableString(ratingText);
        int startIndex4 = 0;
        int endIndex4 = startIndex + 18; // Индекс, начиная с которого нужно применять стиль и размер шрифта
        spannableString4.setSpan(new AbsoluteSizeSpan(20, true), startIndex4, endIndex4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString4.setSpan(new StyleSpan(Typeface.BOLD), startIndex4, endIndex4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookRatingTextView.setText(spannableString4);


        String imageUrl = book.get("image");
        Picasso.get().load(imageUrl).into(bookImageView);
    }
}
