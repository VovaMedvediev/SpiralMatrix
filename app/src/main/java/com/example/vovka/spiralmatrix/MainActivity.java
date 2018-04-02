package com.example.vovka.spiralmatrix;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<Integer> list;
    private int size;
    private static int[] arr = {0, 1, 19, 271, 3439, 40951, 468559};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMatrix(View view) {
        EditText matrixSizeEditText = (EditText) findViewById(R.id.matrixSizeEditText);
        String matrixSize = matrixSizeEditText.getText().toString().trim();
        size = Integer.valueOf(matrixSize);
        new ProgressTask().execute();
    }

    public List<Integer> twoDArrayToList(Integer[][] twoDArray){
        list = new ArrayList<Integer>();
        for(Integer[] array : twoDArray) {
            list.addAll(Arrays.asList(array));
        }
        return list;
    }

    public static int countExcludedNumber(int numb, int excludedNumber){
        int currentStep = 0;
        int previousStep = -1;
        while(currentStep != previousStep) {
            previousStep = currentStep;
            int tempNumb = numb + previousStep;
            currentStep = getMaxValueForMatrix(tempNumb, excludedNumber);
        }
        return currentStep;
    }

    public static int getMaxValueForMatrix(Integer maxNumber, int excludedNumber){
        int result = 0;
        int temp = maxNumber;
        for( int i = maxNumber.toString().length() - 1; i > 0; i--){
            double x = Math.pow(10, i);
            double divider = Math.floor(temp/x);
            if(divider < excludedNumber){
                result += (int) ((arr[i - 1] * 9 + Math.pow(10, i - 1)) * divider);
            } else if(divider == excludedNumber) {
                result += (int) ((arr[i - 1] * 9 + Math.pow(10, i - 1) * divider) + temp % (int) x);
                return result;
            } else {
                result += (int) ((arr[i - 1] * 9 + Math.pow(10, i - 1)) * (divider - 1)) + (int) x;
            }
            temp %= (int) x;
            if(temp == 0) {
                break;
            }
        }
        if(maxNumber % 10 > excludedNumber) {
            result+=1;
        }
        return result;
    }

    class ProgressTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Integer[][] spiral = new Integer[size][size];
            CharSequence cs1 = "6";
            Integer value = size*size + countExcludedNumber(size*size, 6);
            Integer minCol = 0;
            Integer maxCol = size-1;
            Integer minRow = 0;
            Integer maxRow = size-1;
            while(value>0){
                for(int i = minRow; i <= maxRow; i++){
                    while(String.valueOf(value).contains(cs1)){
                        value--;
                    }
                    spiral[i][minCol] = value;
                    value--;
                }
                for(int i = minCol+1; i <= maxCol; i++){
                    while(String.valueOf(value).contains(cs1)){
                        value--;
                    }
                    spiral[maxRow][i] = value;
                    value--;
                }
                for(int i = maxRow-1; i >= minRow; i--){
                    while(String.valueOf(value).contains(cs1)){
                        value--;
                    }
                    spiral[i][maxCol] = value;
                    value--;
                }
                for(int i = maxCol-1; i >= minCol+1; i--){
                    while(String.valueOf(value).contains(cs1)){
                        value--;
                    }
                    spiral[minRow][i] = value;
                    value--;
                }
                minCol++;
                minRow++;
                maxCol--;
                maxRow--;
            }
            twoDArrayToList(spiral);
            return null;
        }
    }
}
