package com.example.minishop_pechko;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    ArrayList<Product> products = new ArrayList<Product>();
    BoxAdapter boxAdapter;
    TextView footerTextView; // добавьте это

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // создаем адаптер
        fillData();
        footerTextView = (TextView) findViewById(R.id.footerTextView); // замените на id вашего TextView
        boxAdapter = new BoxAdapter(this, products, footerTextView); // передайте footerTextView

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);


        // инициализируем TextView
        footerTextView = (TextView) findViewById(R.id.footerTextView); // замените на id вашего TextView
    }

    // генерируем данные для адаптера
    void fillData() {
        String[] clothingItems = {"T-shirt", "Jeans", "Dress", "Skirt", "Jacket", "Sweater", "Blouse", "Shorts", "Coat", "Pants",
                "Scarf", "Hat", "Socks", "Underwear", "Gloves", "Tie", "Belt", "Shoes", "Boots", "Sandals"};

        for (int i = 0; i < clothingItems.length; i++) {
            if (i == 0) {
                products.add(new Product(clothingItems[i % clothingItems.length] + " by Pechko", (i + 1) * 1000, false));
            } else {
                products.add(new Product(clothingItems[i % clothingItems.length], (i + 1) * 1000, false));
            }
        }
    }

    // выводим информацию о корзине
    public void showResult(View v) {
        ArrayList<Product> resultProducts = boxAdapter.getBox();
        // обновляем TextView
        footerTextView.setText("Activated products: " + resultProducts.size());
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("products", resultProducts);
        startActivity(intent);
    }
}