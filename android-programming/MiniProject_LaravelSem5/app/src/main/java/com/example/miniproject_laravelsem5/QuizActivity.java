package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.miniproject_laravelsem5.databinding.ActivityQuizBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String STATE_QUESTIONS = "saved_questions";
    private static final String STATE_INDEX = "saved_index";

    private ActivityQuizBinding binding;
    private QuestionLoader loader;
    private ArrayList<TriviaQuestion> questions = new ArrayList<>();
    private MaterialButton[] choiceButtons;
    private Snackbar snackbar;
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

        binding.setStudentName(studentName);
        choiceButtons = new MaterialButton[]{
                binding.btnChoice0, binding.btnChoice1, binding.btnChoice2, binding.btnChoice3
        };
        for (int i = 0; i < choiceButtons.length; i++) {
            int selected = i;
            choiceButtons[i].setOnClickListener(view -> submitAnswer(selected));
        }
        binding.btnNext.setOnClickListener(view -> next());
        binding.btnRetry.setOnClickListener(view -> loader.load());

        if (savedInstanceState != null) {
            Serializable saved = savedInstanceState.getSerializable(STATE_QUESTIONS);
            if (saved instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<TriviaQuestion> restored = (ArrayList<TriviaQuestion>) saved;
                questions = restored;
            }
            currentIndex = savedInstanceState.getInt(STATE_INDEX);
        }
        if (!questions.isEmpty()) {
            currentIndex = Math.max(0, Math.min(currentIndex, questions.size() - 1));
            displayQuestion();
            return;
        }

        loader = new ViewModelProvider(this).get(QuestionLoader.class);
        loader.error().observe(this, this::displayLoadState);
        loader.questions().observe(this, loaded -> {
            questions = loaded;
            displayQuestion();
        });
        loader.loadIfNeeded();
    }

    private void displayLoadState(int messageId) {
        if (!questions.isEmpty()) {
            return;
        }
        boolean loading = messageId == 0;
        binding.loadingPanel.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.errorPanel.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (!loading) {
            binding.txtError.setText(messageId);
        }
    }

    private void displayQuestion() {
        TriviaQuestion question = questions.get(currentIndex);
        boolean answered = question.isAnswered();

        binding.loadingPanel.setVisibility(View.GONE);
        binding.errorPanel.setVisibility(View.GONE);
        binding.questionPanel.setVisibility(View.VISIBLE);
        binding.setQuestionText(question.getQuestionText());
        binding.txtProgress.setText(getString(R.string.quiz_question_progress, currentIndex + 1, questions.size()));
        binding.txtCurrentScore.setText(getString(
                R.string.quiz_current_score, TriviaQuestion.countCorrect(questions), questions.size()));
        binding.quizProgress.setProgress((currentIndex + 1) * 100 / questions.size());

        for (int i = 0; i < choiceButtons.length; i++) {
            MaterialButton button = choiceButtons[i];
            boolean selected = i == question.getSelectedIndex();
            boolean correct = answered && i == question.getCorrectIndex();
            boolean incorrect = selected && !correct;
            int background = correct ? R.color.correct_surface : incorrect ? R.color.incorrect_surface : R.color.surface;
            int foreground = correct ? R.color.success : incorrect ? R.color.error : R.color.on_surface;
            int border = correct || incorrect ? foreground : R.color.outline;
            button.setText(question.getChoices().get(i));
            button.setSelected(selected);
            button.setClickable(!answered);
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(background)));
            button.setStrokeColor(ColorStateList.valueOf(getColor(border)));
            button.setTextColor(getColor(foreground));
            int status = correct ? R.string.result_status_correct : R.string.result_status_incorrect;
            ViewCompat.setStateDescription(button, selected
                    ? getString(R.string.answer_selected_status, getString(status))
                    : correct ? getString(status) : null);
        }

        binding.btnNext.setEnabled(answered);
        binding.btnNext.setText(currentIndex == questions.size() - 1
                ? R.string.button_finish_quiz : R.string.button_next_question);
        binding.feedbackPanel.setVisibility(answered ? View.VISIBLE : View.GONE);
        if (answered) {
            boolean correct = question.isCorrect();
            binding.imgFeedback.setImageResource(correct ? R.drawable.ic_correct : R.drawable.ic_incorrect);
            binding.imgFeedback.setContentDescription(getString(correct ? R.string.icon_correct : R.string.icon_incorrect));
            binding.txtFeedback.setText(correct ? getString(R.string.feedback_correct)
                    : getString(R.string.feedback_incorrect, question.getCorrectAnswer()));
            binding.txtFeedback.setTextColor(getColor(correct ? R.color.success : R.color.error));
        }
    }

    private void submitAnswer(int selectedIndex) {
        TriviaQuestion question = questions.get(currentIndex);
        if (question.isAnswered()) {
            return;
        }
        question.setSelectedIndex(selectedIndex);
        Log.d(TAG, "Question " + (currentIndex + 1) + " correct=" + question.isCorrect());
        displayQuestion();
        binding.feedbackPanel.setAlpha(0f);
        binding.feedbackPanel.animate().alpha(1f).setDuration(180).start();
        binding.screenScroll.post(() -> {
            if (!isFinishing() && !isDestroyed()) {
                binding.screenScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
        snackbar = Snackbar.make(binding.getRoot(), question.isCorrect()
                ? R.string.result_status_correct : R.string.result_status_incorrect, Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(binding.btnNext);
        snackbar.show();
    }

    private void next() {
        if (questions.isEmpty() || !questions.get(currentIndex).isAnswered()) {
            return;
        }
        if (snackbar != null) {
            snackbar.dismiss();
        }
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            displayQuestion();
            binding.screenScroll.scrollTo(0, 0);
            return;
        }
        binding.btnNext.setEnabled(false);
        Bundle result = new Bundle();
        result.putString(MainActivity.EXTRA_NAME, studentName);
        result.putString(MainActivity.EXTRA_ID, studentId);
        result.putSerializable(ResultActivity.EXTRA_QUESTIONS, questions);
        startActivity(new Intent(this, ResultActivity.class).putExtras(result));
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
        if (snackbar != null) {
            snackbar.dismiss();
        }
        if (isFinishing() && loader != null) {
            loader.cancel();
        }
    }

}
