package com.example.calculatorlab1zam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText eText;
    boolean isFirst;

    double firstOperand;
    double secondOperand;

    double coefficient;

    String operator;
    String result;

    boolean skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eText = findViewById(R.id.editText);
        isFirst = true;
        skip = false;
        coefficient = 1;
    }

    public void clickNumber(View view){
        String number = eText.getText().toString();
        if(number.equals("0")){
            number = "";
        }
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        number = number + buttonText;
        eText.setText(number);
    }

    public void clickOperation(View view){
        int bID = view.getId();

        if (bID==R.id.buttonPoint){
            String number = eText.getText().toString();
            number = number + ".";
            eText.setText(number);
        }
        else if (bID==R.id.buttonAC) {
            eText.setText("0");
            isFirst = true;
            skip = false;
            coefficient = 1;
        }
        else if (bID==R.id.buttonC) {
            String number = eText.getText().toString();
            String str = number.substring(number.length()-1);
            if (str.matches("[-+/*]")){
                isFirst=true;
            }
            if (number.length()==1){
                eText.setText("0");
            }
            else{
                number = number.substring(0, number.length()-1);
                eText.setText(number);
            }


        }
        else if (bID==R.id.buttonPlus) {

            String number = eText.getText().toString();
            if (!isFirst) {
                performOperation(number);
                if(skip){
                    return;
                }
            }
            number = eText.getText().toString();
            number = number + "+";
            eText.setText(number);
            isFirst = false;
            operator = "+";
        }
        else if (bID==R.id.buttonMinus) {

            String number = eText.getText().toString();
            if (!isFirst) {
                performOperation(number);
                if(skip){
                    return;
                }
            }
            number = eText.getText().toString();
            number = number + "-";
            eText.setText(number);
            isFirst = false;
            operator = "-";
        }
        else if (bID==R.id.buttonMul) {

            String number = eText.getText().toString();
            if (!isFirst) {
                performOperation(number);
                if(skip){
                    return;
                }
            }
            number = eText.getText().toString();
            number = number + "*";
            eText.setText(number);
            isFirst = false;
            operator = "*";
        }
        else if (bID==R.id.buttonDEV) {

            String number = eText.getText().toString();
            if (!isFirst) {
                performOperation(number);
                if(skip){
                    return;
                }
            }
            number = eText.getText().toString();
            number = number + "/";
            eText.setText(number);
            isFirst = false;
            operator = "/";
        }
        else if (bID==R.id.buttonSQRT) {
            String number = eText.getText().toString();
            String[] operands = number.split("[-+/*]");

            if (operands.length != 2 ){
                result = Math.sqrt(Double.parseDouble(number)) + "";
                eText.setText(result);
            }
            else {
                performOperation(number);
                number = eText.getText().toString();
                result = Math.sqrt(Double.parseDouble(number)) + "";
                eText.setText(result);
            }
            isFirst=true;
        }
        else if (bID==R.id.buttonEquals) {
            String number = eText.getText().toString();
            performOperation(number);
            isFirst=true;
        }
    }

    public void performOperation(String num){
        if(num.charAt(0) =='-'){
            num = num.substring(1);
            coefficient = -1;
        }
        String[] operands = num.split("[-+/*]");
        if (operands.length != 2){
            skip = true;
            return;
        }
        firstOperand = Double.parseDouble(operands[0]);
        secondOperand = Double.parseDouble(operands[1]);

        switch (operator){
            case "+":
                result = coefficient*firstOperand + secondOperand + "";
                eText.setText(result);
                break;
            case "-":
                result = coefficient*firstOperand - secondOperand + "";
                eText.setText(result);
                break;
            case "*":
                result = coefficient*firstOperand * secondOperand + "";
                eText.setText(result);
                break;
            case "/":
                if (secondOperand==0){
                    result="Error!";
                    break;
                }
                result = coefficient*firstOperand / secondOperand + "";
                eText.setText(result);
                break;
        }

        eText.setText(result);
        coefficient=1;
    }
}