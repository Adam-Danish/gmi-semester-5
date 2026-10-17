package com.example.exammarkcalculator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etExamMark;
    private TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LIFECYCLE", "onCreate");
        Toast.makeText(this, "onCreate called", Toast.LENGTH_SHORT).show();

        etExamMark = findViewById(R.id.etExamMark);
        tvResult = findViewById(R.id.tvResult);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnClear = findViewById(R.id.btnClear);

        btnCalculate.setOnClickListener(v -> calculateGrade());

        btnClear.setOnClickListener(v -> {
            etExamMark.setText("");
            tvResult.setText("Score: -\nGrade: -");
            etExamMark.requestFocus();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("LIFECYCLE", "onStart");
        Toast.makeText(this, "onStart called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("LIFECYCLE", "onResume");
        Toast.makeText(this, "onResume called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("LIFECYCLE", "onPause");
        Toast.makeText(this, "onPause called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("LIFECYCLE", "onStop");
        Toast.makeText(this, "onStop called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("LIFECYCLE", "onRestart");
        Toast.makeText(this, "onRestart called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("LIFECYCLE", "onDestroy");
        Toast.makeText(this, "onDestroy called", Toast.LENGTH_SHORT).show();
    }

    private void calculateGrade() {

        String input = etExamMark.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter your exam mark.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double mark;

        try {
            mark = Double.parseDouble(input);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid number.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!Double.isFinite(mark) || mark < 0 || mark > 100) {

            Toast.makeText(
                    this,
                    "Mark must be between 0 and 100.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String grade;

        if (mark >= 80) {
            grade = "A";

        } else if (mark >= 70) {
            grade = "B";

        } else if (mark >= 60) {
            grade = "C";

        } else if (mark >= 50) {
            grade = "D";

        } else if (mark >= 40) {
            grade = "E";

        } else {
            grade = "F";
        }

        String result = String.format(
                Locale.US,
                "Score: %.1f / 100\nGrade: %s",
                mark,
                grade
        );

        tvResult.setText(result);
    }
}