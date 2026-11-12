package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject_laravelsem5.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String STATE_NAME = "name";
    private static final String STATE_ID = "student_id";

    public static final String EXTRA_NAME = "com.example.miniproject_laravelsem5.NAME";
    public static final String EXTRA_ID = "com.example.miniproject_laravelsem5.ID";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState != null) {
            binding.editName.setText(savedInstanceState.getString(STATE_NAME));
            binding.editStudentId.setText(savedInstanceState.getString(STATE_ID));
        }
        clearErrorOnEdit(binding.editNameField);
        clearErrorOnEdit(binding.editStudentIdField);
        binding.editName.setNextFocusForwardId(R.id.editStudentId);
        binding.btnStart.setOnClickListener(view -> validateAndStart());
        binding.editStudentId.setOnEditorActionListener((view, action, event) -> {
            if (action != EditorInfo.IME_ACTION_DONE) {
                return false;
            }
            validateAndStart();
            return true;
        });
    }

    private void validateAndStart() {
        if (!binding.btnStart.isEnabled()) {
            return;
        }
        InputMethodManager keyboard = getSystemService(InputMethodManager.class);
        keyboard.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        String name = binding.editName.getText().toString().trim();
        String studentId = binding.editStudentId.getText().toString().trim();
        binding.editNameField.setError(null);
        binding.editStudentIdField.setError(null);

        if (name.length() < 2 || !name.matches("\\p{L}[\\p{L}\\p{M} .'’-]*")) {
            showError(binding.editNameField, name.isEmpty() ? R.string.error_empty_name : R.string.error_invalid_name);
            return;
        }
        if (!studentId.matches("[A-Za-z0-9_-]{4,20}")) {
            showError(binding.editStudentIdField, studentId.isEmpty() ? R.string.error_empty_id : R.string.error_invalid_id);
            return;
        }

        Bundle student = new Bundle();
        student.putString(EXTRA_NAME, name);
        student.putString(EXTRA_ID, studentId);
        binding.btnStart.setEnabled(false);
        startActivity(new Intent(this, QuizActivity.class).putExtras(student));
    }

    private void clearErrorOnEdit(TextInputLayout field) {
        field.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable text) {
                field.setError(null);
            }
        });
    }

    private void showError(TextInputLayout field, int message) {
        field.setError(getString(message));
        field.getEditText().requestFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_NAME, binding.editName.getText().toString());
        outState.putString(STATE_ID, binding.editStudentId.getText().toString());
        super.onSaveInstanceState(outState);
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
        ScreenTheme.updateSystemBars(this);
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
