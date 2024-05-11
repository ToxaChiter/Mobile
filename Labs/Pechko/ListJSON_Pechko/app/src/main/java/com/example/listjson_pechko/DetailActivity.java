package com.example.listjson_pechko;

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
    private TextView flowerNameTextView;
    private TextView flowerColorTextView;
    private TextView flowerOriginTextView;
    private TextView flowerMeaningTextView;
    private ImageView flowerImageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_detail);

        flowerNameTextView = findViewById(R.id.flowerNameTextView);
        flowerColorTextView = findViewById(R.id.flowerColorTextView);
        flowerOriginTextView = findViewById(R.id.flowerOriginTextView);
        flowerMeaningTextView = findViewById(R.id.flowerMeaningTextView);
        flowerImageView = findViewById(R.id.flowerImageView);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("flower")) {
            HashMap<String, String> flower = (HashMap<String, String>) intent.getSerializableExtra("flower");
            displayFlowerDetails(flower);
        }
    }

    private void displayFlowerDetails(HashMap<String, String> flower) {
        flowerNameTextView.setText(flower.get("name"));

        String colorText = "Цвет:  " + flower.get("color");
        SpannableString spannableString = new SpannableString(colorText);
        int startIndex = 0;
        int endIndex = startIndex + 5;
        spannableString.setSpan(new AbsoluteSizeSpan(20, true), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        flowerColorTextView.setText(spannableString);

        String originText = "Происхождение:  " + flower.get("origin");
        SpannableString spannableString1 = new SpannableString(originText);
        int startIndex1 = 0;
        int endIndex1 = startIndex + 13;
        spannableString1.setSpan(new AbsoluteSizeSpan(20, true), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(new StyleSpan(Typeface.BOLD), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        flowerOriginTextView.setText(spannableString1);

        String meaningText = "Значение:  " + flower.get("meaning");
        SpannableString spannableString2 = new SpannableString(meaningText);
        int startIndex2 = 0;
        int endIndex2 = startIndex1 + 8;
        spannableString2.setSpan(new AbsoluteSizeSpan(20, true), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString2.setSpan(new StyleSpan(Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        flowerMeaningTextView.setText(spannableString2);

        String imageUrl = flower.get("image");
        Picasso.get().load(imageUrl).into(flowerImageView);
    }
}
