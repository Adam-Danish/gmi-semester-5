package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.miniproject_laravelsem5.databinding.ActivityQuizBinding;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String STATE_QUESTIONS = "saved_questions";
    private static final String STATE_INDEX = "saved_index";

    private ActivityQuizBinding binding;
    private Button[] answerButtons;
    private ArrayList<TriviaQuestion> questions = new ArrayList<>();
    private int currentIndex;
    private String studentName;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        studentName = getIntent().getStringExtra(MainActivity.EXTRA_NAME);
        studentId = getIntent().getStringExtra(MainActivity.EXTRA_ID);

        if (studentName == null || studentId == null) {
            Toast.makeText(this, R.string.missing_student_data, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        answerButtons = new Button[]{
                binding.btnChoice0,
                binding.btnChoice1,
                binding.btnChoice2,
                binding.btnChoice3
        };

        for (int i = 0; i < answerButtons.length; i++) {
            int answerIndex = i;
            answerButtons[i].setOnClickListener(view -> checkAnswer(answerIndex));
        }

        binding.btnNext.setOnClickListener(view -> nextQuestion());

        if (savedInstanceState != null) {
            @SuppressWarnings("unchecked")
            ArrayList<TriviaQuestion> savedQuestions =
                    (ArrayList<TriviaQuestion>) savedInstanceState.getSerializable(STATE_QUESTIONS);

            if (savedQuestions != null && !savedQuestions.isEmpty()) {
                questions = savedQuestions;
                currentIndex = savedInstanceState.getInt(STATE_INDEX);
                showQuestion();
                return;
            }
        }

        QuestionLoader loader = new ViewModelProvider(this).get(QuestionLoader.class);

        binding.btnRetry.setOnClickListener(view -> loader.load());
        loader.error().observe(this, this::showLoadingOrError);
        loader.questions().observe(this, loadedQuestions -> {
            questions = loadedQuestions;
            showQuestion();
        });

        loader.loadIfNeeded();
    }

    private void showLoadingOrError(int messageId) {
        if (!questions.isEmpty()) {
            return;
        }

        binding.txtLoadStatus.setText(messageId == 0 ? R.string.quiz_loading : messageId);
        binding.btnRetry.setVisibility(messageId == 0 ? View.GONE : View.VISIBLE);
    }

    private void showQuestion() {
        TriviaQuestion question = questions.get(currentIndex);
        boolean answered = question.isAnswered();

        binding.txtLoadStatus.setVisibility(View.GONE);
        binding.btnRetry.setVisibility(View.GONE);
        binding.questionPanel.setVisibility(View.VISIBLE);

        binding.setQuestionText(question.getQuestionText());
        binding.txtProgress.setText(getString(
                R.string.quiz_question_progress, currentIndex + 1, questions.size()));
        binding.txtCurrentScore.setText(getString(
                R.string.quiz_current_score, TriviaQuestion.countCorrect(questions), questions.size()));

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(question.getChoices().get(i));
            answerButtons[i].setEnabled(!answered);
        }

        binding.btnNext.setEnabled(answered);
        binding.btnNext.setText(currentIndex == questions.size() - 1
                ? R.string.button_finish_quiz : R.string.button_next_question);

        binding.feedbackPanel.setVisibility(answered ? View.VISIBLE : View.GONE);

        if (answered) {
            showFeedback(question);
        }
    }

    private void checkAnswer(int answerIndex) {
        TriviaQuestion question = questions.get(currentIndex);

        if (question.isAnswered()) {
            return;
        }

        question.setSelectedIndex(answerIndex);
        showQuestion();

        int message = question.isCorrect() ? R.string.toast_correct : R.string.toast_incorrect;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showFeedback(TriviaQuestion question) {
        if (question.isCorrect()) {
            binding.imgFeedback.setImageResource(R.drawable.ic_correct);
            binding.imgFeedback.setContentDescription(getString(R.string.icon_correct));
            binding.txtFeedback.setText(R.string.feedback_correct);
        } else {
            binding.imgFeedback.setImageResource(R.drawable.ic_incorrect);
            binding.imgFeedback.setContentDescription(getString(R.string.icon_incorrect));
            binding.txtFeedback.setText(getString(
                    R.string.feedback_incorrect, question.getCorrectAnswer()));
        }
    }

    private void nextQuestion() {
        if (questions.isEmpty() || !questions.get(currentIndex).isAnswered()) {
            return;
        }

        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion();
            binding.screenScroll.scrollTo(0, 0);
            return;
        }

        Bundle result = new Bundle();
        result.putString(MainActivity.EXTRA_NAME, studentName);
        result.putString(MainActivity.EXTRA_ID, studentId);
        result.putSerializable(ResultActivity.EXTRA_QUESTIONS, questions);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtras(result);

        binding.btnNext.setEnabled(false);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_QUESTIONS, questions);
        outState.putInt(STATE_INDEX, currentIndex);
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