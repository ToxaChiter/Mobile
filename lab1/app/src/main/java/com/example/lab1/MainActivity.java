package com.example.lab1;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    String oldNumber;
    String operator = "";
    Boolean isNew = true;

    EditText editText; // Поле вывода значений
    EditText NoeditText;    // Подпись сверху

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextText);
        editText.setEnabled(false); // Чтобы нельзя было изменять
        NoeditText = findViewById(R.id.YouDontDeleteThis);
        NoeditText.setEnabled(false);
    }

    // Обработка нажатия кнопок
    public void clickNumber(View view) {

        if (isNew)
            editText.setText("");
        isNew = false;

        String number = editText.getText().toString();

        if (view.getId() == R.id.buttonNull) {
            if (zeroIsFirst(number) && number.length() == 1) {  // Проверка на правильное отображение нулей для каждой цифры
                number = "0";
            } else {
                number = number + "0";
            }

        } else if (view.getId() == R.id.buttonOne) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "1";

        } else if (view.getId() == R.id.buttonTwo) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "2";

        } else if (view.getId() == R.id.buttonThree) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "3";

        } else if (view.getId() == R.id.buttonFour) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "4";

        } else if (view.getId() == R.id.buttonFive) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "5";

        } else if (view.getId() == R.id.buttonSix) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "6";

        } else if (view.getId() == R.id.buttonSeven) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "7";

        } else if (view.getId() == R.id.buttonEight) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "8";

        } else if (view.getId() == R.id.buttonNine) {
            if (zeroIsFirst(number) && number.length() == 1) {
                number = number.substring(1);
            }
            number = number + "9";

        } else if (view.getId() == R.id.buttonComma) {

            if (commaIsPresent(number) && number.length() == 1) {
            } else
            if (zeroIsFirst(number)) {
                number = "0.";
            } else {
                number = number + ".";
            }

        } else if (view.getId() == R.id.buttonChangeSing) {


            if(numberIsZero(number)) {
                number = "0";
            } else {
                if (minusIsPresent(number)) {
                    number = number.substring(1);
                } else {
                    number = "-" + number;
                }
            }

        }
        editText.setText(number);
    }

    // Обработчик арифметических операций
    public void Operation(View view) {
        isNew = true;
        oldNumber = editText.getText().toString();

        if (view.getId() == R.id.buttonMinus) {
            operator = "-";
        } else if (view.getId() == R.id.buttonPlus) {
            operator = "+";
        } else if (view.getId() == R.id.buttonMultiplication) {
            operator = "×";
        } else if (view.getId() == R.id.buttonDivision) {
            operator = "÷";
        }
    }

    // Обработчик процентов
    public void clickPercent(View view) {
        if (operator == "") {
            String number = editText.getText().toString();
            double temp = Double.parseDouble(number) / 100;
            number = temp + "";
            editText.setText(number);

        } else {
            String newNumber = editText.getText().toString();
            Double resultNumber = 0.0;

            switch (operator) {
                case "-":
                    resultNumber = Double.parseDouble(oldNumber) - Double.parseDouble(oldNumber) * Double.parseDouble(newNumber) / 100;
                    break;

                case "+":
                    resultNumber = Double.parseDouble(oldNumber) + Double.parseDouble(oldNumber) * Double.parseDouble(newNumber) / 100;
                    break;

                case "×":
                    resultNumber = Double.parseDouble(oldNumber) * Double.parseDouble(newNumber) / 100;
                    break;

                case "÷":
                    resultNumber = Double.parseDouble(oldNumber) / Double.parseDouble(newNumber) * 100;
                    break;
            }
            editText.setText(resultNumber + "");
            operator = "";
        }
    }

    // обработчик равно
    public void clickEqual (View view) {

        String newNumber = editText.getText().toString();
        Double resultNumber = 0.0;

        if (Double.parseDouble(newNumber) < 0.00000001 && operator == "/" || newNumber.equals("") && operator == "/") {
            Toast.makeText(MainActivity.this, "Операция невозможна", Toast.LENGTH_LONG).show();
        } else {

            switch (operator) {
                case "-":
                    resultNumber = Double.parseDouble(oldNumber) - Double.parseDouble(newNumber);
                    break;

                case "+":
                    resultNumber = Double.parseDouble(oldNumber) + Double.parseDouble(newNumber);
                    break;

                case "×":
                    resultNumber = Double.parseDouble(oldNumber) * Double.parseDouble(newNumber);
                    break;

                case "÷":
                    resultNumber = Double.parseDouble(oldNumber) / Double.parseDouble(newNumber);
                    break;
            }
            editText.setText(resultNumber + "");
        }
    }

    // Обработчик сброса значений АС
    public void clickAC (View view) {
        editText.setText("0");
        isNew = true;
    }

    // Обработчик наличия точки
    public boolean commaIsPresent (String number) {
        if (!number.contains(".")) {
            return false;
        } else {
            return true;
        }
    }


    // Обработчик наличия минуса перед числом
    public boolean minusIsPresent (String number) {
        if (number.charAt(0) == '-') {
            return true;
        } else {
            return false;
        }
    }

    // Обработчик для первого нуля (чтобы изменялся на цифру и не было много нулей)
    private boolean zeroIsFirst(String number) {
        if(number.equals("")) {
            return true;
        }
        if (number.charAt(0) == '0') {
            return true;
        } else {
            return false;
        }
    }

    private boolean numberIsZero(String number) {
        if(number.equals("0") || number.equals("")) {
            return true;
        } else
            return false;
    }
}
