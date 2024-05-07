package com.example.lab2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText request;
    ImageView image;
    Button next, prev, search, download, goToWebSite;

    RatingBar ratingBar;
    private static final String BASE_URL = "https://www.google.com/search?tbm=isch&q=";
    private int currentIndex = 0; // переменная для хранения текущего индекса загруженной картинки
    // Метод загрузки картинки по указанному индексу из списка imageUrls
    // поиск изображений по запросу
    List<String> imageUrls = new ArrayList<>();
    List<String> pageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

    }

    void initView() {
        request = findViewById(R.id.requestURL);
        image = findViewById(R.id.image);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        search = findViewById(R.id.search);
        download = findViewById(R.id.download);
        goToWebSite = findViewById(R.id.visitSite);
        ratingBar = findViewById(R.id.ratingBar);

        download.setEnabled(false);
        next.setEnabled(false);
        prev.setEnabled(false);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWebSite.setEnabled(false);
                download.setEnabled(false);
                next.setEnabled(false);
                prev.setEnabled(false);

                String userInput = request.getText().toString();
                Log.d("MainActivity", "Запрос пользователя: " + userInput);
                if (!userInput.isEmpty()) {
                    String searchUrl = BASE_URL + userInput;
                    Log.d("MainActivity", "Созданный URL: " + searchUrl);
                    currentIndex = 0;
                    imageUrls.clear();
                    pageUrls.clear();

                    AsyncTask<String, Void, Document> execute = new UploadingImagesFromInternet().execute(searchUrl);

                } else {
                    // Показываем сообщение об ошибке
                    android.widget.Toast.makeText(MainActivity.this, "Введите текстовый запрос", android.widget.Toast.LENGTH_LONG).show();
                    ImageView imageView = findViewById(R.id.image);
                }
            }
        });
    }

    private class UploadingImagesFromInternet extends AsyncTask<String, Void, Document>
    {
        protected Document doInBackground(String... params)
        {
            try {
                String searchUrl = params[0];
                String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
                return Jsoup.connect(searchUrl).userAgent(userAgent).referrer("https://www.google.com/").get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        protected void onPostExecute(Document doc)
        {
            if (doc != null)
            {
                Elements elementsImg = doc.select("img[src]");
                for (Element imgElement : elementsImg)
                {
                    String imgUrl = imgElement.attr("src");
                    // Проверяем, не заканчивается ли ссылка расширением .gif
                    if (!imgUrl.endsWith(".gif"))
                    {
                        // Преобразуем относительный URL в абсолютный
                        try {
                            URL url = new URL(new URL(doc.baseUri()), imgUrl);
                            String absoluteUrl = url.toString() + "?size=original";
                            imageUrls.add(absoluteUrl);

                            // Получаем ссылку на страницу с изображением
                            String pageUrl = Objects.requireNonNull(imgElement.parents().select("a[href]").first()).attr("href");

                            URL baseUrl = new URL(new URL(doc.baseUri()), pageUrl);
                            String absolutePageUrl = baseUrl.toString();
                            pageUrls.add(absolutePageUrl);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Выводим массивы абсолютных ссылок на изображения и ссылок на страницы с изображениями
                Log.d("MainActivity", "Image URLs: " + imageUrls);
                Log.d("MainActivity", "Page URLs: " + pageUrls);

                // Создаем экземпляр класса UploadingImagesFromInternet
                UploadingImagesFromInternet uploader = new UploadingImagesFromInternet();
                // Передаем список абсолютных URL-адресов изображений uploader
                loadImageFromUrl(imageUrls, pageUrls);
            } else {
                // Обработка ошибки, если doc равен null
                Log.d("MainActivity", "Doc is null");
            }
        }
    }

    private Pair<String, String> loadImageFromUrl(List<String> imageUrls, List<String> pageUrls, int index)
    {
        String imageUrl = null;
        String pageUrl = null;


        if (index >= 0 && index < imageUrls.size())
        {
            imageUrl = imageUrls.get(index);
            pageUrl = pageUrls.get(index);
            Picasso.get().load(imageUrl).into(image);
        }
        currentIndex = index;

        prev.setEnabled(true);
        next.setEnabled(true);
        download.setEnabled(true);
        goToWebSite.setEnabled(true);

        return new Pair<>(imageUrl, pageUrl);
    }
    public void loadImageFromUrl(List<String> imageUrls, List<String> pageUrls)
    {
        loadImageFromUrl(imageUrls, pageUrls, currentIndex); // Загружаем первую картинку (индекс 0)
        Log.d("MainActivity", "Индекс текущей картинки: "  + currentIndex);
    }
}