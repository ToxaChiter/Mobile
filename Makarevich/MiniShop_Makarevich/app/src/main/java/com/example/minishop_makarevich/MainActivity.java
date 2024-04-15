package com.example.minishop_makarevich;

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
        setContentView(R.layout.main);

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
        String[] toyNames = {"Teddy Bear", "Doll by Makarevich", "LEGO Set", "Toy Car", "Action Figure", "Puzzle", "Board Game", "Stuffed Animal", "RC Helicopter", "Play-Doh Set",
                "Barbie", "Hot Wheels", "Remote Control Car", "Train Set", "Yo-yo", "Kite", "Rubik's Cube", "Slime Kit", "Model Kit", "Building Blocks"};
        for (int i = 0; i < 20; i++) {
            products.add(new Product(toyNames[i % toyNames.length], (i + 1) * 1000, false));
        }
    }

    // выводим информацию о корзине
    public void showResult(View v) {
        ArrayList<Product> resultProducts = boxAdapter.getBox();
        // обновляем TextView
        footerTextView.setText("Activated Products: " + resultProducts.size());
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("products", resultProducts);
        startActivity(intent);
    }
}
