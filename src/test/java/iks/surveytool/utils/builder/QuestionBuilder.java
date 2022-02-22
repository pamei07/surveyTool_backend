package iks.surveytool.utils.builder;

import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionType;

public class QuestionBuilder {
    public Question createQuestion(Long id, String text, boolean required, QuestionType questionType) {
        Question newQuestion = new Question(text, required, questionType);
        newQuestion.setId(id);
        return newQuestion;
    }
}
