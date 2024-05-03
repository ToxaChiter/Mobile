package com.example.laba_8;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView detailTextView = (TextView) findViewById(R.id.detailTextView);

        String selectedItem = getIntent().getStringExtra("selectedItem");
        try {
            JSONObject itemData = new JSONObject(selectedItem);
            // Здесь вы можете использовать данные itemData для заполнения вашего интерфейса
            // Например, если у вас есть поле "name" в данных элемента, вы можете установить его как текст в detailTextView
            String name = itemData.getString("name");
            detailTextView.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

