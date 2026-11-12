package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject_laravelsem5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String EXTRA_NAME = "com.example.miniproject_laravelsem5.NAME";
    public static final String EXTRA_ID = "com.example.miniproject_laravelsem5.ID";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(view -> startQuiz());

        binding.editStudentId.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startQuiz();
                return true;
            }
            return false;
        });
    }

    private void startQuiz() {
        if (!binding.btnStart.isEnabled()) {
            return;
        }

        String name = binding.editName.getText().toString().trim();
        String studentId = binding.editStudentId.getText().toString().trim();

        if (name.isEmpty()) {
            binding.editName.requestFocus();
            binding.editName.setError(getString(R.string.error_empty_name));
            return;
        }

        if (name.length() < 2 || !name.matches("\\p{L}[\\p{L}\\p{M} .'’-]*")) {
            binding.editName.requestFocus();
            binding.editName.setError(getString(R.string.error_invalid_name));
            return;
        }

        if (studentId.isEmpty()) {
            binding.editStudentId.requestFocus();
            binding.editStudentId.setError(getString(R.string.error_empty_id));
            return;
        }

        if (!studentId.matches("[A-Za-z0-9_-]{4,20}")) {
            binding.editStudentId.requestFocus();
            binding.editStudentId.setError(getString(R.string.error_invalid_id));
            return;
        }

        InputMethodManager keyboard = getSystemService(InputMethodManager.class);
        if (keyboard != null) {
            keyboard.hideSoftInputFromWindow(binding.editStudentId.getWindowToken(), 0);
        }

        Bundle student = new Bundle();
        student.putString(EXTRA_NAME, name);
        student.putString(EXTRA_ID, studentId);

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtras(student);

        binding.btnStart.setEnabled(false);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        binding.btnStart.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}