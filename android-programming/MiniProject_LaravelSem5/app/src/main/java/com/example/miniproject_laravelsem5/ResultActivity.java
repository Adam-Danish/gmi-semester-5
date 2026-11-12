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
    public static final String EXTRA_QUESTIONS = "com.example.miniproject_laravelsem5.QUESTIONS";

    private ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra(MainActivity.EXTRA_NAME);
        String id = getIntent().getStringExtra(MainActivity.EXTRA_ID);
        Serializable received = getIntent().getSerializableExtra(EXTRA_QUESTIONS);
        if (name == null || id == null || !(received instanceof ArrayList) || ((ArrayList<?>) received).isEmpty()) {
            Toast.makeText(this, R.string.result_no_data, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<TriviaQuestion> questions = (ArrayList<TriviaQuestion>) received;
        binding.setStudentName(name);
        binding.txtStudentId.setText(getString(R.string.result_student_id, id));
        displayResults(questions);
        binding.btnRestart.setOnClickListener(view -> {
            binding.btnRestart.setEnabled(false);
            Bundle student = new Bundle();
            student.putString(MainActivity.EXTRA_NAME, name);
            student.putString(MainActivity.EXTRA_ID, id);
            startActivity(new Intent(this, QuizActivity.class).putExtras(student));
            finish();
        });
        binding.btnHome.setOnClickListener(view -> {
            Intent home = new Intent(this, MainActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
            finish();
        });
    }

    private void displayResults(ArrayList<TriviaQuestion> questions) {
        int total = questions.size();
        int correct = TriviaQuestion.countCorrect(questions);
        int percentage = Math.round(correct * 100.0f / total);
        int percentageColor = percentage >= 80 ? R.color.success
                : percentage >= 50 ? R.color.on_surface : R.color.error;
        binding.txtPercentage.setText(getString(R.string.result_percentage_format, percentage));
        binding.txtPercentage.setTextColor(getColor(percentageColor));
        binding.txtScore.setText(getString(R.string.result_score_format, correct, total));
        binding.txtCorrectCount.setText(getString(R.string.result_correct_count, correct));
        binding.txtIncorrectCount.setText(getString(R.string.result_incorrect_count, total - correct));
        binding.resultProgress.setProgress(percentage);
        Log.d(TAG, "Correct=" + correct + " Incorrect=" + (total - correct) + " Percentage=" + percentage);

        for (int i = 0; i < total; i++) {
            TriviaQuestion question = questions.get(i);
            boolean isCorrect = question.isCorrect();
            ItemResultBinding row = ItemResultBinding.inflate(getLayoutInflater(), binding.breakdownContainer, false);
            row.txtItemNumber.setText(getString(R.string.result_question_number, i + 1));
            row.txtItemQuestion.setText(question.getQuestionText());
            row.txtYourAnswer.setText(getString(R.string.result_your_answer, question.getSelectedAnswer()));
            row.txtCorrectAnswer.setText(getString(R.string.result_correct_answer, question.getCorrectAnswer()));
            row.txtItemStatus.setText(isCorrect ? R.string.result_status_correct : R.string.result_status_incorrect);
            row.txtItemStatus.setTextColor(getColor(isCorrect ? R.color.success : R.color.error));
            row.imgItemStatus.setImageResource(isCorrect ? R.drawable.ic_correct : R.drawable.ic_incorrect);
            row.imgItemStatus.setContentDescription(getString(isCorrect ? R.string.icon_correct : R.string.icon_incorrect));
            binding.breakdownContainer.addView(row.getRoot());
        }
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
