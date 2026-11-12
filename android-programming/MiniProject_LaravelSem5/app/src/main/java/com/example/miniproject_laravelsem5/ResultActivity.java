package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject_laravelsem5.databinding.ActivityResultBinding;
import com.example.miniproject_laravelsem5.databinding.ItemResultBinding;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";

    public static final String EXTRA_QUESTIONS =
            "com.example.miniproject_laravelsem5.QUESTIONS";

    private ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String studentName = getIntent().getStringExtra(MainActivity.EXTRA_NAME);
        String studentId = getIntent().getStringExtra(MainActivity.EXTRA_ID);
        Serializable data = getIntent().getSerializableExtra(EXTRA_QUESTIONS);

        if (studentName == null || studentId == null
                || !(data instanceof ArrayList) || ((ArrayList<?>) data).isEmpty()) {
            Toast.makeText(this, R.string.result_no_data, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<TriviaQuestion> questions = (ArrayList<TriviaQuestion>) data;

        binding.setStudentName(studentName);
        binding.txtStudentId.setText(getString(R.string.result_student_id, studentId));

        showResults(questions);
        binding.btnRestart.setOnClickListener(view -> restartQuiz(studentName, studentId));
    }

    private void showResults(ArrayList<TriviaQuestion> questions) {
        int total = questions.size();
        int correct = TriviaQuestion.countCorrect(questions);
        int percentage = Math.round(correct * 100f / total);

        binding.txtPercentage.setText(getString(R.string.result_percentage_format, percentage));
        binding.txtCorrectCount.setText(getString(R.string.result_correct_count, correct));
        binding.txtIncorrectCount.setText(getString(R.string.result_incorrect_count, total - correct));

        for (int i = 0; i < total; i++) {
            TriviaQuestion question = questions.get(i);
            ItemResultBinding item = ItemResultBinding.inflate(
                    getLayoutInflater(), binding.breakdownContainer, false);

            item.txtItemNumber.setText(getString(R.string.result_question_number, i + 1));
            item.txtItemQuestion.setText(question.getQuestionText());
            item.txtYourAnswer.setText(getString(
                    R.string.result_your_answer, question.getSelectedAnswer()));
            item.txtCorrectAnswer.setText(getString(
                    R.string.result_correct_answer, question.getCorrectAnswer()));
            item.txtItemStatus.setText(question.isCorrect()
                    ? R.string.result_status_correct : R.string.result_status_incorrect);

            binding.breakdownContainer.addView(item.getRoot());
        }
    }

    private void restartQuiz(String studentName, String studentId) {
        Bundle student = new Bundle();
        student.putString(MainActivity.EXTRA_NAME, studentName);
        student.putString(MainActivity.EXTRA_ID, studentId);

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtras(student);

        binding.btnRestart.setEnabled(false);
        startActivity(intent);
        finish();
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
    }
}