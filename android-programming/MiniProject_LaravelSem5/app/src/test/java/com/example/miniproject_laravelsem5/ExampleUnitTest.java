package com.example.miniproject_laravelsem5;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ExampleUnitTest {
    @Test
    public void scoreCountsOnlyCorrectAnswers() {
        ArrayList<String> choices = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        TriviaQuestion correct = new TriviaQuestion("Question 1", choices, 2);
        TriviaQuestion incorrect = new TriviaQuestion("Question 2", choices, 2);
        TriviaQuestion unanswered = new TriviaQuestion("Question 3", choices, 2);
        correct.setSelectedIndex(2);
        incorrect.setSelectedIndex(0);

        assertEquals(1, TriviaQuestion.countCorrect(Arrays.asList(correct, incorrect, unanswered)));
        assertEquals("A", incorrect.getSelectedAnswer());
        assertEquals("C", incorrect.getCorrectAnswer());
    }
}
