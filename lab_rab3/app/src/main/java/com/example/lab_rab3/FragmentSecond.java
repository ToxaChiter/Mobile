package com.example.lab_rab3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentSecond extends Activity {

    public static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_second);

        ListView categoriesL = findViewById(R.id.listFormulas);

        ArrayList<HashMap<String, String>> formList = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(AssetJSONFile("data.json", this));
            JSONArray m_jArry = obj.getJSONArray("books");
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);

                String id_value = jo_inside.getString("id");
                String title_value = jo_inside.getString("title");
                String author_value = jo_inside.getString("author");
                String year_value = jo_inside.getString("year");
                String rating_value = jo_inside.getString("rating");
                String language_value = jo_inside.getString("language");
                String genre_value = jo_inside.getString("genre");
                String image_url = jo_inside.getString("image");

                m_li = new HashMap<>();
                m_li.put("id", id_value);
                m_li.put("title", title_value);
                m_li.put("author", author_value);
                m_li.put("image", image_url);
                m_li.put("year", year_value);
                m_li.put("rating", rating_value);
                m_li.put("language", language_value);
                m_li.put("genre", genre_value);

                formList.add(m_li);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                formList,
                R.layout.list_item,
                new String[]{"year", "title", "author", "image", "rating", "language", "genre"},
                new int[]{R.id.bookYear, R.id.bookTitle, R.id.bookAuthor, R.id.itemPicture, R.id.bookRating, R.id.bookLanguageTextView, R.id.bookGenreTextView});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.bookRating) {
                    TextView textView = (TextView) view;
                    textView.setText("Рейтинг: " + textRepresentation);
                    return true;

                } else if (view.getId() == R.id.itemPicture && data instanceof String) {
                    ImageView imageView = (ImageView) view;
                    String imageUrl = (String) data;
                    Picasso.get().load(imageUrl).into(imageView);
                    return true;
                }
                return false;
            }
        });

        categoriesL.setAdapter(adapter);

        categoriesL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> book = (HashMap<String, String>) parent.getItemAtPosition(position);

                Intent intent = new Intent(FragmentSecond.this, DetailActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });
    }
}
