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

    private ArrayList<TriviaQuestion> questions =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityResultBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        String studentName = getIntent().getStringExtra(
                MainActivity.EXTRA_NAME
        );

        String studentId = getIntent().getStringExtra(
                MainActivity.EXTRA_ID
        );

        String programme = getIntent().getStringExtra(
                MainActivity.EXTRA_PROGRAMME
        );

        Serializable received =
                getIntent().getSerializableExtra(
                        EXTRA_QUESTIONS
                );

        if (!(received instanceof ArrayList)) {

            Toast.makeText(
                    this,
                    R.string.error_no_result,
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<TriviaQuestion> receivedQuestions =
                (ArrayList<TriviaQuestion>) received;

        questions = receivedQuestions;

        if (questions.isEmpty()) {

            Toast.makeText(
                    this,
                    R.string.error_no_result,
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        binding.setStudentName(studentName);

        binding.txtStudentId.setText(
                getString(
                        R.string.student_id_value,
                        studentId
                )
        );

        binding.txtProgramme.setText(
                getString(
                        R.string.programme_value,
                        programme
                )
        );

        calculateAndDisplayResults();
        createQuestionBreakdown();

        binding.btnRestart.setOnClickListener(
                view -> restartAssessment()
        );
    }

    private void calculateAndDisplayResults() {

        int correct = 0;
        int total = questions.size();

        for (TriviaQuestion question : questions) {

            if (question.isCorrect()) {
                correct++;
            }
        }

        int incorrect = total - correct;

        int percentage = Math.round(
                correct * 100.0f / total
        );

        binding.txtScore.setText(
                getString(
                        R.string.score_value,
                        correct,
                        total
                )
        );

        binding.txtPercentage.setText(
                getString(
                        R.string.percentage_value,
                        percentage
                )
        );

        binding.txtCorrectCount.setText(
                getString(
                        R.string.correct_count_value,
                        correct
                )
        );

        binding.txtIncorrectCount.setText(
                getString(
                        R.string.incorrect_count_value,
                        incorrect
                )
        );

        binding.resultProgress.setMax(100);
        binding.resultProgress.setProgress(percentage);

        if (percentage >= 80) {

            binding.txtPerformance.setText(
                    R.string.performance_excellent
            );

        } else if (percentage >= 60) {

            binding.txtPerformance.setText(
                    R.string.performance_good
            );

        } else if (percentage >= 40) {

            binding.txtPerformance.setText(
                    R.string.performance_average
            );

        } else {

            binding.txtPerformance.setText(
                    R.string.performance_improve
            );
        }

        Log.d(
                TAG,
                "Correct=" + correct +
                        " Incorrect=" + incorrect +
                        " Percentage=" + percentage
        );
    }

    private void createQuestionBreakdown() {

        binding.breakdownContainer.removeAllViews();

        for (int i = 0; i < questions.size(); i++) {

            TriviaQuestion question =
                    questions.get(i);

            ItemResultBinding item =
                    ItemResultBinding.inflate(
                            getLayoutInflater(),
                            binding.breakdownContainer,
                            false
                    );

            item.txtItemNumber.setText(
                    getString(
                            R.string.review_question_number,
                            i + 1
                    )
            );

            item.txtItemQuestion.setText(
                    question.getQuestionText()
            );

            item.txtYourAnswer.setText(
                    getString(
                            R.string.your_answer_value,
                            question.getSelectedAnswer()
                    )
            );

            item.txtCorrectAnswer.setText(
                    getString(
                            R.string.correct_answer_value,
                            question.getCorrectAnswer()
                    )
            );

            if (question.isCorrect()) {

                item.imgItemStatus.setImageResource(
                        R.drawable.ic_correct
                );

                item.txtItemStatus.setText(
                        R.string.status_correct
                );

                item.txtItemStatus.setTextColor(
                        getColor(R.color.success)
                );

            } else {

                item.imgItemStatus.setImageResource(
                        R.drawable.ic_incorrect
                );

                item.txtItemStatus.setText(
                        R.string.status_incorrect
                );

                item.txtItemStatus.setTextColor(
                        getColor(R.color.error)
                );
            }

            binding.breakdownContainer.addView(
                    item.getRoot()
            );
        }
    }

    private void restartAssessment() {

        Intent intent = new Intent(
                ResultActivity.this,
                MainActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

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
        binding = null;
    }
}
