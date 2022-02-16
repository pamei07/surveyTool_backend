package iks.surveytool.utils.builder;

import iks.surveytool.entities.Question;

public class QuestionBuilder {
    public Question createQuestion(Long id, String text, boolean required, boolean hasCheckbox) {
        Question newQuestion = new Question(text, required, hasCheckbox);
        newQuestion.setId(id);
        return newQuestion;
    }
}
