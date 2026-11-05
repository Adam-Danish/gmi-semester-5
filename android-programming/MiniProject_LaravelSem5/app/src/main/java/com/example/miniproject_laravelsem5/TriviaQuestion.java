package com.example.miniproject_laravelsem5;

import java.io.Serializable;
import java.util.ArrayList;

public class TriviaQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String questionText;
    private final String category;
    private final String difficulty;

    private final ArrayList<String> choices;
    private final int correctIndex;

    private int selectedIndex = -1;

    public TriviaQuestion(
            String questionText,
            String category,
            String difficulty,
            ArrayList<String> choices,
            int correctIndex
    ) {

        this.questionText = questionText;
        this.category = category;
        this.difficulty = difficulty;

        this.choices = new ArrayList<>(choices);
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
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
