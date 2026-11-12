package com.example.miniproject_laravelsem5;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.exammarkcalculator", appContext.getPackageName());
    }

    @Test
    public void parseQuestionDecodesEntitiesAndRetainsTheCorrectChoice() throws Exception {
        JSONObject json = new JSONObject()
                .put("question", "What&#039;s &quot;A&quot;?")
                .put("correct_answer", "A &amp; B")
                .put("incorrect_answers", new JSONArray().put("C").put("D").put("E"));

        TriviaQuestion question = QuestionLoader.parseQuestion(json);
        assertEquals("What's \"A\"?", question.getQuestionText());
        assertEquals("A & B", question.getCorrectAnswer());
        assertEquals(4, question.getChoices().size());
    }

    @Test
    public void mapsVolleyFailuresToSpecificMessages() {
        assertEquals(R.string.error_timeout, QuestionLoader.errorMessage(new TimeoutError()));
        assertEquals(R.string.error_no_connection, QuestionLoader.errorMessage(new NoConnectionError()));
        assertEquals(R.string.error_json, QuestionLoader.errorMessage(new ParseError()));
        NetworkResponse rateLimited = new NetworkResponse(429, new byte[0], false, 0L, null);
        assertEquals(R.string.error_rate_limit, QuestionLoader.errorMessage(new VolleyError(rateLimited)));
    }

    @Test
    public void activitiesResumeAfterStoppingAndResultsAllowBack() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent main = new Intent(context, MainActivity.class);
        Intent quiz = new Intent(context, QuizActivity.class)
                .putExtra(MainActivity.EXTRA_NAME, "Demo Student")
                .putExtra(MainActivity.EXTRA_ID, "TEST2026");

        ArrayList<TriviaQuestion> answers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            TriviaQuestion question = new TriviaQuestion("Question " + i,
                    new ArrayList<>(Arrays.asList("A", "B", "C", "D")), 0);
            question.setSelectedIndex(i <= 6 ? 0 : 1);
            answers.add(question);
        }
        Intent result = new Intent(context, ResultActivity.class)
                .putExtra(MainActivity.EXTRA_NAME, "Demo Student")
                .putExtra(MainActivity.EXTRA_ID, "TEST2026")
                .putExtra(ResultActivity.EXTRA_QUESTIONS, answers);

        for (Intent intent : Arrays.asList(main, quiz, result)) {
            try (ActivityScenario<?> scenario = ActivityScenario.launch(intent)) {
                scenario.moveToState(Lifecycle.State.CREATED);
                scenario.moveToState(Lifecycle.State.RESUMED);
                assertEquals(Lifecycle.State.RESUMED, scenario.getState());
                if (intent == result) {
                    scenario.onActivity(activity ->
                            ((ResultActivity) activity).getOnBackPressedDispatcher().onBackPressed());
                    long deadline = SystemClock.uptimeMillis() + 4000;
                    while (scenario.getState() != Lifecycle.State.DESTROYED
                            && SystemClock.uptimeMillis() < deadline) {
                        SystemClock.sleep(50);
                    }
                    assertEquals(Lifecycle.State.DESTROYED, scenario.getState());
                }
            }
        }
    }

    @Test
    public void resultsCloseWhenTheAssessmentDataIsMissing() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        try (ActivityScenario<ResultActivity> scenario =
                     ActivityScenario.launch(new Intent(context, ResultActivity.class))) {
            assertEquals(Lifecycle.State.DESTROYED, scenario.getState());
        }
    }
}
