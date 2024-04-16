package com.example.lab_rab4;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView productListView;
    private ProductAdapter productAdapter;
    private TextView selectedCountTextView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.buttonBasket);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Product> selectedProducts = new ArrayList<>();
                for (int i = 0; i < productAdapter.getCount(); i++) {
                    if (productAdapter.getItem(i).isSelected()) {
                        selectedProducts.add(productAdapter.getItem(i));
                    }
                }

                // Проверяем, что выбран хотя бы один элемент
                if (selectedProducts.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, BasketFragment.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("selectedProducts", selectedProducts);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    // Если список выбранных элементов пуст, показываем сообщение
                    Toast.makeText(MainActivity.this, "Ваша корзина пуста", Toast.LENGTH_SHORT).show();
                }
            }
        });

        productListView = findViewById(R.id.productList);
        selectedCountTextView = findViewById(R.id.selected_count);
        productAdapter = new ProductAdapter(this, R.layout.item_product, selectedCountTextView);
        productListView.setAdapter(productAdapter);
        final TextView selectedCountTextView = findViewById(R.id.selected_count);

        productAdapter.add(new Product("Яблоки","Цена, руб: 3,35", "Вес: 1,3 кг"));
        productAdapter.add(new Product("Картофель", "Цена, руб: 4,99", "Вес: 2,5 кг"));
        productAdapter.add(new Product("Киви", "Цена, руб: 7,99", "Вес: 0,7 кг"));
        productAdapter.add(new Product("Подсолнечное масло", "Цена, руб: 3,19", "Объём: 800 мл"));
        productAdapter.add(new Product("Яйца куриные", "Цена, руб: 3,99", "Кол-во: 10 шт"));
        productAdapter.add(new Product("Сыр", "Цена за кг, руб: 16,99", "Вес: 400 г"));
        productAdapter.add(new Product("Мука пшеничная", "Цена, руб: 2,99", "Вес: 2000 г"));
        productAdapter.add(new Product("Шашлык", "Цена, руб: 10,99", "Вес: 1 кг"));
        productAdapter.add(new Product("Шоколад Milka", "Цена, руб: 18,45", "Масса: 300 г"));
        productAdapter.add(new Product("Кофе Jacobs Monarch", "Цена, руб: 31,99", "Масса: 400 г"));
        productAdapter.add(new Product("Крупа гречневая", "Цена, руб: 2,02", "Вес: 900 г"));
        productAdapter.add(new Product("Сок Rich", "Цена, руб: 4,95", "Объём: 1 л"));
        productAdapter.add(new Product("Сметана 20%", "Цена, руб: 3,06", "Масса: 380 г"));
        productAdapter.add(new Product("Творог 5%", "Цена, руб: 1,43", "масса: 200 г"));
        productAdapter.add(new Product("Филе цыплёнка", "Цена, руб: 13,60", "Вес: 600 г"));
        productAdapter.add(new Product("Колбаса варёная", "Цена, руб: 4,19", "Масса: 400 г"));
        productAdapter.add(new Product("Вода Darida", "Цена, руб: 1,59", "Объём: 1,5 л"));

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = productAdapter.getItem(position);
                selectedProduct.setSelected(!selectedProduct.isSelected());

                int selectedCount = 0;
                for (int i = 0; i < productAdapter.getCount(); i++) {
                    if (productAdapter.getItem(i).isSelected()) {
                        selectedCount++;
                    }
                }
                if (selectedCountTextView == null) {
                    Log.e("BasketFragment", "selectedCountTextView is null");
                } else {
                    selectedCountTextView.setText("Выбрано: " + selectedCount);
                }
                productAdapter.notifyDataSetChanged();
            }
        });
    }
}
