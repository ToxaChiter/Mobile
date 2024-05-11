package com.example.listjson_makarevich;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    private TextView movieTitleTextView;
    private TextView movieTypeTextView;
    private TextView movieDirectorTextView;
    private TextView movieYearTextView;
    private TextView movieRatingTextView;
    private TextView movieGenreTextView;
    private ImageView movieImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_detail);

        movieTitleTextView = findViewById(R.id.movieTitleTextView);
        movieTypeTextView = findViewById(R.id.movieTypeTextView);
        movieDirectorTextView = findViewById(R.id.movieDirectorTextView);
        movieYearTextView = findViewById(R.id.movieYearTextView);
        movieRatingTextView = findViewById(R.id.movieRatingTextView);
        movieGenreTextView = findViewById(R.id.movieGenreTextView);
        movieImageView = findViewById(R.id.movieImageView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            HashMap<String, String> movie = (HashMap<String, String>) intent.getSerializableExtra("movie");
            displayMovieDetails(movie);
        }
    }

    private void displayMovieDetails(HashMap<String, String> movie) {
        movieTitleTextView.setText(movie.get("title"));
        movieTypeTextView.setText("Тип: " + movie.get("type"));
        movieDirectorTextView.setText("Режиссер: " + movie.get("director"));
        movieYearTextView.setText("Год выпуска: " + movie.get("year"));
        movieGenreTextView.setText("Жанр: " + movie.get("genre"));
        movieRatingTextView.setText("Рейтинг: " + movie.get("rating"));

        // Отображение изображения с использованием Picasso
        String imageUrl = movie.get("image");
        Picasso.get().load(imageUrl).into(movieImageView);
    }
}
