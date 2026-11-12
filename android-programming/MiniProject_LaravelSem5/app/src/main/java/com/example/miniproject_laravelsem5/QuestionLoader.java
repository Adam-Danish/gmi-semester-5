package com.example.miniproject_laravelsem5;

import android.app.Application;
import android.text.Html;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class QuestionLoader extends AndroidViewModel {
    private static final String TAG = "QuestionLoader";
    private static final int QUESTION_COUNT = 10;
    private static final String API_URL =
            "https://opentdb.com/api.php?amount=" + QUESTION_COUNT + "&type=multiple";

    private final MutableLiveData<ArrayList<TriviaQuestion>> questions = new MutableLiveData<>();
    // Zero means the loading view should be displayed.
    private final MutableLiveData<Integer> error = new MutableLiveData<>(0);
    private RequestQueue queue;
    private Request<?> pendingRequest;

    public QuestionLoader(Application application) {
        super(application);
    }

    LiveData<ArrayList<TriviaQuestion>> questions() {
        return questions;
    }

    LiveData<Integer> error() {
        return error;
    }

    void loadIfNeeded() {
        if (questions.getValue() == null && error.getValue() == 0) {
            load();
        }
    }

    void load() {
        if (pendingRequest != null) {
            return;
        }
        error.setValue(0);
        if (queue == null) {
            queue = Volley.newRequestQueue(getApplication());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                this::receive, failure -> {
                    pendingRequest = null;
                    Log.w(TAG, "Trivia request failed", failure);
                    error.setValue(errorMessage(failure));
                });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(12000, 0, 1));
        pendingRequest = queue.add(request);
    }

    private void receive(JSONObject response) {
        pendingRequest = null;
        try {
            int responseCode = response.getInt("response_code");
            if (responseCode != 0) {
                error.setValue(responseCode == 5 ? R.string.error_rate_limit : R.string.error_api_response);
                return;
            }
            JSONArray results = response.getJSONArray("results");
            if (results.length() != QUESTION_COUNT) {
                error.setValue(R.string.error_question_count);
                return;
            }
            ArrayList<TriviaQuestion> loaded = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                loaded.add(parseQuestion(results.getJSONObject(i)));
            }
            questions.setValue(loaded);
        } catch (JSONException failure) {
            Log.w(TAG, "Invalid trivia response", failure);
            error.setValue(R.string.error_json);
        }
    }

    static TriviaQuestion parseQuestion(JSONObject item) throws JSONException {
        String question = decode(item.getString("question"));
        String correct = decode(item.getString("correct_answer"));
        JSONArray incorrect = item.getJSONArray("incorrect_answers");
        if (incorrect.length() != 3 || question.isEmpty() || correct.isEmpty()) {
            throw new JSONException("Missing question text or answer options");
        }
        ArrayList<String> choices = new ArrayList<>();
        choices.add(correct);
        for (int i = 0; i < incorrect.length(); i++) {
            choices.add(decode(incorrect.getString(i)));
        }
        if (choices.contains("") || new HashSet<>(choices).size() != 4) {
            throw new JSONException("Answer options must be nonempty and unique");
        }
        Collections.shuffle(choices);
        return new TriviaQuestion(question, choices, choices.indexOf(correct));
    }

    private static String decode(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString().trim();
    }

    static int errorMessage(VolleyError failure) {
        if (failure.networkResponse != null && failure.networkResponse.statusCode == 429) {
            return R.string.error_rate_limit;
        }
        if (failure instanceof TimeoutError) {
            return R.string.error_timeout;
        }
        if (failure instanceof NoConnectionError) {
            return R.string.error_no_connection;
        }
        if (failure instanceof ParseError) {
            return R.string.error_json;
        }
        return R.string.error_network;
    }

    void cancel() {
        if (pendingRequest != null) {
            pendingRequest.cancel();
            pendingRequest = null;
        }
        if (queue != null) {
            queue.stop();
            queue = null;
        }
    }

    @Override
    protected void onCleared() {
        cancel();
    }
}
