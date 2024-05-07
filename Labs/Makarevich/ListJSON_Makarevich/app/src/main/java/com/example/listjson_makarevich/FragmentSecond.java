package com.example.listjson_makarevich;

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

        ArrayList<HashMap<String, String>> movieList = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(AssetJSONFile("data.json", this));
            JSONArray m_jArry = obj.getJSONArray("movies_and_shows");
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);

                String id_value = jo_inside.getString("id");
                String title_value = jo_inside.getString("title");
                String type_value = jo_inside.getString("type");
                String director_value = jo_inside.getString("director");
                String year_value = jo_inside.getString("year");
                String rating_value = jo_inside.getString("rating");
                String genre_value = jo_inside.getString("genre");
                String image_url = jo_inside.getString("image");

                m_li = new HashMap<>();
                m_li.put("id", id_value);
                m_li.put("title", title_value);
                m_li.put("type", type_value);
                m_li.put("director", director_value);
                m_li.put("image", image_url);
                m_li.put("year", year_value);
                m_li.put("rating", rating_value);
                m_li.put("genre", genre_value);

                movieList.add(m_li);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                movieList,
                R.layout.list_item,
                new String[]{"year", "title", "type", "director", "rating", "genre", "image"},
                new int[]{R.id.movieYearTextView, R.id.movieTitleTextView, R.id.movieTypeTextView, R.id.movieDirectorTextView, R.id.movieRatingTextView, R.id.movieGenreTextView, R.id.movieImageView});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.movieRatingTextView) {
                    ((TextView) view).setText("Рейтинг: " + textRepresentation);
                    return true;
                } else if (view.getId() == R.id.movieImageView && data instanceof String) {
                    Picasso.get().load((String) data).into((ImageView) view);
                    return true;
                }
                return false;
            }
        });

        categoriesL.setAdapter(adapter);

        categoriesL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> movie = (HashMap<String, String>) parent.getItemAtPosition(position);
                Intent intent = new Intent(FragmentSecond.this, DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });
    }
}
