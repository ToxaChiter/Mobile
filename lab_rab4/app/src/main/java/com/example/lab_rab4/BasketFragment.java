package com.example.lab_rab4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

public class BasketFragment extends Activity {

    Button button;

    private ListView basketListView;
    private ProductAdapter basketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basket_fragment);

        final ArrayList<Product> selectedProducts = getIntent().getParcelableArrayListExtra("selectedProducts");

        for (Product product : selectedProducts) {
            product.setSelected(false);
        }

        basketListView = findViewById(R.id.basketList);
        final TextView selectedCountTextView = findViewById(R.id.selected_count);
        basketAdapter = new ProductAdapter(this, R.layout.item_product, selectedCountTextView);
        basketListView.setAdapter(basketAdapter);

        basketAdapter.addAll(selectedProducts);
        basketAdapter.notifyDataSetChanged();

        basketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = selectedProducts.get(position);
                boolean isSelected = !selectedProduct.isSelected();
                selectedProduct.setSelected(isSelected);
                // Обновляем данные в адаптере
                basketAdapter.notifyDataSetChanged();

                // Обновляем значение selectedCount и отображаем в selectedCountTextView
                int selectedCount = 0;
                for (Product product : selectedProducts) {
                    if (product.isSelected()) {
                        selectedCount++;
                    }
                }
                selectedCountTextView.setText("Выбрано: " + selectedCount);
            }
        });

        button = (Button) findViewById(R.id.buttonDelete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean anySelected = false;
                ArrayList<Product> newSelectedProducts = new ArrayList<>();

                // Проверяем, есть ли выбранные элементы
                for (Product product : selectedProducts) {
                    if (product.isSelected()) {
                        anySelected = true;
                    } else {
                        newSelectedProducts.add(product);
                    }
                }

                if (anySelected) {
                    // Обновляем список выбранных элементов и адаптер
                    selectedProducts.clear();
                    selectedProducts.addAll(newSelectedProducts);
                    basketAdapter.setProductList(selectedProducts);
                    basketAdapter.notifyDataSetChanged();

                    // Обновляем значение selectedCount и отображаем в selectedCountTextView
                    int selectedCount = 0;
                    for (Product product : selectedProducts) {
                        if (product.isSelected()) {
                            selectedCount++;
                        }
                    }
                    selectedCountTextView.setText("Выбрано: " + selectedCount);
                } else {
                    // Выводим сообщение, если нет выбранных элементов
                    Toast.makeText(BasketFragment.this, "Выберите для удаления", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}