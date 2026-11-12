package com.example.miniproject_laravelsem5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TriviaQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String questionText;

    private final ArrayList<String> choices;
    private final int correctIndex;

    private int selectedIndex = -1;

    public TriviaQuestion(
            String questionText,
            ArrayList<String> choices,
            int correctIndex
    ) {

        this.questionText = questionText;

        this.choices = new ArrayList<>(choices);
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public boolean isAnswered() {
        return selectedIndex >= 0;
    }

    public boolean isCorrect() {
        return selectedIndex == correctIndex;
    }

    static int countCorrect(List<TriviaQuestion> questions) {
        int correct = 0;
        for (TriviaQuestion question : questions) {
            if (question.isCorrect()) {
                correct++;
            }
        }
        return correct;
    }

    public String getCorrectAnswer() {
        return choices.get(correctIndex);
    }

    public String getSelectedAnswer() {

        if (!isAnswered()) {
            return "";
        }

        return choices.get(selectedIndex);
    }
}
