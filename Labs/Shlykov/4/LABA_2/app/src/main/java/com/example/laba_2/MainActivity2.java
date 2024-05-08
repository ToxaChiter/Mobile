package com.example.laba_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity2 extends Activity {
    ArrayList<Product> products;
    BoxAdapter boxAdapter;
    ListView lvMain;
    TextView footerTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Получаем данные из MainActivity1 через Intent
        Intent intent = getIntent();
        products = (ArrayList<Product>) intent.getSerializableExtra("products");

        footerTextView = (TextView) findViewById(R.id.footerTextView);
        boxAdapter = new BoxAdapter(this, products, footerTextView);



        lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);
    }
    public void showSelectedProducts(View v) {
        ArrayList<Product> selectedProducts = boxAdapter.getBox();
        boxAdapter.updateProducts(selectedProducts); // обновляем данные в адаптере
    }
}
