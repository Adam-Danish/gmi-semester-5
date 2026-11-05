package com.example.miniproject_laravelsem5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.miniproject_laravelsem5.databinding.ActivityQuizBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private static final String API_URL =
            "https://opentdb.com/api.php?amount=10&type=multiple";

    private static final String STATE_QUESTIONS =
            "saved_questions";

    private static final String STATE_INDEX =
            "saved_index";

    private ActivityQuizBinding binding;

    private RequestQueue requestQueue;

    private ArrayList<TriviaQuestion> questions =
            new ArrayList<>();

    private Button[] choiceButtons;

    private int currentIndex = 0;

    private String studentName;
    private String studentId;
    private String programme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityQuizBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        studentName = getIntent().getStringExtra(
                MainActivity.EXTRA_NAME
        );

        studentId = getIntent().getStringExtra(
                MainActivity.EXTRA_ID
        );

        programme = getIntent().getStringExtra(
                MainActivity.EXTRA_PROGRAMME
        );

        if (studentName == null ||
                studentId == null ||
                programme == null) {

            Toast.makeText(
                    this,
                    R.string.missing_student_data,
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        binding.setStudentName(studentName);

        choiceButtons = new Button[]{
                binding.btnChoice0,
                binding.btnChoice1,
                binding.btnChoice2,
                binding.btnChoice3
        };

        for (int i = 0; i < choiceButtons.length; i++) {

            final int selectedIndex = i;

            choiceButtons[i].setOnClickListener(
                    view -> submitAnswer(selectedIndex)
            );
        }

        binding.btnNext.setOnClickListener(view -> {

            if (questions.isEmpty()) {
                return;
            }

            TriviaQuestion question =
                    questions.get(currentIndex);

            if (!question.isAnswered()) {
                return;
            }

            if (currentIndex < questions.size() - 1) {

                currentIndex++;
                displayQuestion();

            } else {

                openResultActivity();
            }
        });

        binding.btnRetry.setOnClickListener(
                view -> fetchQuestions()
        );

        requestQueue = Volley.newRequestQueue(
                getApplicationContext()
        );

        if (savedInstanceState != null) {

            Serializable saved =
                    savedInstanceState.getSerializable(
                            STATE_QUESTIONS
                    );

            if (saved instanceof ArrayList) {

                @SuppressWarnings("unchecked")
                ArrayList<TriviaQuestion> restored =
                        (ArrayList<TriviaQuestion>) saved;

                questions = restored;
            }

            currentIndex = savedInstanceState.getInt(
                    STATE_INDEX,
                    0
            );
        }

        if (!questions.isEmpty()) {

            currentIndex = Math.max(
                    0,
                    Math.min(
                            currentIndex,
                            questions.size() - 1
                    )
            );

            displayQuestion();

        } else {

            fetchQuestions();
        }
    }

    private void fetchQuestions() {

        requestQueue.cancelAll(TAG);

        questions.clear();
        currentIndex = 0;

        binding.progressLoading.setVisibility(View.VISIBLE);
        binding.txtLoading.setVisibility(View.VISIBLE);

        binding.questionPanel.setVisibility(View.GONE);
        binding.errorPanel.setVisibility(View.GONE);

        binding.btnRetry.setEnabled(false);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,

                response -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    try {

                        int responseCode =
                                response.getInt("response_code");

                        if (responseCode == 5) {
                            showError(R.string.error_rate_limit);
                            return;
                        }

                        if (responseCode != 0) {
                            showError(R.string.error_api_response);
                            return;
                        }

                        JSONArray results =
                                response.getJSONArray("results");

                        if (results.length() != 10) {
                            showError(R.string.error_question_count);
                            return;
                        }

                        ArrayList<TriviaQuestion> loaded =
                                new ArrayList<>();

                        for (int i = 0; i < results.length(); i++) {

                            JSONObject item =
                                    results.getJSONObject(i);

                            String questionText = decodeHtml(
                                    item.getString("question")
                            );

                            String category = decodeHtml(
                                    item.getString("category")
                            );

                            String difficulty = decodeHtml(
                                    item.getString("difficulty")
                            );

                            String correctAnswer = decodeHtml(
                                    item.getString("correct_answer")
                            );

                            JSONArray incorrectAnswers =
                                    item.getJSONArray(
                                            "incorrect_answers"
                                    );

                            if (incorrectAnswers.length() != 3) {
                                throw new JSONException(
                                        "Expected 3 incorrect answers"
                                );
                            }

                            ArrayList<String> choices =
                                    new ArrayList<>();

                            for (int j = 0;
                                 j < incorrectAnswers.length();
                                 j++) {

                                choices.add(
                                        decodeHtml(
                                                incorrectAnswers.getString(j)
                                        )
                                );
                            }

                            choices.add(correctAnswer);

                            Collections.shuffle(choices);

                            int correctIndex =
                                    choices.indexOf(correctAnswer);

                            TriviaQuestion question =
                                    new TriviaQuestion(
                                            questionText,
                                            category,
                                            difficulty,
                                            choices,
                                            correctIndex
                                    );

                            loaded.add(question);
                        }

                        questions = loaded;

                        binding.progressLoading.setVisibility(
                                View.GONE
                        );

                        binding.txtLoading.setVisibility(
                                View.GONE
                        );

                        displayQuestion();

                    } catch (JSONException exception) {

                        Log.e(
                                TAG,
                                "Failed to parse trivia response",
                                exception
                        );

                        showError(R.string.error_json);
                    }
                },

                error -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Log.e(
                            TAG,
                            "Volley request failed",
                            error
                    );

                    if (error.networkResponse != null &&
                            error.networkResponse.statusCode == 429) {

                        showError(R.string.error_rate_limit);

                    } else {

                        showError(R.string.error_network);
                    }
                }
        );

        request.setTag(TAG);

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        12000,
                        0,
                        1.0f
                )
        );

        requestQueue.add(request);
    }

    private String decodeHtml(String text) {

        return Html.fromHtml(
                text,
                Html.FROM_HTML_MODE_LEGACY
        ).toString().trim();
    }

    private void showError(int messageId) {

        binding.progressLoading.setVisibility(View.GONE);
        binding.txtLoading.setVisibility(View.GONE);

        binding.questionPanel.setVisibility(View.GONE);
        binding.errorPanel.setVisibility(View.VISIBLE);

        binding.txtError.setText(messageId);
        binding.btnRetry.setEnabled(true);
    }

    private void displayQuestion() {

        if (questions.isEmpty()) {
            return;
        }

        binding.progressLoading.setVisibility(View.GONE);
        binding.txtLoading.setVisibility(View.GONE);
        binding.errorPanel.setVisibility(View.GONE);

        binding.questionPanel.setVisibility(View.VISIBLE);

        TriviaQuestion question =
                questions.get(currentIndex);

        binding.setQuestionText(
                question.getQuestionText()
        );

        binding.txtProgress.setText(
                getString(
                        R.string.question_position,
                        currentIndex + 1,
                        questions.size()
                )
        );

        binding.quizProgress.setMax(100);

        binding.quizProgress.setProgress(
                (currentIndex + 1) * 100 /
                        questions.size()
        );

        binding.txtCategory.setText(
                getString(
                        R.string.category_difficulty,
                        question.getCategory(),
                        question.getDifficulty()
                )
        );

        for (int i = 0; i < choiceButtons.length; i++) {

            Button button = choiceButtons[i];

            button.setText(
                    question.getChoices().get(i)
            );

            button.setEnabled(true);
            button.setClickable(!question.isAnswered());

            button.setBackgroundTintList(null);

            button.setBackgroundResource(
                    R.drawable.bg_option
            );

            button.setTextColor(
                    getColor(R.color.ink)
            );
        }

        binding.imgFeedback.setVisibility(View.GONE);
        binding.txtFeedback.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.GONE);

        if (question.isAnswered()) {

            int correctIndex =
                    question.getCorrectIndex();

            int selectedIndex =
                    question.getSelectedIndex();

            choiceButtons[correctIndex].setBackgroundResource(
                    R.drawable.bg_option_correct
            );

            choiceButtons[correctIndex].setTextColor(
                    getColor(R.color.success)
            );

            if (!question.isCorrect()) {

                choiceButtons[selectedIndex].setBackgroundResource(
                        R.drawable.bg_option_incorrect
                );

                choiceButtons[selectedIndex].setTextColor(
                        getColor(R.color.error)
                );
            }

            if (question.isCorrect()) {

                binding.imgFeedback.setImageResource(
                        R.drawable.ic_correct
                );

                binding.txtFeedback.setText(
                        R.string.feedback_correct
                );

                binding.txtFeedback.setTextColor(
                        getColor(R.color.success)
                );

            } else {

                binding.imgFeedback.setImageResource(
                        R.drawable.ic_incorrect
                );

                binding.txtFeedback.setText(
                        getString(
                                R.string.feedback_incorrect,
                                question.getCorrectAnswer()
                        )
                );

                binding.txtFeedback.setTextColor(
                        getColor(R.color.error)
                );
            }

            binding.imgFeedback.setVisibility(
                    View.VISIBLE
            );

            binding.txtFeedback.setVisibility(
                    View.VISIBLE
            );

            binding.btnNext.setVisibility(
                    View.VISIBLE
            );

            if (currentIndex == questions.size() - 1) {

                binding.btnNext.setText(
                        R.string.btn_view_results
                );

            } else {

                binding.btnNext.setText(
                        R.string.btn_next
                );
            }
        }
    }

    private void submitAnswer(int selectedIndex) {

        if (questions.isEmpty()) {
            return;
        }

        TriviaQuestion question =
                questions.get(currentIndex);

        if (question.isAnswered()) {
            return;
        }

        question.setSelectedIndex(selectedIndex);

        Log.d(
                TAG,
                "Question " + (currentIndex + 1) +
                        " correct=" + question.isCorrect()
        );

        displayQuestion();

        Toast.makeText(
                this,
                question.isCorrect()
                        ? R.string.toast_correct
                        : R.string.toast_incorrect,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void openResultActivity() {

        Bundle resultData = new Bundle();

        resultData.putString(
                MainActivity.EXTRA_NAME,
                studentName
        );

        resultData.putString(
                MainActivity.EXTRA_ID,
                studentId
        );

        resultData.putString(
                MainActivity.EXTRA_PROGRAMME,
                programme
        );

        resultData.putSerializable(
                ResultActivity.EXTRA_QUESTIONS,
                questions
        );

        Intent intent = new Intent(
                QuizActivity.this,
                ResultActivity.class
        );

        intent.putExtras(resultData);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(
                STATE_QUESTIONS,
                questions
        );

        outState.putInt(
                STATE_INDEX,
                currentIndex
        );

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

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }

        binding = null;
    }
}
