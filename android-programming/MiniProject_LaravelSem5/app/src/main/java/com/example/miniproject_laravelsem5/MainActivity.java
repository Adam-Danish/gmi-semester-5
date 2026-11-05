package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject_laravelsem5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String EXTRA_NAME =
            "com.example.miniproject_laravelsem5.NAME";

    public static final String EXTRA_ID =
            "com.example.miniproject_laravelsem5.ID";

    public static final String EXTRA_PROGRAMME =
            "com.example.miniproject_laravelsem5.PROGRAMME";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityMainBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(
                view -> validateAndStart()
        );
    }

    private void validateAndStart() {

        String name = binding.editName.getText()
                .toString().trim();

        String studentId = binding.editStudentId.getText()
                .toString().trim();

        String programme = binding.editProgramme.getText()
                .toString().trim();

        if (name.length() < 2) {

            binding.editName.setError(
                    getString(R.string.error_name)
            );

            binding.editName.requestFocus();
            return;
        }

        if (!studentId.matches("[A-Za-z0-9_-]{4,20}")) {

            binding.editStudentId.setError(
                    getString(R.string.error_student_id)
            );

            binding.editStudentId.requestFocus();
            return;
        }

        if (programme.isEmpty()) {

            binding.editProgramme.setError(
                    getString(R.string.error_programme)
            );

            binding.editProgramme.requestFocus();
            return;
        }

        Toast.makeText(
                this,
                R.string.registration_success,
                Toast.LENGTH_SHORT
        ).show();

        Bundle studentData = new Bundle();

        studentData.putString(EXTRA_NAME, name);
        studentData.putString(EXTRA_ID, studentId);
        studentData.putString(EXTRA_PROGRAMME, programme);

        Intent intent = new Intent(
                MainActivity.this,
                QuizActivity.class
        );

        intent.putExtras(studentData);

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
        binding = null;
    }
}
