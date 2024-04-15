package com.example.listjson_pechko;

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

        ArrayList<HashMap<String, String>> flowerList = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(AssetJSONFile("data.json", this));
            JSONArray m_jArry = obj.getJSONArray("flowers");
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);

                String id_value = jo_inside.getString("id");
                String name_value = jo_inside.getString("name");
                String color_value = jo_inside.getString("color");
                String origin_value = jo_inside.getString("origin");
                String meaning_value = jo_inside.getString("meaning");
                String image_url = jo_inside.getString("image");

                m_li = new HashMap<>();
                m_li.put("id", id_value);
                m_li.put("name", name_value);
                m_li.put("color", color_value);
                m_li.put("origin", origin_value);
                m_li.put("meaning", meaning_value);
                m_li.put("image", image_url);

                flowerList.add(m_li);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                flowerList,
                R.layout.list_item,
                new String[]{"name", "color", "origin", "meaning", "image"},
                new int[]{R.id.flowerNameTextView, R.id.flowerColorTextView, R.id.flowerOriginTextView, R.id.flowerMeaningTextView, R.id.flowerImageView});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.flowerImageView && data instanceof String) {
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
                HashMap<String, String> flower = (HashMap<String, String>) parent.getItemAtPosition(position);

                Intent intent = new Intent(FragmentSecond.this, DetailActivity.class);
                intent.putExtra("flower", flower);
                startActivity(intent);
            }
        });
    }
}
